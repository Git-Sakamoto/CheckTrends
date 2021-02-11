package com.example.checktrends.twitter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.checktrends.R;

public class WebViewDialogFragment extends DialogFragment {
    WebView webView;

    private String html;

    /*
        読み込み中に表示するProgressDialogの表示、非表示を行うスクリプト
        widgets.jsの読み込みに合わせる必用がある
     */
    private final String script =
                    "<script type=\"text/javascript\">" +
                        " window.twttr = (function (d,s,id){ " +
                            " var t, js, fjs = d.getElementsByTagName(s)[0]; " +
                            " if (d.getElementById(id)) return; " +
                            " js=d.createElement(s); " +
                            " js.id=id; " +
                            " js.src=\"//platform.twitter.com/widgets.js\";" +
                            " fjs.parentNode.insertBefore(js, fjs); " +
                            " return window.twttr || (t = { _e: [], ready: function(f){ t._e.push(f) } }); " +
                        " }(document, \"script\", \"twitter-wjs\")); " +

                        " twttr.ready(function (twttr) { " +
                            " Android.showProgressDialog(); " +
                            " twttr.events.bind('loaded',function (event){ " +
                                " Android.hideProgressDialog(); " +
                            " }); "+
                        " }); " +

                    "</script>";

    WebViewDialogFragment(String html){
        this.html = html;
    }

    private ProgressDialog dialog = null;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View inputView = layoutInflater.inflate(R.layout.dialog_web_view, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inputView);
        builder.setPositiveButton("閉じる", null);
        AlertDialog alertDialog = builder.show();

        webView = inputView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.loadDataWithBaseURL(null, html + script, "text/html", "UTF-8", null);

        return  alertDialog;
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void showProgressDialog() {
            if (dialog == null || !dialog.isShowing()) {
                dialog = new ProgressDialog(getActivity());
                dialog.setTitle("読み込み中");
                dialog.show();
            }
        }

        @JavascriptInterface
        public void hideProgressDialog() {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }

    }
}
