package com.example.checktrends.googletrends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.checktrends.CustomDatePicker;
import com.example.checktrends.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GoogleTrendsFragment extends Fragment implements CustomDatePicker.CustomDatePickerListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_trends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new HttpRequest(this,null).execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_google_trends,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_past_trends:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                //明日以降の日付を選択不可にする
                GregorianCalendar maxDate = new GregorianCalendar();
                maxDate.set(year, month, day);

                //Googleトレンドの急上昇ワードは29日前までのデータしか取得できないため、選択を制限する
                GregorianCalendar minDate = new GregorianCalendar();
                minDate.set(year, month, day - 29);

                CustomDatePicker customDatePicker = new CustomDatePicker(calendar,maxDate,minDate);
                customDatePicker.setTargetFragment(GoogleTrendsFragment.this,0);
                customDatePicker.show(getParentFragmentManager(),"dialog");

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        new HttpRequest(this,String.format("%d%02d%02d",year,month,day)).execute();
    }
}