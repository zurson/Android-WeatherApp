package com.example.weatherapp.Fragments.DetailedInfoFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherapp.WeatherData.Weather;
import com.example.weatherapp.WeatherData.WeatherData;
import com.example.weatherapp.R;
import com.example.weatherapp.Utils.Settings;
import com.example.weatherapp.Interfaces.WeatherDataUpdater;

import org.json.JSONException;

public class VisibilityFragment extends DetailFragment implements WeatherDataUpdater {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void updateData() {
        try {
            Weather weather = WeatherData.getCurrentWeatherData();

            getImageView().setImageResource(R.drawable.visibility);
            getImageView().setVisibility(View.VISIBLE);

            getDescriptionTextView().setText("Visibility");

            getValueTextView().setText(weather.getVisibility() + " " + Settings.DISTANCE_UNIT);

        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }
    }

}
