package com.example.checktrends.yahoonews;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    void execute(){
        onPreExecute();

        httpRequest();
    }

    void onPreExecute() {
        progressBar.setVisibility(android.widget.ProgressBar.VISIBLE);
    }

    void onPostExecute() {
        progressBar.setVisibility(android.widget.ProgressBar.GONE);

        if(newsList.isEmpty()){
            Toast.makeText(context,R.string.error_message_is_cannot_connect,Toast.LENGTH_SHORT).show();
            return;
        }

        setRecyclerView();
    }

    boolean netWorkCheck(Context context){
        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if( info != null ){
            return info.isConnected();
        } else {
            return false;
        }
    }

    void httpRequest(){
        Interceptor onlineInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());
                int maxAge = 60; // read from cache for 60 seconds even if there is internet connection
                return response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            }
        };

        Interceptor offlineInterceptor= new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (netWorkCheck(context) == false) {
                    int maxStale = 60 * 30;
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
                return chain.proceed(request);
            }
        };

        final long cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(context.getCacheDir(), cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(offlineInterceptor)
                .addNetworkInterceptor(onlineInterceptor)
                .cache(cache)
                .build();

        okhttp3.Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                addNewsList(response.body().string());

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> onPostExecute());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
        });
    }

    void addNewsList(String response){
        String title,url,jpgUrl;

        Document document = Jsoup.parse(response);
        Elements elements = document.select("a.newsFeed_item_link");
        for (Element element : elements) {
            url = element.attr("href");
            title = element.select("div.newsFeed_item_title").text();
            String thumbnail = element.select("div.newsFeed_item_thumbnail").html().replace("\n","");
            jpgUrl = thumbnail.substring(thumbnail.indexOf("src=\"") + 5,thumbnail.indexOf("\"",thumbnail.indexOf("src=\"") + 5)).replace("&amp;", "&");
            newsList.add(new News(title, url, jpgUrl));
        }
    };

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
                viewNews(position);
            }

            @Override
            void registerBookmark(int position) {
                RecyclerManager.this.registerBookmark(position);
            }
        };
        recyclerView.setAdapter(yahooNewsRecyclerAdapter);
    }

    void viewNews(int position){
        //ニュースを表示、表示したニュースを既読テーブルに登録
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(newsList.get(position).getUrl()));

        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.insertAlreadyRead(newsList.get(position).getUrl());

        yahooNewsRecyclerAdapter.notifyItemChanged(position);
    }

    void registerBookmark(int position){
        DBAdapter dbAdapter = new DBAdapter(context);
        if(dbAdapter.insertBookmark(newsList.get(position).getTitle(),newsList.get(position).getUrl())){
            Toast.makeText(context,R.string.register_complete,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,R.string.already_registered,Toast.LENGTH_SHORT).show();
        }
    }

    void tabChange(){
        if(yahooNewsRecyclerAdapter != null){
            yahooNewsRecyclerAdapter.setAlreadyReadList(getAlreadyReadList());
            yahooNewsRecyclerAdapter.notifyDataSetChanged();
        }
    }

}
