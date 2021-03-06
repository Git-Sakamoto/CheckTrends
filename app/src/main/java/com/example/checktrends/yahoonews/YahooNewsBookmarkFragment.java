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
import androidx.recyclerview.widget.DividerItemDecoration;
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
    List<Bookmark> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yahoo_news_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setBookmarkRecycler();
    }

    void setBookmarkRecycler(){
        list = new ArrayList<>();
        DBAdapter dbAdapter = new DBAdapter(getActivity());
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

        yahooBookmarkRecyclerAdapter = new YahooBookmarkRecyclerAdapter(this,list,getAlreadyReadList()){
            @Override
            void onItemClick(int position) {
                viewWebPage(position);
            }

            @Override
            void selectDeleteBookmark(int position) {
                openDeleteDialog(position);
            }
        };

        recyclerView.setAdapter(yahooBookmarkRecyclerAdapter);
    }

    void viewWebPage(int position){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(list.get(position).getUrl()));

        DBAdapter dbAdapter = new DBAdapter(getActivity());
        dbAdapter.insertAlreadyRead(list.get(position).getUrl());

        yahooBookmarkRecyclerAdapter.notifyItemChanged(position);
    }

    void openDeleteDialog(int position){
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

    void deleteBookmark(String id){
        DBAdapter dbAdapter = new DBAdapter(getActivity());
        dbAdapter.deleteBookmark(id);
        setBookmarkRecycler();
    }

    List<String> getAlreadyReadList(){
        List<String>result = new ArrayList<>();

        DBAdapter dbAdapter = new DBAdapter(getActivity());
        Cursor c = dbAdapter.selectAlreadyRead();
        if(c.moveToFirst()){
            do {
                result.add(c.getString(1));
            }while (c.moveToNext());
        }
        c.close();

        return result;
    }
}