package com.example.checktrends.twitter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.checktrends.R;

import twitter4j.OEmbed;

public class WebViewDialogFragment extends DialogFragment {
    OEmbed oEmbed;
    WebViewDialogFragment(OEmbed oEmbed){
        this.oEmbed = oEmbed;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View inputView = layoutInflater.inflate(R.layout.dialog_web_view, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inputView);
        builder.setPositiveButton("閉じる", null);

        AlertDialog alertDialog = builder.show();
        WebView webView = inputView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL(null, oEmbed.getHtml(), "text/html", "UTF-8", null);
        return  alertDialog;
    }
}
