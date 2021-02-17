package com.example.checktrends.googletrends;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checktrends.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class HttpRequest {
    private Fragment fragment;
    private ProgressDialog dialog;
    boolean specifyDate = false;

    private String URL = "https://trends.google.com/trends/api/dailytrends?geo=JP"; //JSON取得用

    HttpRequest(Fragment fragment,String date){
        this.fragment = fragment;

        if (date != null){
            specifyDate = true; //日付を選択（過去のトレンドを表示）して実行した
            URL = "https://trends.google.com/trends/api/dailytrends?geo=JP&ed=" + date;
        }
    }

    void execute(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        dialog = new ProgressDialog(fragment.getActivity());
        dialog.setTitle("読み込み中");
        dialog.show();

        Handler handler = new Handler(Looper.getMainLooper());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.post(() -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(fragment.getActivity(),R.string.error_message_is_cannot_connect,Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    List<Object> result = new ArrayList<>();

                    //JSONを取得した時に不要な文字列 )]}', が含まれているので、削除しないと正しく読み込むことができない
                    //全データ
                    JSONObject json = new JSONObject(response.body().string().replace(")]}',", ""));

                    //日付や、日付に対するトレンドの配列が格納された配列　日付の降順
                    JSONArray trendingSearchesDays = json.getJSONObject("default").getJSONArray("trendingSearchesDays");

                    //日付を指定してトレンドを表示する場合は、指定された日付1日分だけのトレンドを取得
                    //日付を指定していない（初期表示）の場合は、1回のリクエストで取得できる全てのトレンドを表示
                    for (int dateCount = 0; specifyDate ?  dateCount < 1 : dateCount < trendingSearchesDays.length(); dateCount++) {

                        //日付の格納
                        String date = trendingSearchesDays.getJSONObject(dateCount).getString("date");
                        result.add(date.substring(0, 4) + "年" + date.substring(4, 6) + "月" + date.substring(6, 8) + "日");

                        //トレンドの配列
                        JSONArray trendingSearches = trendingSearchesDays.getJSONObject(dateCount).getJSONArray("trendingSearches");

                        //トレンド名をキーに、関連ニュースが格納されたMapを作成する
                        for (int trendCount = 0; trendCount < trendingSearches.length(); trendCount++) {

                            List<News> newsList = new ArrayList<>();

                            //トレンドに対して関連ニュースが存在するか確認
                            if (trendingSearches.getJSONObject(trendCount).isNull("articles") == false) {
                                //トレンドに対する関連ニュースが格納された配列
                                JSONArray articles = trendingSearches.getJSONObject(trendCount).getJSONArray("articles");

                                newsList = getNewsList(articles);

                            }

                            result.add(new Trend(
                                    (trendCount + 1) + "．",
                                    trendingSearches.getJSONObject(trendCount).getJSONObject("title")
                                            .getString("query"),
                                    newsList)
                            );
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }

                            setRecyclerView(result);
                        }
                    });

                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        });
    }



    List<News> getNewsList(JSONArray articles) throws JSONException {
        List<News> newsList = new ArrayList<>();

        for(int newsCount = 0; newsCount < articles.length(); newsCount++){
            News news;

            //ニュースにサムネイルが用意されているか確認
            if (articles.getJSONObject(newsCount).isNull("image") == false) {
                //サムネイル付きのニュース
                news = new News(
                        articles.getJSONObject(newsCount).getString("title"),
                        articles.getJSONObject(newsCount).getString("timeAgo"),
                        articles.getJSONObject(newsCount).getString("source"),
                        articles.getJSONObject(newsCount).getJSONObject("image").getString("newsUrl"),
                        articles.getJSONObject(newsCount).getJSONObject("image").getString("imageUrl")
                );
            } else {
                //サムネイル無しのニュース
                news = new News(
                        articles.getJSONObject(newsCount).getString("title"),
                        articles.getJSONObject(newsCount).getString("timeAgo"),
                        articles.getJSONObject(newsCount).getString("source"),
                        articles.getJSONObject(newsCount).getString("url"),
                        null
                );
            }
            newsList.add(news);
        }

        return newsList;
    }

    void setRecyclerView(List<Object>result){
        RecyclerView recyclerView = fragment.getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
        GoogleTrendsRecyclerAdapter googleTrendsRecyclerAdapter = new GoogleTrendsRecyclerAdapter(fragment.getActivity(),result){
            @Override
            void clickSearchButton(String word){
                searchWord(word);
            }
        };
        recyclerView.setAdapter(googleTrendsRecyclerAdapter);
    }

    void searchWord(String word){
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, word);
        fragment.startActivity(intent);
    }
}
