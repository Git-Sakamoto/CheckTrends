package com.example.checktrends.bookmark;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.checktrends.EditTextManager;
import com.example.checktrends.R;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class UrlInputDialog extends DialogFragment {
    AlertDialog alertDialog;
    EditTextManager editTextManager;

    interface UrlInputDialogListener{
        void registrationComplete();
    }

    private UrlInputDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (UrlInputDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString() + "はインターフェースを実装していません");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        editTextManager = new EditTextManager(getActivity());

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View inputView = layoutInflater.inflate(R.layout.dialog_bookmark_input, null);

        EditText editTextUrl = inputView.findViewById(R.id.edit_url);
        editTextUrl.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_URI
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ブックマークの登録");
        builder.setView(inputView);
        builder.setPositiveButton("登録",null);
        builder.setNegativeButton("キャンセル", null);
        alertDialog = builder.show();

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBookmark(editTextUrl);
            }
        });

        return  alertDialog;
    }

    private void registerBookmark(EditText editTextUrl){
        String url;
        String title = null;

        if(editTextManager.isEmpty(editTextUrl) == false){
            url = editTextUrl.getText().toString();
        }else{
            return;
        }

        GetHtmlTitle getHtmlTitle = new GetHtmlTitle(url);
        FutureTask futureTask = new FutureTask(getHtmlTitle);
        Thread thread = new Thread(futureTask);
        thread.start();

        try {
            title = (String) futureTask.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(title != null){
            DBAdapter dbAdapter = new DBAdapter(getActivity());
            dbAdapter.openDB();
            dbAdapter.insertBookmark(title,url);
            dbAdapter.closeDB();
            System.out.println("タイトル：" + title);
            System.out.println("url：" + url);

            listener.registrationComplete();

            alertDialog.dismiss();

            Toast.makeText(getActivity(), R.string.register_complete, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), R.string.error_message_is_cannot_url_connect, Toast.LENGTH_SHORT).show();
        }
    }

}
