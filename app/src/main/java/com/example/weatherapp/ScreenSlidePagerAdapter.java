package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.weatherapp.Interfaces.WeatherDataUpdater;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList;

    public ScreenSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
        fragmentList = new ArrayList<>();
    }

    public void addNewFragment(Fragment fragment) {
        if (fragment == null)
            return;

        fragmentList.add(fragment);
    }

    public void updateFragments() {
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof WeatherDataUpdater && fragment.isAdded())
                ((WeatherDataUpdater) fragment).updateData();
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

}
