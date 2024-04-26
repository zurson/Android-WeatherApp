package com.example.weatherapp.Threads;

import static com.example.weatherapp.Utils.Settings.DATA_FETCH_DIALOG_MESSAGE;
import static com.example.weatherapp.Utils.Settings.DATA_FETCH_DIALOG_TITLE;

import android.content.Context;
import android.os.Looper;

import com.example.weatherapp.Exceptions.ErrorCodeException;
import com.example.weatherapp.Exceptions.WeatherDataException;
import com.example.weatherapp.Interfaces.WeatherDataUpdater;
import com.example.weatherapp.Utils.GlobalUtilities;
import com.example.weatherapp.Utils.Settings;
import com.example.weatherapp.WeatherData.WeatherData;

import org.json.JSONException;

import java.io.IOException;

public class WeatherDataUpdaterThread extends Thread {

    private final WeatherDataUpdater weatherDataUpdater;
    private final Context context;
    private final boolean fromDevice;

    public WeatherDataUpdaterThread(WeatherDataUpdater weatherDataUpdater, boolean fromDevice) {
        this.weatherDataUpdater = weatherDataUpdater;
        this.context = (Context) weatherDataUpdater;
        this.fromDevice = fromDevice;
    }

    @Override
    public void run() {
        Looper.prepare();

        try {

            if (fromDevice) {
                loadDataFromDevice(false);
                return;
            }

            if (isInternetConnection()) {
                WeatherData.fetchData(context);
                weatherDataUpdater.updateData();
                WeatherData.saveDataToFiles(context);
                Settings.setLastUpdateTime();
                return;
            }

            loadDataFromDevice(true);

        } catch (JSONException | ErrorCodeException | IOException e) {
            GlobalUtilities.showAlertDialog(context, DATA_FETCH_DIALOG_TITLE, DATA_FETCH_DIALOG_MESSAGE);
        } catch (WeatherDataException ignored) {
            GlobalUtilities.startUpdateThread(weatherDataUpdater, false);
        }

    }


    private void updateFromInternet() {

    }

    private boolean isInternetConnection() {
        return GlobalUtilities.isInternetConnected(context);
    }

    private void loadDataFromDevice(boolean withDialog) throws IOException, WeatherDataException {
        try {

            WeatherData.loadDataFromFiles(context);
            weatherDataUpdater.updateData();

            if (withDialog)
                GlobalUtilities.showAlertDialog(context, "Alert",
                        "Due to no internet connection data has been loaded from device. " +
                                "Displayed weather forecast could be outdated!");

        } catch (WeatherDataException e) {
            if (withDialog)
                throw new IOException("No internet connection");
            else
                throw new WeatherDataException("No data on device!");
        }
    }

}
