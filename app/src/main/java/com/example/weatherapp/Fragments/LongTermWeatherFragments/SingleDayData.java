package com.example.weatherapp.Fragments.LongTermWeatherFragments;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.Enums.Units;
import com.example.weatherapp.WeatherData.Weather;
import com.example.weatherapp.Utils.GlobalUtilities;
import com.example.weatherapp.Utils.Settings;

import org.json.JSONException;

public class SingleDayData {

    private TextView dayOfWeekTextView;
    private ImageView weatherIconImageView;
    private TextView weatherDescriptionTextView;
    private TextView temperatureTextView;
    private final Context context;

    public SingleDayData(Context context, TextView dayOfWeekTextView, ImageView weatherIconImageView, TextView weatherDescriptionTextView, TextView temperatureTextView) {
        this.dayOfWeekTextView = dayOfWeekTextView;
        this.weatherIconImageView = weatherIconImageView;
        this.weatherDescriptionTextView = weatherDescriptionTextView;
        this.temperatureTextView = temperatureTextView;
        this.context = context;
    }

    public void updateData(Weather weather) {
        try {
            dayOfWeekTextView.setText(weather.getWeekDay());
//            dayOfWeekTextView.setText(shortWeekDay(weather.getWeekDay()));

            updateIcon(weather);
            weatherDescriptionTextView.setText(weather.getWeatherMainDescription());

            Units usingUnits = Settings.getUsingUnits();
            String degreeSymbol = usingUnits == Units.STANDARD ? "" : Settings.DEGREE_SYMBOL;
            temperatureTextView.setText(weather.getTemperature() + degreeSymbol + Settings.getTemperatureUnit());


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void updateIcon(Weather weather) throws JSONException {
        int iconId = GlobalUtilities.getResourceIdByName(context, Settings.WEATHER_ICONS_FOLDER_NAME,
                Settings.WEATHER_ICON_PREFIX + weather.getWeatherIcon());

        if (iconId != 0) {
            weatherIconImageView.setVisibility(View.VISIBLE);
            weatherIconImageView.setImageResource(iconId);
        }
        else
            weatherIconImageView.setVisibility(View.INVISIBLE);
    }

    private String shortWeekDay(String weekDay) {
        return weekDay.substring(0, Settings.WEEK_DAY_LETTERS);
    }

}
