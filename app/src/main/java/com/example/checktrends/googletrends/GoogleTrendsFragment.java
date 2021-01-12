package com.example.checktrends.googletrends;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checktrends.AnimationEndListener;
import com.example.checktrends.ImageGetTask;
import com.example.checktrends.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleTrendsFragment extends Fragment{

    ProgressDialog dialog;

    private final String URL = "https://trends.google.com/trends/api/dailytrends?geo=JP";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_trends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        dialog = new ProgressDialog(getActivity());
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
                        Toast.makeText(getActivity(),R.string.error_message_is_cannot_connect,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    LinkedHashMap<String, LinkedHashMap<String, News>> result = new LinkedHashMap<>();

                    final String jsonStr = response.body().string().replace(")]}',", "");
                    JSONObject json = new JSONObject(jsonStr);
                    JSONArray jsonTrendingSearchesDays = json.getJSONObject("default").getJSONArray("trendingSearchesDays");

                    for (int i = 0; i < jsonTrendingSearchesDays.length(); i++) {
                        LinkedHashMap<String, News> map = new LinkedHashMap<>();
                        //System.out.println(jsonTrendingSearchesDays.getJSONObject(i).getString("date"));
                        JSONArray jsonTrendingSearches = jsonTrendingSearchesDays.getJSONObject(i).getJSONArray("trendingSearches");
                        for (int j = 0; j < jsonTrendingSearches.length(); j++) {
                            News news = null;
                            //System.out.println(jsonTrendingSearches.getJSONObject(j).getJSONObject("title").getString("query"));
                            if (jsonTrendingSearches.getJSONObject(j).isNull("articles") == false) {
                                JSONArray articles = jsonTrendingSearches.getJSONObject(j).getJSONArray("articles");

                                //System.out.println(articles.getJSONObject(0).getString("title"));
                                //System.out.println(articles.getJSONObject(0).getString("timeAgo"));
                                //System.out.println(articles.getJSONObject(0).getString("source"));

                                if (articles.getJSONObject(0).isNull("image") == false) {
                                    //System.out.println(articles.getJSONObject(0).getJSONObject("image").getString("newsUrl"));
                                    //System.out.println(articles.getJSONObject(0).getJSONObject("image").getString("imageUrl"));
                                    news = new News(
                                            articles.getJSONObject(0).getString("title"),
                                            articles.getJSONObject(0).getString("timeAgo"),
                                            articles.getJSONObject(0).getString("source"),
                                            articles.getJSONObject(0).getJSONObject("image").getString("newsUrl"),
                                            articles.getJSONObject(0).getJSONObject("image").getString("imageUrl")
                                        );
                                } else {
                                    //System.out.println(articles.getJSONObject(0).getString("url"));
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
                                    jsonTrendingSearches.getJSONObject(j).getJSONObject("title").getString("query"),
                                    news
                            );
                        }
                        result.put(jsonTrendingSearchesDays.getJSONObject(i).getString("date"), map);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout linearLayout = view.findViewById(R.id.linearLayout);

                            for (String date : result.keySet()) {
                                TextView textDate = new TextView(getActivity());
                                textDate.setText(date.substring(0, 4) + "年" + date.substring(4, 6) + "月" + date.substring(6, 8) + "日");
                                textDate.setTextSize(20);
                                linearLayout.addView(textDate);

                                int rank = 1;
                                LinkedHashMap<String, News> titleMap = result.get(date);
                                for (String title : titleMap.keySet()) {
                                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.google_trends_result, null);

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
                                            startActivity(intent);
                                        }
                                    });

                                    LinearLayout layoutExpansion = view.findViewById(R.id.layout_expansion);
                                    ImageButton buttonExpansion = view.findViewById(R.id.button_expansion);
                                    buttonExpansion.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (layoutExpansion.getVisibility() == View.GONE) {
                                                layoutExpansion.setVisibility(View.VISIBLE);
                                            } else {
                                                layoutExpansion.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    News news = titleMap.get(title);
                                    TextView testText = view.findViewById(R.id.textView);
                                    testText.setText(news.getTitle());

                                    ImageView imageView = view.findViewById(R.id.imageView);
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
                                            customTabsIntent.launchUrl(getActivity(), Uri.parse(news.getNewsUrl()));
                                        }
                                    });

                                    linearLayout.addView(view);

                                    rank++;
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
            }

        });
    }
}