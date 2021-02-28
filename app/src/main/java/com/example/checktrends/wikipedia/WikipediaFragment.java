package com.example.checktrends.wikipedia;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.checktrends.R;
import com.google.android.material.tabs.TabLayout;

public class WikipediaFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wikipedia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new HttpRequest(getActivity()){
            @Override
            void loadingComplete(WikipediaContent content) {
                setPagerAdapter(view,content);
            }
        }.execute();
    }

    void setPagerAdapter(View view,WikipediaContent content){
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(),content);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }
}