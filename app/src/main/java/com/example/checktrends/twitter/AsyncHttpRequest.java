package com.example.checktrends.twitter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.checktrends.ResultListAdapter;
import com.example.checktrends.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import twitter4j.OEmbed;
import twitter4j.OEmbedRequest;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class AsyncHttpRequest{
    ListView listView;
    ProgressBar progressBar;
    Context context;
    Twitter twitter;
    Fragment fragment;
    List<String> result = new ArrayList<>();
    String title;

    public AsyncHttpRequest(Fragment fragment) {
        this.fragment = fragment;
        context = fragment.getActivity();
        listView = fragment.getView().findViewById(R.id.listView);
        progressBar = fragment.getView().findViewById(R.id.progressBar);
    }

    private class AsyncRunnable implements Runnable {
        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            try {
                twitter = new TwitterFactory().getInstance();
                twitter.getOAuth2Token();

                Trends trends;
                trends = twitter.getPlaceTrends(23424856);
                for (Trend trend : trends.getTrends()) {
                    title = trend.getName();
                    result.add(title);
                    //System.out.println(trend.getTweetVolume());
                }

                Map<String, RateLimitStatus> helpMap = twitter.help().getRateLimitStatus("trends");
                for(String key : helpMap.keySet()){
                    System.out.println(key);
                    RateLimitStatus rateLimitStatus = helpMap.get(key);

                    System.out.println(rateLimitStatus.getLimit());
                    System.out.println(rateLimitStatus.getRemaining());
                    System.out.println(rateLimitStatus.getResetTimeInSeconds());
                }

            } catch (TwitterException e) {
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

    void onPostExecute(List<String> result) {
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
                title = result.get(position);
                if(title.contains("#")){
                    intent.setData(Uri.parse("https://twitter.com/search?q=%23"+title.substring(1)));
                }else{
                    intent.setData(Uri.parse("https://twitter.com/search?q="+title));
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String title = ((TextView)view.findViewById(R.id.text_title)).getText().toString();
                openWebViewDialog(title);
                return true;
            }
        });
    }

    void openWebViewDialog(String title){
        progressBar.setVisibility(android.widget.ProgressBar.VISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Query query = new Query();
                    query.setQuery(title + " exclude:retweets min_faves:10");
                    //query.resultType(Query.MIXED);
                    query.setCount(100);
                    query.lang("ja");

                    QueryResult result;
                    Status displayTweet = null;
                    for(int searchPage = 1; searchPage < 5; searchPage++) {
                        result = twitter.search(query);
                        for (Status tweet : result.getTweets()) {
                            if (displayTweet == null || displayTweet.getRetweetCount() < tweet.getRetweetCount()) {
                                displayTweet = tweet;
                            }
                        }
                        query = result.nextQuery();
                        if(query == null)break;
                    }

                    if(displayTweet!=null){
                        OEmbedRequest oEmbedRequest = new OEmbedRequest(displayTweet.getId(),null);
                        oEmbedRequest.setHideMedia(false);
                        OEmbed oEmbed = twitter.getOEmbed(oEmbedRequest);

                        WebViewDialogFragment dialog = new WebViewDialogFragment(oEmbed);
                        dialog.show(fragment.getActivity().getSupportFragmentManager(),null);
                    }else{
                        Toast.makeText(context,R.string.error_message_is_cannot_connect,Toast.LENGTH_LONG).show();
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                /*} catch (NullPointerException e) {
                    e.printStackTrace();*/
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(android.widget.ProgressBar.GONE);
                    }
                });
            }
        }).start();
    }

}
