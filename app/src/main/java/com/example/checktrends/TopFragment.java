package com.example.checktrends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TopFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String[]title = getResources().getStringArray(R.array.array_title);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, title);
        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String text = (String)listView.getItemAtPosition(position);
                if(text.equals(getString(R.string.yahoo_news_title))){
                    Navigation.findNavController(view).navigate(R.id.action_topFragment_to_yahooNewsFragment);
                }else if(text.equals(getString(R.string.twitter_title))){
                    Navigation.findNavController(view).navigate(R.id.action_topFragment_to_twitterFragment);
                }else if(text.equals(getString(R.string.google_trends_title))){
                    Navigation.findNavController(view).navigate(R.id.action_topFragment_to_googleTrendsFragment);
                }else if(text.equals(getString(R.string.book_mark_title))){
                    Navigation.findNavController(view).navigate(R.id.action_topFragment_to_bookMarkFragment);
                }
            }
        });
    }

}