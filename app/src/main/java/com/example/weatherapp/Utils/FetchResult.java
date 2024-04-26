package com.example.weatherapp.Utils;

import com.example.weatherapp.WeatherData.Weather;

import org.json.JSONObject;

import java.util.List;

public class FetchResult {

    private final Weather currentWeather;
    private final List<Weather> longTermWeather;

    private final JSONObject currentWeatherJsonObject, longTermWeatherJsonObject;

    public FetchResult(Weather currentWeather, List<Weather> longTermWeather, JSONObject currentWeatherJsonObject, JSONObject longTermWeatherJsonObject) {
        this.currentWeather = currentWeather;
        this.longTermWeather = longTermWeather;
        this.currentWeatherJsonObject = currentWeatherJsonObject;
        this.longTermWeatherJsonObject = longTermWeatherJsonObject;
    }

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public List<Weather> getLongTermWeather() {
        return longTermWeather;
    }

    public JSONObject getCurrentWeatherJsonObject() {
        return currentWeatherJsonObject;
    }

    public JSONObject getLongTermWeatherJsonObject() {
        return longTermWeatherJsonObject;
    }

}
