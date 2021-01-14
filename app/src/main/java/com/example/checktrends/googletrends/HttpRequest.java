package com.example.checktrends.googletrends;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.checktrends.ImageGetTask;
import com.example.checktrends.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {
    private Fragment fragment;
    private ProgressDialog dialog;

    private final String URL = "https://trends.google.com/trends/api/dailytrends?geo=JP"; //JSON取得用

    HttpRequest(Fragment fragment){
        this.fragment = fragment;
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Toast.makeText(fragment.getActivity(),R.string.error_message_is_cannot_connect,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    LinkedHashMap<String, LinkedHashMap<String, News>> result = new LinkedHashMap<>();

                    //JSONを取得した時に不要な文字列 )]}', が含まれているので、削除しないと正しく読み込むことができない
                    JSONObject json = new JSONObject(response.body().string().replace(")]}',", "")); //全データ
                    JSONArray trendingSearchesDays = json.getJSONObject("default").getJSONArray("trendingSearchesDays"); //日付や、日付に対するトレンドの配列が格納された配列　日付の降順

                    //日付をキーに、トレンド名と関連ニュースが格納されたMapを作成する
                    for (int i = 0; i < trendingSearchesDays.length(); i++) {
                        LinkedHashMap<String, News> map = new LinkedHashMap<>();

                        JSONArray trendingSearches = trendingSearchesDays.getJSONObject(i).getJSONArray("trendingSearches"); //トレンドの配列

                        //トレンド名をキーに、関連ニュースが格納されたMapを作成する
                        for (int j = 0; j < trendingSearches.length(); j++) {
                            News news = null;
                            //トレンドに対して関連ニュースが存在するか確認
                            if (trendingSearches.getJSONObject(j).isNull("articles") == false) {
                                JSONArray articles = trendingSearches.getJSONObject(j).getJSONArray("articles"); //トレンドに対する関連ニュースが格納された配列

                                //ニュースにサムネイルが用意されているか確認
                                if (articles.getJSONObject(0).isNull("image") == false) {
                                    news = new News(
                                            articles.getJSONObject(0).getString("title"),
                                            articles.getJSONObject(0).getString("timeAgo"),
                                            articles.getJSONObject(0).getString("source"),
                                            articles.getJSONObject(0).getJSONObject("image").getString("newsUrl"),
                                            articles.getJSONObject(0).getJSONObject("image").getString("imageUrl")
                                    );
                                } else {
                                    news = new News(
                                            articles.getJSONObject(0).getString("title"),
                                            articles.getJSONObject(0).getString("timeAgo"),
                                            articles.getJSONObject(0).getString("source"),
                                            articles.getJSONObject(0).getString("url"),
                                            null
                                    );
                                }
                            }
                            map.put(
                                    trendingSearches.getJSONObject(j).getJSONObject("title").getString("query"), //トレンド名
                                    news
                            );
                        }
                        result.put(trendingSearchesDays.getJSONObject(i).getString("date"), map);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout linearLayout = fragment.getView().findViewById(R.id.linearLayout);

                            for (String date : result.keySet()) {
                                TextView textDate = new TextView(fragment.getActivity());
                                textDate.setText(date.substring(0, 4) + "年" + date.substring(4, 6) + "月" + date.substring(6, 8) + "日");
                                textDate.setTextSize(20);
                                linearLayout.addView(textDate);

                                int rank = 1;
                                LinkedHashMap<String, News> titleMap = result.get(date);
                                for (String title : titleMap.keySet()) {
                                    View view = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.google_trends_result, null);

                                    TextView textRank = view.findViewById(R.id.text_rank);
                                    textRank.setText(rank + "．");

                                    TextView textTitle = view.findViewById(R.id.text_title);
                                    textTitle.setText(title);

                                    ImageButton buttonSearch = view.findViewById(R.id.button_search);
                                    buttonSearch.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                            intent.putExtra(SearchManager.QUERY, title);
                                            fragment.startActivity(intent);
                                        }
                                    });

                                    LinearLayout layoutExpansion = view.findViewById(R.id.layout_expansion);
                                    ImageButton buttonExpansion = view.findViewById(R.id.button_expansion);
                                    buttonExpansion.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (layoutExpansion.getVisibility() == View.GONE) {
                                                buttonExpansion.setBackgroundResource(R.drawable.icon_tenkai_up_arrow);
                                                layoutExpansion.setVisibility(View.VISIBLE);
                                            } else {
                                                buttonExpansion.setBackgroundResource(R.drawable.icon_tenkai_down_arrow);
                                                layoutExpansion.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    News news = titleMap.get(title);

                                    TextView textNewsTitle = view.findViewById(R.id.text_news_title);
                                    textNewsTitle.setText(news.getTitle());

                                    TextView textNewsSource = view.findViewById(R.id.text_news_source);
                                    textNewsSource.setText(news.getSource() + "　" + news.getTimeAgo().substring(0, 1) + "時間前");

                                    ImageView imageView = view.findViewById(R.id.image_news_photo);
                                    if (TextUtils.isEmpty(news.getImageUrl()) == false) {
                                        new ImageGetTask(imageView).execute(news.getImageUrl());
                                    } else {
                                        imageView.setVisibility(View.GONE);
                                    }

                                    CardView cardView = view.findViewById(R.id.cardView);
                                    cardView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                            CustomTabsIntent customTabsIntent = builder.build();
                                            customTabsIntent.launchUrl(fragment.getActivity(), Uri.parse(news.getNewsUrl()));
                                        }
                                    });

                                    linearLayout.addView(view);

                                    rank++;
                                }
                            }
                        }
                    });

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        });
    }
}
