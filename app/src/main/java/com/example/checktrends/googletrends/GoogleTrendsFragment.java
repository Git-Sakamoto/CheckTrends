package com.example.checktrends.googletrends;

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
import android.widget.ListView;

import com.example.checktrends.R;
import com.example.checktrends.ResultListAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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
                    JSONObject json2 = json.getJSONObject("default");

                    JSONArray prefecturesObject = json2.getJSONArray("trendingSearchesDays");
                    for(int i=0; i<prefecturesObject.length(); ++i) {
                        JSONArray trendingSearches = prefecturesObject.getJSONObject(i).getJSONArray("trendingSearches");
                        for(int j=0; j<trendingSearches.length(); ++j) {
                            System.out.println(trendingSearches.getJSONObject(j).getString("title"));
                        }
                    }

                    /*Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ResultListAdapter resultListAdapter = new ResultListAdapter(getActivity(),result);
                            ListView listView = view.findViewById(R.id.listView);
                            listView.setAdapter(resultListAdapter);
                        }
                    });*/


                }catch(Exception e){
                    Log.e("error",e.getMessage());
                }
            }

        });

    }
}