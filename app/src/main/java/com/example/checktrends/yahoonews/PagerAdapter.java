package com.example.checktrends.yahoonews;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    private CharSequence[] tabTitles = {"アクセスランキング", "コメントランキング"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ViewNewsFragment(position);
                break;
                //return new ViewNewsFragment(position);
            case 1:
                fragment = new ViewNewsFragment(position);
                break;
                //return new ViewNewsFragment(position);
            default:
                fragment = new ViewNewsFragment(position);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

}
