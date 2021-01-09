package com.example.checktrends.googletrends;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checktrends.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleTrendsFragment extends Fragment {

    List<String> result;

    private final String URL = "https://trends.google.com/trends/api/dailytrends?geo=JP";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_trends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
        Handler handler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("onFailure",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String jsonStr = response.body().string().replace(")]}',","");
                Log.d("jsonStr:",jsonStr);

                try{
                    JSONObject json = new JSONObject(jsonStr);
                    JSONObject jsonDefault = json.getJSONObject("default");

                    JSONArray jsonTrendingSearchesDays = jsonDefault.getJSONArray("trendingSearchesDays");
                    for(int i=0; i < jsonTrendingSearchesDays.length(); i++) {
                        result = new ArrayList<>();
                        String date = jsonTrendingSearchesDays.getJSONObject(i).getString("date");
                        //System.out.println(jsonTrendingSearchesDays.getJSONObject(i).getString("date"));

                        JSONArray jsonTrendingSearches = jsonTrendingSearchesDays.getJSONObject(i).getJSONArray("trendingSearches");
                        for(int j=0; j < jsonTrendingSearches.length(); j++) {
                            result.add(jsonTrendingSearches.getJSONObject(j).getJSONObject("title").getString("query"));
                            //System.out.println(jsonTrendingSearches.getJSONObject(j).getJSONObject("title").getString("query"));
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                TextView textDate = new TextView(getActivity());
                                textDate.setText(date.substring(0, 4) + "年" + date.substring(4, 6) + "月" + date.substring(6, 8) + "日");
                                textDate.setTextSize(20);
                                linearLayout.addView(textDate);

                                int rank = 1;
                                for(String title : result) {
                                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.google_trends_result,null);

                                    TextView textRank = view.findViewById(R.id.text_rank);
                                    textRank.setText(rank + "．");

                                    TextView textTitle = view.findViewById(R.id.text_title);
                                    textTitle.setText(title);

                                    ImageButton imageButton = view.findViewById(R.id.imageButton);
                                    imageButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                            intent.putExtra(SearchManager.QUERY,title);
                                            startActivity(intent);
                                        }
                                    });

                                    linearLayout.addView(view);
                                    rank++;
                                }
                            }
                        });
                    }

                }catch(Exception e){
                    Log.e("error",e.getMessage());
                }
            }

        });

    }
}