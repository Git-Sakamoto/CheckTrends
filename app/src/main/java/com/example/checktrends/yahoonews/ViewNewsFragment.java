package com.example.checktrends.yahoonews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.checktrends.R;

public class ViewNewsFragment extends Fragment {
    private String URL;
    RecyclerManager recyclerManager;

    public ViewNewsFragment(int position){
        if(position == 0){
            URL = "https://news.yahoo.co.jp/ranking/access/news";
        }else{
            URL = "https://news.yahoo.co.jp/ranking/comment";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerManager = new RecyclerManager(this,URL);
        recyclerManager.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerManager.tabChange();
    }
}