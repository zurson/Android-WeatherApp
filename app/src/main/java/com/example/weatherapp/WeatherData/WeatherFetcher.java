package com.example.weatherapp.WeatherData;

import com.example.weatherapp.Exceptions.ErrorCodeException;
import com.example.weatherapp.Exceptions.ParserException;
import com.example.weatherapp.Utils.Coordinates;
import com.example.weatherapp.Utils.FetchResult;
import com.example.weatherapp.Utils.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WeatherFetcher {
    private static final int CODE_OK = 200;

    private static final String LONG_TERM_WEATHER_TIME = "12:00:00";

    private static final String API_KEY = "e19225f62c83997591df146ddca18124";
    private static final String CURRENT_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String LONG_TERM_API_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private final DateTimeFormatter dateTimeFormatter;


    public WeatherFetcher() {
        this.dateTimeFormatter = getDateFormatter();
    }


    private URL createUrl(String API_URL) throws IOException {
        Coordinates coordinates = Settings.getUsingCoordinates();
        String location = Settings.getUsingLocation();

        if (coordinates != null)
            return new URL(API_URL + "?lat=" + coordinates.getLat() + "&lon=" + coordinates.getLon() + "&units=" + Settings.getUsingUnits().toString().toLowerCase() + "&appid=" + API_KEY);
        else
            return new URL(API_URL + "?q=" + location + "&units=" + Settings.getUsingUnits().toString().toLowerCase() + "&appid=" + API_KEY);
    }

    private URL createUrlForLocation(String location) throws IOException {
        return new URL(CURRENT_API_URL + "?q=" + location + "&units=" + Settings.getUsingUnits().toString().toLowerCase() + "&appid=" + API_KEY);
    }

    private HttpURLConnection openGetRequestConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        return conn;
    }

    private String readData(URL url) throws IOException {
        Scanner scanner = new Scanner(url.openStream());
        StringBuilder jsonBuilder = new StringBuilder();
        while (scanner.hasNext()) jsonBuilder.append(scanner.nextLine());

        scanner.close();

        return jsonBuilder.toString();
    }

    private DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
    }

    private int getResponseCode(URL url) throws IOException {
        HttpURLConnection conn = openGetRequestConnection(url);
        return conn.getResponseCode();
    }

    private String getCurrentDayOfWeek() {
        LocalDate date = LocalDate.now();
        return date.format(dateTimeFormatter);
    }

    private String getDayOfWeek(String dateTimeString) {
        LocalDate date = LocalDate.parse(dateTimeString.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
        return date.format(dateTimeFormatter);
    }

    private List<Weather> getWeatherList(JSONObject jsonObject) throws JSONException {
        List<Weather> weatherConditionsList = new ArrayList<>(Settings.LONG_TERM_WEATHER_DISPLAY_DAYS);

        JSONArray weatherList = jsonObject.getJSONArray("list");
        JSONObject cityData = jsonObject.getJSONObject("city");

        for (int i = 0; i < weatherList.length(); i++) {
            JSONObject weatherObject = weatherList.getJSONObject(i);
            String dateTimeString = weatherObject.getString("dt_txt");

            if (!dateTimeString.endsWith(LONG_TERM_WEATHER_TIME)) continue;

            weatherConditionsList.add(new Weather(weatherObject, cityData, getDayOfWeek(dateTimeString)));
        }

        return weatherConditionsList;
    }


    private JSONObject fetchLongTermData() throws IOException, JSONException, ErrorCodeException {
        URL url = createUrl(LONG_TERM_API_URL);

        int code = getResponseCode(url);
        if (code != CODE_OK) throw new ErrorCodeException(String.valueOf(code));

        String json = readData(url);
        JSONObject jsonObject = new JSONObject(json);

        return jsonObject;
    }


    private JSONObject fetchCurrentData() throws IOException, JSONException, ErrorCodeException {
        URL url = createUrl(CURRENT_API_URL);

        int code = getResponseCode(url);
        if (code != CODE_OK) throw new ErrorCodeException(String.valueOf(code));

        String json = readData(url);
        JSONObject weatherData = new JSONObject(json);

        return weatherData;
    }


    public FetchResult fetchData() throws JSONException, IOException, ErrorCodeException {
        JSONObject longTermData = fetchLongTermData();
        JSONObject currentData = fetchCurrentData();

        Weather currentWeather = new Weather(currentData, null, getCurrentDayOfWeek());
        List<Weather> longTermWeatherList = getWeatherList(longTermData);

        return new FetchResult(currentWeather, longTermWeatherList, currentData, longTermData);
    }

    public FetchResult loadData(JSONObject currentData, JSONObject longTermData) throws JSONException, ParserException {
        if (currentData == null || longTermData == null)
            throw new ParserException("Provided null object");

        Weather currentWeather = new Weather(currentData, null, getCurrentDayOfWeek());
        List<Weather> longTermWeatherList = getWeatherList(longTermData);

        return new FetchResult(currentWeather, longTermWeatherList, currentData, longTermData);
    }

    public boolean locationExists(String location) {
        if (location == null || location.isEmpty()) return false;

        CompletableFuture<Boolean> asyncTask = CompletableFuture.supplyAsync(() -> {

            try {
                URL url = createUrlForLocation(location);
                return getResponseCode(url) == CODE_OK;
            } catch (IOException e) {
                return false;
            }

        });

        try {
            return asyncTask.get();
        } catch (ExecutionException | InterruptedException ignored) {
            return false;
        }

    }


//    System.out.println("Dzien tygodnia: " + weather.getWeekDay());
//
//    System.out.println("Temperatura: " + weather.getTemperature() + " °C");
//    System.out.println("Temperatura odczuwalna: " + weather.getFeelsLikeTemperature() + " °C");
//    System.out.println("Temperatura minimalna: " + weather.getMinTemperature() + " °C");
//    System.out.println("Temperatura maksymalna: " + weather.getMaxTemperature() + " °C");
//    System.out.println("Ciśnienie: " + weather.getPressure() + " hPa");
//    System.out.println("Wilgotność: " + weather.getHumidity() + " %");
//    System.out.println("Widoczność: " + weather.getVisibility() + " metrów");
//    System.out.println("Prędkość wiatru: " + weather.getWindSpeed() + " km/h");
//    System.out.println("Kierunek wiatru: " + weather.getWindDirection() + " stopni");
//    System.out.println("Opady deszczu (ostatnia godzina): " + weather.getRainVolumeLastHour() + " mm");
//    System.out.println("Zachmurzenie: " + weather.getCloudiness() + " %");
//
//    System.out.println("Nazwa miasta: " + weather.getCityName());
//    System.out.println("ID miasta: " + weather.getCityID());
//
//    System.out.println("ID pogody: " + weather.getWeatherID());
//    System.out.println("Główny opis pogody: " + weather.getWeatherMainDescription());
//    System.out.println("Szczegółowy opis pogody: " + weather.getWeatherDetailedDescription());
//    System.out.println("Ikona pogody: " + weather.getWeatherIcon());
//    System.out.println("DT: " + weather.getDT());
//    System.out.println("Strefa czasowa: " + weather.getTimezone());

}
