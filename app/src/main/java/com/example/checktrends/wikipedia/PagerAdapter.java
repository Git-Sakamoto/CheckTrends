package com.example.checktrends.wikipedia;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    WikipediaContent content;

    private CharSequence[] tabTitles = {"できごと", "誕生日","記念日・年中行事"};

    public PagerAdapter(FragmentManager fm,WikipediaContent content) {
        super(fm);
        this.content = content;
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
                fragment = new WikipediaContentFragment(content.getEvents());
                break;
            case 1:
                fragment = new WikipediaContentFragment(content.getBirthdays());
                break;
            case 2:
                fragment = new WikipediaContentFragment(content.getAnniversaries());
                break;
            default:
                fragment = new WikipediaContentFragment(content.getEvents());
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
