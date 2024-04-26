package com.example.weatherapp.Fragments.LongTermWeatherFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.WeatherData.Weather;
import com.example.weatherapp.WeatherData.WeatherData;
import com.example.weatherapp.R;
import com.example.weatherapp.Utils.GlobalUtilities;
import com.example.weatherapp.Utils.Settings;
import com.example.weatherapp.Interfaces.WeatherDataUpdater;

import java.util.List;

public class LongTermWeatherFragment extends Fragment implements WeatherDataUpdater {

    private int dayOfWeekTextViewId, weatherIconImageViewId, weatherDescriptionTextViewId, temperatureTextViewId;
    private TextView dayOfWeekTextView;
    private ImageView weatherIconImageView;
    private TextView weatherDescriptionTextView;
    private TextView temperatureTextView;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.long_term_weather_fragment, container, false);

        clearData();

        return view;
    }

    @Override
    public void updateData() {
        List<Weather> weathers = WeatherData.getLongTermWeatherData();

        for (int i = 1; i <= Settings.LONG_TERM_WEATHER_DISPLAY_DAYS && i <= weathers.size(); i++) {
            findIds(i);
            findViews();

            SingleDayData singleDayData = new SingleDayData(getContext(), dayOfWeekTextView, weatherIconImageView,
                    weatherDescriptionTextView, temperatureTextView);

            singleDayData.updateData(weathers.get(i - 1));
        }

    }

    private String getIdWithoutLastChar(int id) {
        String name = getResources().getResourceName(id);
        String[] splitted = name.split("/");

        name = splitted[1].substring(0, splitted[1].length() - 1);
        return name;
    }

    private void clearData() {
        for (int i = 1; i <= Settings.LONG_TERM_WEATHER_DISPLAY_DAYS; i++) {
            findIds(i);
            findViews();

            dayOfWeekTextView.setText(Settings.NO_DATA_TEXT);
            weatherIconImageView.setVisibility(View.INVISIBLE);
            weatherDescriptionTextView.setText(Settings.NO_DATA_TEXT);
            temperatureTextView.setText(Settings.NO_DATA_TEXT);

        }
    }

    private void findIds(int pos) {
        dayOfWeekTextViewId = getId(getIdWithoutLastChar(R.id.long_term_weather_day_1), pos);
        weatherIconImageViewId = getId(getIdWithoutLastChar(R.id.long_term_weather_icon_day_1), pos);
        weatherDescriptionTextViewId = getId(getIdWithoutLastChar(R.id.long_term_weather_description_day_1), pos);
        temperatureTextViewId = getId(getIdWithoutLastChar(R.id.long_term_weather_temperature_day_1), pos);
    }

    private void findViews() {
        dayOfWeekTextView = view.findViewById(dayOfWeekTextViewId);
        weatherIconImageView = view.findViewById(weatherIconImageViewId);
        weatherDescriptionTextView = view.findViewById(weatherDescriptionTextViewId);
        temperatureTextView = view.findViewById(temperatureTextViewId);
    }

    private int getId(String idString, int position) {
        return GlobalUtilities.getResourceIdByName(getContext(), "id", idString + position);
    }

}
