package com.example.weatherapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.Interfaces.WeatherDataUpdater;
import com.example.weatherapp.R;

public class MainLayoutFragment extends Fragment implements WeatherDataUpdater {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.current_weather_info_layout, container, false);
        return view;
    }

    @Override
    public void updateData() {
        updateFragment(R.id.current_info_fragment);
        updateFragment(R.id.detailed_weather_info_fragment);
        updateFragment(R.id.long_term_weather_fragment);
    }


    private void updateFragment(int fragmentId) {
        Fragment fragment = getChildFragmentManager().findFragmentById(fragmentId);
        WeatherDataUpdater weatherDataUpdater = (WeatherDataUpdater) fragment;
        weatherDataUpdater.updateData();
    }

}
