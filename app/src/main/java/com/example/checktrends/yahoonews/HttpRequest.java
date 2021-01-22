package com.example.checktrends.yahoonews;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checktrends.R;
import com.example.checktrends.News;
import com.example.checktrends.RecyclerViewOnClick;
import com.example.checktrends.ResultRecyclerAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpRequest {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Context context;
    private String URL;

    public HttpRequest(Fragment fragment,String URL) {
        context = fragment.getActivity();
        recyclerView = fragment.getView().findViewById(R.id.recyclerView);
        progressBar = fragment.getView().findViewById(R.id.progressBar);

        this.URL = URL;
    }

    private class AsyncRunnable implements Runnable {
        List<News> result = new ArrayList<>();
        String title,url;

        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            try {
                Document document = Jsoup.connect(URL).get();
                Elements elements = document.select("a.newsFeed_item_link");
                for (Element element : elements) {
                    url = element.attr("href");
                    title = element.select("div.newsFeed_item_title").text();
                    result.add(new News(title, url));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(result);
                }
            });
        }
    }

    void onPreExecute() {
        progressBar.setVisibility(android.widget.ProgressBar.VISIBLE);
    }

    void execute(){
        onPreExecute();
        ExecutorService executorService  = Executors.newSingleThreadExecutor();
        executorService.submit(new AsyncRunnable());
    }

    void onPostExecute(List<News> result) {
        progressBar.setVisibility(android.widget.ProgressBar.GONE);

        if(result.isEmpty()){
            Toast.makeText(context,R.string.error_message_is_cannot_connect,Toast.LENGTH_LONG).show();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ResultRecyclerAdapter resultListAdapter = new ResultRecyclerAdapter(context, result, new RecyclerViewOnClick() {
            @Override
            public void onClick(Object object) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(((News) object).getUrl()));
            }

            @Override
            public void onLongClick(Object object) {
                //未実装
            }
        });
        recyclerView.setAdapter(resultListAdapter);
    }

}
