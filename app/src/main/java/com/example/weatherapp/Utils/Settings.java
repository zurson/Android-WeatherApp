package com.example.weatherapp.Utils;

import static com.example.weatherapp.Enums.Units.IMPERIAL;
import static com.example.weatherapp.Enums.Units.METRIC;
import static com.example.weatherapp.Enums.Units.STANDARD;

import com.example.weatherapp.Enums.Units;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kotlin.Unit;

public final class Settings {
    private static final Lock lock = new ReentrantLock();


    public static final long AUTO_UPDATE_CHECK_TIME_IN_MS = 15_000; // 15 sec
    private static final int DATA_REFRESH_TIME_IN_MINUTES = 1;
    private static LocalDateTime LAST_UPDATE_TIME = LocalDateTime.now();
    private static String USING_LOCATION = "Lodz";
    private static Coordinates USING_COORDINATES = null; // new Coordinates(51.75, 19.46667) default: Łódź (Poland)
    private static Units USING_UNITS = METRIC;
    public static final String WEATHER_ICON_PREFIX = "icon_";
    public static final String DEGREE_SYMBOL = "°";
    private static final String PRESSURE_UNIT = "hPa";
    public static final String DISTANCE_UNIT = "km";
    public static final String HUMIDITY_UNIT = "%";
    public static final String NO_DATA_TEXT = "---";
    public static final String HEIGHT_SYMBOL = "H: ";
    public static final String LENGTH_SYMBOL = "L: ";
    public static final String WEATHER_ICONS_FOLDER_NAME = "drawable";
    public static final int LONG_TERM_WEATHER_DISPLAY_DAYS = 5;
    public static final int WEEK_DAY_LETTERS = 3;

    public static final String ALERT_DIALOG_BUTTON_TEXT = "CLOSE";
    public static final String DATA_UPDATE_TOAST_TEXT = "Weather data updated!";
    public static final String DATA_FETCH_DIALOG_TITLE = "Data fetching error!";
    public static final String DATA_FETCH_DIALOG_MESSAGE = "Cannot to fetch data from online server! Check your internet connection and refresh data manually!";

    public static final String NEW_FAVOURITE_LOCATION_ADD_TEXT = "[Add new location]";

    public static final String SETTINGS_FILENAME = "settings";
    public static final String LOCATIONS_USING_LOCATION_KEY = "using_location";
    public static final String LOCATIONS_SET_KEY = "favourite_locations";
    public static final String UNITS_KEY = "using_units";

    public static final String CURRENT_WEATHER_JSON_FILENAME_SUFFIX = "_current_weather_data";
    public static final String LONG_TERM_WEATHER_JSON_FILENAME_SUFFIX = "_long_term_weather_data";


    private static final HashMap<Units, String> TEMPERATURE_UNITS;
    static {
        TEMPERATURE_UNITS = new HashMap<>();
        TEMPERATURE_UNITS.put(METRIC, "C");
        TEMPERATURE_UNITS.put(IMPERIAL, "F");
        TEMPERATURE_UNITS.put(STANDARD, "K");
    }


    public static final Set<String> DEFAULT_LOCATIONS_SET;

    static {
        DEFAULT_LOCATIONS_SET = new HashSet<>();
        DEFAULT_LOCATIONS_SET.add(USING_LOCATION);
    }


    public static String getTemperatureUnit() {
        try {
            lock.lock();
            return TEMPERATURE_UNITS.get(USING_UNITS);
        } finally {
            lock.unlock();
        }
    }

    public static void setUsingCoordinates(Coordinates coordinates) {
        if (coordinates == null)
            return;

        lock.lock();
        USING_COORDINATES = coordinates;
        lock.unlock();
    }

    public static Coordinates getUsingCoordinates() {
        try {
            lock.lock();
            if (USING_COORDINATES != null)
                return new Coordinates(USING_COORDINATES);

            return null;
        } finally {
            lock.unlock();
        }
    }

    public static Units getUsingUnits() {
        try {
            lock.lock();
            return Units.valueOf(String.valueOf(USING_UNITS));
        } finally {
            lock.unlock();
        }
    }

    public static void setUsingUnits(Units units) {
        lock.lock();
        USING_UNITS = units;
        lock.unlock();
    }

    public static String getPressureUnit() {
        return PRESSURE_UNIT;
    }

    public static String getUsingLocation() {
        try {
            lock.lock();
            return USING_LOCATION;
        } finally {
            lock.unlock();
        }
    }

    public static void setUsingLocation(String location) {
        lock.lock();
        USING_LOCATION = location;
        lock.unlock();
    }

    public static String getSpeedUnit() {
        Units unit = getUsingUnits();
        return unit == METRIC || unit == STANDARD ? "km/h" : "mph";
    }


    public static void setLastUpdateTime() {
        lock.lock();
        LAST_UPDATE_TIME = LocalDateTime.now();
        lock.unlock();
    }

    public static LocalDateTime getLastUpdateTime() {
        try {
            lock.lock();
            return LocalDateTime.from(LAST_UPDATE_TIME);
        } finally {
            lock.unlock();
        }
    }

    public static boolean isTimeForUpdateData() {
        try {
            lock.lock();
            LocalDateTime currentTime = LocalDateTime.now();
            Duration duration = Duration.between(LAST_UPDATE_TIME, currentTime);
            return duration.toMinutes() >= DATA_REFRESH_TIME_IN_MINUTES;
        } finally {
            lock.unlock();
        }
    }

}
