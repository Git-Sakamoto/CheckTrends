package com.example.checktrends.wikipedia;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.checktrends.CustomDatePicker;
import com.example.checktrends.R;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WikipediaFragment extends Fragment implements CustomDatePicker.CustomDatePickerListener{
    Calendar calendar = Calendar.getInstance();
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wikipedia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M月d日");
        String date = simpleDateFormat.format(new Date());
        new HttpRequest(getActivity(),date){
            @Override
            void loadingComplete(WikipediaContent content) {
                setPagerAdapter(view,content);
            }
        }.execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_wikipedia,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_past_trends:
                CustomDatePicker customDatePicker = new CustomDatePicker(calendar,null,null);
                customDatePicker.setTargetFragment(WikipediaFragment.this,0);
                customDatePicker.show(getParentFragmentManager(),"dialog");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        calendar.set(year, month - 1, day);
        System.out.println(month+"月"+day+"日");
        new HttpRequest(getActivity(),month+"月"+day+"日"){
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