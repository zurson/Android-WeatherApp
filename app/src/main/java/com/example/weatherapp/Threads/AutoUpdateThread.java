package com.example.weatherapp.Threads;

import static com.example.weatherapp.Utils.Settings.AUTO_UPDATE_CHECK_TIME_IN_MS;

import android.content.Context;
import android.os.Looper;

import com.example.weatherapp.Interfaces.WeatherDataUpdater;
import com.example.weatherapp.Utils.GlobalUtilities;
import com.example.weatherapp.Utils.Settings;

public class AutoUpdateThread extends Thread {

    private final WeatherDataUpdater weatherDataUpdater;

    public AutoUpdateThread(WeatherDataUpdater weatherDataUpdater) {
        this.weatherDataUpdater = weatherDataUpdater;
    }

    @Override
    public void run() {
        Looper.prepare();

        while (true) {

            try {
                if (!GlobalUtilities.isInternetConnected((Context) weatherDataUpdater))
                    continue;

                if (Settings.isTimeForUpdateData())
                    GlobalUtilities.startUpdateThread(weatherDataUpdater, false);

                Thread.sleep(AUTO_UPDATE_CHECK_TIME_IN_MS);

            } catch (InterruptedException e) {
                break;
            }

        }

    }

}
