package com.example.weatherapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.weatherapp.Enums.Units;
import com.example.weatherapp.Exceptions.PreferencesNoDataFoundException;
import com.example.weatherapp.Fragments.MainLayoutFragment;
import com.example.weatherapp.Fragments.SettingsLayoutFragment;
import com.example.weatherapp.Interfaces.WeatherDataUpdater;
import com.example.weatherapp.Threads.AutoUpdateThread;
import com.example.weatherapp.Utils.GlobalUtilities;
import com.example.weatherapp.Utils.Settings;
import com.example.weatherapp.Utils.SharedPreferencesManager;

import java.io.Serializable;

public class ScreenSlidePagerActivity extends FragmentActivity implements WeatherDataUpdater, Serializable {
    private static final String WAS_PREVIOUS_START = "PREV_START_FLAG";
    private transient SharedPreferencesManager preferencesManager;

    private transient AutoUpdateThread autoUpdateThread;

    private transient ScreenSlidePagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_slide_layout);

        ViewPager2 viewPager = getViewPager2();
        adapter = new ScreenSlidePagerAdapter(this);
        viewPager.setSaveFromParentEnabled(false);
        viewPager.setAdapter(adapter);

        preferencesManager = initializePreferencesManager(Settings.SETTINGS_FILENAME);
        loadUsingLocation();
        loadUsingUnits();

        addFragmentsToAdapter();

        if (savedInstanceState == null || !savedInstanceState.getBoolean(WAS_PREVIOUS_START))
            GlobalUtilities.startUpdateThread(this, false);
        else {
            GlobalUtilities.startUpdateThread(this, true);
        }
    }


    @Override
    public void updateData() {
        runOnUiThread(() -> {
            adapter.updateFragments();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        autoUpdateThread.interrupt();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAutoUpdateThread();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(WAS_PREVIOUS_START, true);
    }

    private void startAutoUpdateThread() {
        autoUpdateThread = new AutoUpdateThread(this);
        autoUpdateThread.setDaemon(true);
        autoUpdateThread.start();
    }

    private ViewPager2 getViewPager2() {
        return findViewById(R.id.pager);
    }

    private void addFragmentsToAdapter() {
        adapter.addNewFragment(new MainLayoutFragment());
        adapter.addNewFragment(prepareSettingsFragment());
    }

    private SharedPreferencesManager initializePreferencesManager(String filename) {
        return new SharedPreferencesManager(this, filename);
    }

    private void loadUsingLocation() {
        try {
            String usingLocation = preferencesManager.getString(Settings.LOCATIONS_USING_LOCATION_KEY);
            Settings.setUsingLocation(usingLocation);
        } catch (PreferencesNoDataFoundException ignored) {
        }
    }

    private void loadUsingUnits() {
        try {
            Units unit = Units.valueOf(preferencesManager.getString(Settings.UNITS_KEY));
            Settings.setUsingUnits(unit);
        } catch (PreferencesNoDataFoundException | IllegalArgumentException ignored) {
        }
    }

    private SettingsLayoutFragment prepareSettingsFragment() {
        SettingsLayoutFragment settingsFragment = new SettingsLayoutFragment();
        Bundle args = new Bundle();
        WeatherDataUpdater weatherDataUpdater = this;
        args.putSerializable("weatherDataUpdater", (Serializable) weatherDataUpdater);
        settingsFragment.setArguments(args);

        return settingsFragment;
    }

}