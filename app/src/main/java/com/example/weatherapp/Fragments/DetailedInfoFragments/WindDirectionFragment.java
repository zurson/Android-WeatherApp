package com.example.weatherapp.Fragments.DetailedInfoFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherapp.WeatherData.Weather;
import com.example.weatherapp.WeatherData.WeatherData;
import com.example.weatherapp.R;
import com.example.weatherapp.Interfaces.WeatherDataUpdater;

import org.json.JSONException;

public class WindDirectionFragment extends DetailFragment implements WeatherDataUpdater {

    private static final String[] WIND_DIRECTIONS = {"North", "North-East", "East", "South-East", "South", "South-West", "West", "North-West"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void updateData() {
        try {
            Weather weather = WeatherData.getCurrentWeatherData();

            getImageView().setImageResource(R.drawable.wind_direction);
            getImageView().setVisibility(View.VISIBLE);

            getDescriptionTextView().setText("Wind direction");

            setWindDirection(weather.getWindDirection());


        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setWindDirection(int windDirection) {
        int index = (int) Math.round((((double) windDirection % 360) / 45)) % 8;
        getValueTextView().setText(WIND_DIRECTIONS[index]);
    }

}
