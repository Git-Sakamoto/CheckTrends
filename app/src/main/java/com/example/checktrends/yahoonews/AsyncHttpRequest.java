package com.example.checktrends.yahoonews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.checktrends.R;
import com.example.checktrends.News;
import com.example.checktrends.ResultListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpRequest {
    ListView listView;
    ProgressBar progressBar;
    Context context;
    private final String SITE_URL = "https://news.yahoo.co.jp/ranking/access/news";

    public AsyncHttpRequest(Fragment fragment) {
        context = fragment.getActivity();
        listView = fragment.getView().findViewById(R.id.listView);
        progressBar = fragment.getView().findViewById(R.id.progressBar);
    }

    private class AsyncRunnable implements Runnable {
        List<News> result = new ArrayList<>();
        String title,url;

        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            try {
                Document document = Jsoup.connect(SITE_URL).get();
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

        ResultListAdapter resultListAdapter = new ResultListAdapter(context,result);
        listView.setAdapter(resultListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(Uri.parse(result.get(position).getUrl()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

}
