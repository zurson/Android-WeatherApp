package com.example.weatherapp.Fragments.DetailedInfoFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.Interfaces.WeatherDataUpdater;

public class DetailedWeatherInfoFragment extends Fragment implements WeatherDataUpdater {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detailed_weather_info_fragment, container, false);

        return view;
    }

    @Override
    public void updateData() {

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.wind_speed_fragment);
        WeatherDataUpdater weatherDataUpdater = (WeatherDataUpdater) fragment;
        weatherDataUpdater.updateData();

        fragment = getChildFragmentManager().findFragmentById(R.id.wind_direction_fragment);
        weatherDataUpdater = (WeatherDataUpdater) fragment;
        weatherDataUpdater.updateData();

        fragment = getChildFragmentManager().findFragmentById(R.id.humidity_fragment);
        weatherDataUpdater = (WeatherDataUpdater) fragment;
        weatherDataUpdater.updateData();

        fragment = getChildFragmentManager().findFragmentById(R.id.visibility_fragment);
        weatherDataUpdater = (WeatherDataUpdater) fragment;
        weatherDataUpdater.updateData();

    }

}
