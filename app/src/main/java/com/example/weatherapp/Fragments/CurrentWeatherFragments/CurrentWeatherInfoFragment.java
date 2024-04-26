package com.example.weatherapp.Fragments.CurrentWeatherFragments;

import static com.example.weatherapp.Utils.Settings.HEIGHT_SYMBOL;
import static com.example.weatherapp.Utils.Settings.LENGTH_SYMBOL;
import static com.example.weatherapp.Utils.Settings.WEATHER_ICONS_FOLDER_NAME;
import static com.example.weatherapp.Utils.Settings.WEATHER_ICON_PREFIX;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.Enums.Units;
import com.example.weatherapp.WeatherData.Weather;
import com.example.weatherapp.WeatherData.WeatherData;
import com.example.weatherapp.R;
import com.example.weatherapp.Utils.GlobalUtilities;
import com.example.weatherapp.Utils.Settings;
import com.example.weatherapp.Interfaces.WeatherDataUpdater;

import org.json.JSONException;

public class CurrentWeatherInfoFragment extends Fragment implements WeatherDataUpdater {

    private TextView cityNameTextView, temperatureTextView, descriptionTextView;
    private TextView timeTextView, pressureTextView, coordinatesTextView;
    private ImageView weatherIcon1, weatherIcon2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.current_weather_info_fragment, container, false);

        findElements(view);
        clearData();

        return view;
    }

    private void findElements(View view) {
        cityNameTextView = view.findViewById(R.id.basic_weather_city_name_text_view);
        temperatureTextView = view.findViewById(R.id.basic_weather_temperature_text_view);
        descriptionTextView = view.findViewById(R.id.basic_weather_description_text_view);

        timeTextView = view.findViewById(R.id.basic_weather_time_text_view);
        pressureTextView = view.findViewById(R.id.basic_weather_pressure_text_view);
        coordinatesTextView = view.findViewById(R.id.basic_weather_coordinates_text_view);

        weatherIcon1 = view.findViewById(R.id.basic_weather_icon_1);
        weatherIcon2 = view.findViewById(R.id.basic_weather_icon_2);
    }

    private void clearData() {
        cityNameTextView.setText(Settings.NO_DATA_TEXT);
        temperatureTextView.setText(Settings.NO_DATA_TEXT);
        descriptionTextView.setText(Settings.NO_DATA_TEXT);

        timeTextView.setText(Settings.NO_DATA_TEXT);
        pressureTextView.setText(Settings.NO_DATA_TEXT);
        coordinatesTextView.setText(Settings.NO_DATA_TEXT);

        weatherIcon1.setVisibility(View.INVISIBLE);
        weatherIcon2.setVisibility(View.INVISIBLE);
    }


    @Override
    public void updateData() {

        try {
            Weather weather = WeatherData.getCurrentWeatherData();

            Units usingUnits = Settings.getUsingUnits();
            String degreeSymbol = usingUnits == Units.STANDARD ? "" : Settings.DEGREE_SYMBOL;

            cityNameTextView.setText(weather.getCityName());
            temperatureTextView.setText(weather.getTemperature() + degreeSymbol + Settings.getTemperatureUnit());
            descriptionTextView.setText(weather.getWeatherMainDescription());

            timeTextView.setText(weather.getLastUpdateTime());
            pressureTextView.setText(weather.getPressure() + " " + Settings.getPressureUnit());
            setCoordinatesTextView(weather);

            updateIcon(weather);

        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCoordinatesTextView(Weather weather) throws JSONException {
        double lat = weather.getCoordinates().getLat();
        double lon = weather.getCoordinates().getLon();

        StringBuilder sb = new StringBuilder();

        sb.append(HEIGHT_SYMBOL).append(lat).append(Settings.DEGREE_SYMBOL);
        sb.append('\n');
        sb.append(LENGTH_SYMBOL).append(lon).append(Settings.DEGREE_SYMBOL);

        coordinatesTextView.setText(sb.toString());
    }

    private void updateIcon(Weather weather) throws JSONException {
        int resId = GlobalUtilities.getResourceIdByName(getContext(), WEATHER_ICONS_FOLDER_NAME,
                WEATHER_ICON_PREFIX + weather.getWeatherIcon());

        if (resId != 0) {
            weatherIcon1.setVisibility(View.VISIBLE);
            weatherIcon1.setImageResource(resId);

            weatherIcon2.setVisibility(View.VISIBLE);
            weatherIcon2.setImageResource(resId);
        }
        else {
            weatherIcon1.setVisibility(View.INVISIBLE);
            weatherIcon2.setVisibility(View.INVISIBLE);
        }
    }

}
