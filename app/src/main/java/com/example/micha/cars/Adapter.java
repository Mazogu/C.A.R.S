package com.example.micha.cars;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by micha on 11/7/2016.
 */

public class Adapter extends FragmentPagerAdapter {
    public Adapter(FragmentManager manager){
        super(manager);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt("Whatever", position + 1);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public int getCount() {
        return 10;
    }

}
