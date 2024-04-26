package com.example.weatherapp.Utils;

import static com.example.weatherapp.Utils.Settings.ALERT_DIALOG_BUTTON_TEXT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.Toast;

import com.example.weatherapp.Interfaces.WeatherDataUpdater;
import com.example.weatherapp.Threads.WeatherDataUpdaterThread;

public class GlobalUtilities {

    public static void startUpdateThread(WeatherDataUpdater weatherDataUpdater, boolean fromDevice) {
        WeatherDataUpdaterThread weatherDataUpdaterThread = new WeatherDataUpdaterThread(weatherDataUpdater, fromDevice);
        Thread thread = new Thread(weatherDataUpdaterThread);
        thread.setDaemon(true);
        thread.start();
    }

    public static void showAlertDialog(Context context, String title, String message) {
        if (context == null || title == null || message == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                ALERT_DIALOG_BUTTON_TEXT,
                (dialog, id) -> dialog.cancel());

        Activity activity = (Activity) context;

        activity.runOnUiThread(() -> {
            builder.create().show();
        });

    }


    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;
    }


    public static int getResourceIdByName(Context context, String folderName, String resourceName) {
        @SuppressLint("DiscouragedApi")
        int resId = context.getResources().getIdentifier(resourceName, folderName, context.getPackageName());

        return resId;
    }


    public static String formatWord(String word) {
        if (word == null || word.isEmpty())
            return "";

        String lowercaseWord = word.toLowerCase();
        return lowercaseWord.substring(0, 1).toUpperCase() + lowercaseWord.substring(1);
    }

    public static void showToast(Activity activity, String message) {
        if (activity == null || message == null || message.isEmpty())
            return;

        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

}
