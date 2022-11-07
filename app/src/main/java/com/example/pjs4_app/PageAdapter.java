package com.example.pjs4_app;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private int numOfTabs;

    /**
     * Required constructor
     * @param fm
     * @param numOfTabs
     */
    public PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    /**
     * Gets the fragment at a certain position
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new ArchivesFragment();
            default:
                return null;
        }
    }

    /**
     * Gets the tablayout number of tabs
     * @return
     */
    @Override
    public int getCount() {
        return numOfTabs;
    }
}
