package com.example.weatherapp.WeatherData;

import static com.example.weatherapp.Utils.Settings.DATA_UPDATE_TOAST_TEXT;

import android.content.Context;
import android.widget.Toast;

import com.example.weatherapp.Exceptions.ErrorCodeException;
import com.example.weatherapp.Exceptions.ParserException;
import com.example.weatherapp.Exceptions.WeatherDataException;
import com.example.weatherapp.Utils.FetchResult;
import com.example.weatherapp.Utils.JsonFileManager;
import com.example.weatherapp.Utils.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WeatherData {
    private static final Lock lock = new ReentrantLock();
    private static final WeatherFetcher weatherFetcher = new WeatherFetcher();

    private static FetchResult fetchResult;


    private static final JsonFileManager jsonFileManager = new JsonFileManager();


    public static Weather getCurrentWeatherData() {
        try {
            lock.lock();
            return fetchResult.getCurrentWeather();
        } finally {
            lock.unlock();
        }
    }

    public static List<Weather> getLongTermWeatherData() {
        try {
            lock.lock();
            return fetchResult.getLongTermWeather();
        } finally {
            lock.unlock();
        }
    }

    public static void fetchData(Context context) throws IOException, JSONException, ErrorCodeException {
        try {
            lock.lock();
            fetchResult = weatherFetcher.fetchData();
            Toast.makeText(context, DATA_UPDATE_TOAST_TEXT, Toast.LENGTH_SHORT).show();
        } finally {
            lock.unlock();
        }
    }

    public static void saveDataToFiles(Context context) {
        if (context == null || fetchResult == null)
            return;

        String usingLocation = Settings.getUsingLocation();

        try {
            lock.lock();
            jsonFileManager.saveJsonObjectToFile(context, fetchResult.getCurrentWeatherJsonObject(), usingLocation + Settings.CURRENT_WEATHER_JSON_FILENAME_SUFFIX);
            jsonFileManager.saveJsonObjectToFile(context, fetchResult.getLongTermWeatherJsonObject(), usingLocation + Settings.LONG_TERM_WEATHER_JSON_FILENAME_SUFFIX);
        } finally {
            lock.unlock();
        }
    }

    public static void loadDataFromFiles(Context context) throws WeatherDataException {
        if (context == null)
            throw new WeatherDataException("Context is null");

        String usingLocation = Settings.getUsingLocation();

        try {
            lock.lock();
            JSONObject currentData = jsonFileManager.loadJsonObjectFromFile(context, usingLocation + Settings.CURRENT_WEATHER_JSON_FILENAME_SUFFIX);
            JSONObject longTermData = jsonFileManager.loadJsonObjectFromFile(context, usingLocation + Settings.LONG_TERM_WEATHER_JSON_FILENAME_SUFFIX);

            fetchResult = weatherFetcher.loadData(currentData, longTermData);

        } catch (JSONException | IOException | ParserException ignored) {
            throw new WeatherDataException("Unable to load data from device");
        } finally {
            lock.unlock();
        }
    }

    public static boolean locationExists(String location) {
        return weatherFetcher.locationExists(location);
    }

}
