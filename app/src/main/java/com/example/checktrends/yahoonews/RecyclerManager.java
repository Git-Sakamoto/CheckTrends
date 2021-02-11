package com.example.checktrends.yahoonews;

import android.content.Context;
import android.database.Cursor;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class RecyclerManager {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Context context;
    Fragment fragment;

    private String URL;
    private List<News> newsList;

    private YahooNewsRecyclerAdapter yahooNewsRecyclerAdapter;

    RecyclerManager(Fragment fragment, String URL) {
        this.fragment = fragment;
        context = fragment.getActivity();
        recyclerView = fragment.getView().findViewById(R.id.recyclerView);
        progressBar = fragment.getView().findViewById(R.id.progressBar);

        this.URL = URL;

        newsList = new ArrayList<>();
    }

    private class AsyncRunnable implements Runnable {
        String title,url,jpgUrl;

        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            try {
                Document document = Jsoup.connect(URL).get();
                Elements elements = document.select("a.newsFeed_item_link");
                for (Element element : elements) {
                    url = element.attr("href");
                    title = element.select("div.newsFeed_item_title").text();
                    String thumbnail = element.select("div.newsFeed_item_thumbnail").html().replace("\n","");
                    jpgUrl = thumbnail.substring(thumbnail.indexOf("src=\"") + 5,thumbnail.indexOf("\"",thumbnail.indexOf("src=\"") + 5)).replace("&amp;", "&");
                    newsList.add(new News(title, url, jpgUrl));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute();
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

    void onPostExecute() {
        progressBar.setVisibility(android.widget.ProgressBar.GONE);

        if(newsList.isEmpty()){
            Toast.makeText(context,R.string.error_message_is_cannot_connect,Toast.LENGTH_LONG).show();
            return;
        }

        setRecyclerView();
    }

    List<String> getAlreadyReadList(){
        List<String>result = new ArrayList<>();

        DBAdapter dbAdapter = new DBAdapter(context);
        Cursor c = dbAdapter.selectAlreadyRead();
        if(c.moveToFirst()){
            do {
                result.add(c.getString(1));
            }while (c.moveToNext());
        }
        c.close();

        return result;
    }

    void setRecyclerView(){
        List<String>alreadyReadList = getAlreadyReadList();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        yahooNewsRecyclerAdapter = new YahooNewsRecyclerAdapter(fragment,newsList,alreadyReadList){
            @Override
            void onItemClick(int position) {
                //ニュースを表示、表示したニュースを既読テーブルに登録
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(newsList.get(position).getUrl()));

                DBAdapter dbAdapter = new DBAdapter(context);
                dbAdapter.insertAlreadyRead(newsList.get(position).getUrl());

                yahooNewsRecyclerAdapter.notifyItemChanged(position);
            }

            @Override
            void registerBookmark(int position) {
                DBAdapter dbAdapter = new DBAdapter(context);
                if(dbAdapter.insertBookmark(newsList.get(position).getTitle(),newsList.get(position).getUrl())){
                    Toast.makeText(context,R.string.register_complete,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,R.string.already_registered,Toast.LENGTH_SHORT).show();
                }
            }
        };
        recyclerView.setAdapter(yahooNewsRecyclerAdapter);
    }

    void tabChange(){
        if(yahooNewsRecyclerAdapter != null){
            yahooNewsRecyclerAdapter.setAlreadyReadList(getAlreadyReadList());
            yahooNewsRecyclerAdapter.notifyDataSetChanged();
        }
    }

}
