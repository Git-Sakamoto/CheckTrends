package com.example.checktrends.yahoonews;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.checktrends.R;
import com.google.android.material.tabs.TabLayout;

public class YahooNewsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yahoo_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_yahoo_news,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_delete:
                openDeleteMenu();
                break;

            case R.id.menu_bookmark:
                Navigation.findNavController(getView()).navigate(R.id.action_yahooNewsFragment_to_yahooNewsBookmarkFragment);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void openDeleteMenu(){
        final String[] items = {"表示履歴を全て削除", "キャンセル"};
        new AlertDialog.Builder(getActivity())
                .setTitle("削除メニュー")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                deleteAlreadyRead();
                                break;
                        }
                    }
                })
                .show();
    }

    void deleteAlreadyRead(){
        new DBAdapter(getActivity()).deleteAlreadyRead();
        Toast.makeText(getActivity(),R.string.deletion_completed,Toast.LENGTH_SHORT).show();
    }

}