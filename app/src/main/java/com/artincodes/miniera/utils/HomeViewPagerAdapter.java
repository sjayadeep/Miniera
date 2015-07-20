package com.artincodes.miniera.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by jayadeep on 30/6/15.
 */
public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;


    public HomeViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {

        super(fragmentManager);
//        fragmentManager.saveFragmentInstanceState(fragments.get(0));

        this.fragments = fragments;

    }

    @Override

    public Fragment getItem(int position) {

        return this.fragments.get(position);

    }


    @Override

    public int getCount() {

        return this.fragments.size();

    }

}
