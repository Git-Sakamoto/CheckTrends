package com.example.checktrends.twitter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    String title;

    private final int WOEID = 23424856;
    private final int MAX_PAGE = 5;
    private final String FILTER = " exclude:retweets min_faves:10";


    public AsyncHttpRequest(Fragment fragment) {
        this.fragment = fragment;
        context = fragment.getActivity();
        listView = fragment.getView().findViewById(R.id.listView);
        progressBar = fragment.getView().findViewById(R.id.progressBar);
    }

    private class AsyncRunnable implements Runnable {
        List<String> result = new ArrayList<>();
        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            try {
                twitter = new TwitterFactory().getInstance();
                twitter.getOAuth2Token();

                Trends trends;
                trends = twitter.getPlaceTrends(WOEID);
                for (Trend trend : trends.getTrends()) {
                    title = trend.getName();
                    result.add(title);
                    //System.out.println(trend.getTweetVolume());
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
                if(title.startsWith("#")){
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
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //String title = ((TextView)view.findViewById(R.id.text_title)).getText().toString();
                title = result.get(position);
                openWebViewDialog(title);
                return true;
            }
        });
    }

    void openWebViewDialog(String title){
        progressBar.setVisibility(android.widget.ProgressBar.VISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            Status displayTweet = null;
            @Override
            public void run() {
                try {
                    if(netWorkCheck(context)) {
                        Query query = new Query();
                        query.setQuery(title + FILTER);
                        query.resultType(Query.POPULAR);
                        query.setCount(100);
                        query.lang("ja");

                        QueryResult result;
                        result = twitter.search(query);
                        for (Status tweet : result.getTweets()) {
                            if (displayTweet == null || displayTweet.getRetweetCount() < tweet.getRetweetCount()) {
                                displayTweet = tweet;
                            }
                        }

                        if (displayTweet == null) {
                            query.resultType(Query.MIXED);
                            for (int searchPage = 1; searchPage <= MAX_PAGE; searchPage++) {
                                System.out.println("現在のページ" + searchPage);
                                result = twitter.search(query);
                                for (Status tweet : result.getTweets()) {
                                    if (displayTweet == null || displayTweet.getRetweetCount() < tweet.getRetweetCount()) {
                                        displayTweet = tweet;
                                    }
                                }
                                query = result.nextQuery();
                                if (query == null) break;
                            }
                        }

                        if (displayTweet != null) {
                            OEmbedRequest oEmbedRequest = new OEmbedRequest(displayTweet.getId(), null);
                            oEmbedRequest.setHideMedia(false);
                            OEmbed oEmbed = twitter.getOEmbed(oEmbedRequest);

                            WebViewDialogFragment dialog = new WebViewDialogFragment(oEmbed.getHtml());
                            dialog.show(fragment.getActivity().getSupportFragmentManager(), null);
                        }
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }finally {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(ProgressBar.GONE);
                            if(displayTweet == null){
                                Toast.makeText(context, R.string.error_message_is_cannot_connect, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public static boolean netWorkCheck(Context context){
        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if( info != null ){
            return info.isConnected();
        } else {
            return false;
        }
    }

}
