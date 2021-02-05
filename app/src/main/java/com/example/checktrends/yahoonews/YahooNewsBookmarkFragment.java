package com.example.checktrends.yahoonews;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.checktrends.R;

import java.util.ArrayList;
import java.util.List;

public class YahooNewsBookmarkFragment extends Fragment {
    RecyclerView recyclerView;
    YahooBookmarkRecyclerAdapter yahooBookmarkRecyclerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yahoo_news_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setBookmarkRecycler();
    }

    void setBookmarkRecycler(){
        List<Bookmark> list = new ArrayList<>();
        DBAdapter dbAdapter = new DBAdapter(getActivity());
        dbAdapter.openDB();
        Cursor c = dbAdapter.selectBookmark();
        if(c.moveToFirst()){
            do {
                list.add(new Bookmark(
                        String.valueOf(c.getInt(0)),
                        c.getString(1),
                        c.getString(2)
                ));
            }while (c.moveToNext());
        }
        c.close();

        yahooBookmarkRecyclerAdapter = new YahooBookmarkRecyclerAdapter(this,list){
            @Override
            void onItemClick(int position) {
                viewWebPage(Uri.parse(list.get(position).getUrl()));
            }

            @Override
            void selectDeleteBookmark(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("ブックマークの削除");
                builder.setMessage(list.get(position).getTitle());
                builder.setPositiveButton("削除",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteBookmark(list.get(position).getId());
                    }
                });
                builder.setNegativeButton("キャンセル", null);
                builder.show();
            }
        };

        recyclerView.setAdapter(yahooBookmarkRecyclerAdapter);
    }

    void viewWebPage(Uri uri){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), uri);
    }

    void deleteBookmark(String id){
        DBAdapter dbAdapter = new DBAdapter(getActivity());
        dbAdapter.openDB();
        dbAdapter.deleteBookmark(id);
        dbAdapter.closeDB();
        setBookmarkRecycler();
    }
}