package com.example.checktrends.twitter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.checktrends.R;

public class WebViewDialogFragment extends DialogFragment {
    String html;

    WebViewDialogFragment(String html){
        this.html = html;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View inputView = layoutInflater.inflate(R.layout.dialog_web_view, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inputView);
        builder.setPositiveButton("閉じる", null);
        AlertDialog alertDialog = builder.show();

        WebView webView = inputView.findViewById(R.id.webView);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

        webView.setWebViewClient(new WebViewClient() {
            private ProgressDialog dialog = null;
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (dialog == null || !dialog.isShowing()) {
                    dialog = new ProgressDialog(getActivity());
                    dialog.setTitle("読み込み中");
                    dialog.show();
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });

        return  alertDialog;
    }
}
