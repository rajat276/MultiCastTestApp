package com.example.rajatjain.multicasttestapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.rajatjain.multicasttestapp.fragment.Reciever;
import com.example.rajatjain.multicasttestapp.fragment.Sender;

/**
 * Created by Rajat Jain on 16-08-2016.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter{


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment fragment2=new Reciever();
                return fragment2;
            case 1:
                Fragment fragment1=new Sender();
                return fragment1;
        }
        return null;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "receiver";
            case 1:
                return "sender";

        }
        return null;
    }
}
