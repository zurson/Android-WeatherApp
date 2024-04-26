package com.example.weatherapp.WeatherData;

import com.example.weatherapp.Enums.Units;
import com.example.weatherapp.Utils.Coordinates;
import com.example.weatherapp.Utils.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class Weather {

    private String weekDay;
    private JSONObject weatherData, cityData;

    public Weather(JSONObject weatherData, JSONObject cityData, String weekDay) {
        this.weatherData = weatherData;
        this.cityData = cityData;
        this.weekDay = weekDay;
    }

    private double convertMetersPerSecondToKilometersPerHour(double speedInMetersPerSecond) {
        return speedInMetersPerSecond * 3.6;
    }

    public String getWeekDay() {
        return weekDay;
    }

    // WEATHER DATA

    public String getTemperature() throws JSONException {
        double temperature = Math.round(weatherData.getJSONObject("main").getDouble("temp"));

        String temp = String.valueOf(temperature);
        if (temp.endsWith(".0"))
            temp = temp.substring(0, temp.length() - 2);

        return temp;
    }

    public String getFeelsLikeTemperature() throws JSONException {
        double temperature = Math.round(weatherData.getJSONObject("main").getDouble("feels_like"));
        String temp = String.valueOf(temperature);
        if (temp.endsWith(".0"))
            temp = temp.substring(0, temp.length() - 2);
        return temp;
    }

    public String getMinTemperature() throws JSONException {
        double temperature = Math.round(weatherData.getJSONObject("main").getDouble("temp_min"));
        String temp = String.valueOf(temperature);
        if (temp.endsWith(".0"))
            temp = temp.substring(0, temp.length() - 2);
        return temp;
    }

    public String getMaxTemperature() throws JSONException {
        double temperature = Math.round(weatherData.getJSONObject("main").getDouble("temp_max"));
        String temp = String.valueOf(temperature);
        if (temp.endsWith(".0"))
            temp = temp.substring(0, temp.length() - 2);
        return temp;
    }

    public int getPressure() throws JSONException {
        return weatherData.getJSONObject("main").getInt("pressure");
    }

    public int getHumidity() throws JSONException {
        return weatherData.getJSONObject("main").getInt("humidity");
    }

    public float getVisibility() throws JSONException {
        float floatValue = (float) weatherData.getInt("visibility") / 1000;
        return Math.round(floatValue * 10.0f) / 10.0f;
    }

    public String getWindSpeed() throws JSONException {
        double windSpeed = weatherData.getJSONObject("wind").getDouble("speed");
        Units usingUnits = Settings.getUsingUnits();

        windSpeed = usingUnits == Units.METRIC || usingUnits == Units.STANDARD ? convertMetersPerSecondToKilometersPerHour(windSpeed) : windSpeed;
        String windSpeedText = String.valueOf(Math.round(windSpeed));

        if (windSpeedText.endsWith(".0"))
            windSpeedText = windSpeedText.substring(0, windSpeedText.length() - 2);

        return windSpeedText;
    }

    public int getWindDirection() throws JSONException {
        return weatherData.getJSONObject("wind").getInt("deg");
    }

    public double getRainVolumeLastHour() throws JSONException {
        if (weatherData.has("rain")) {
            try {
                return weatherData.getJSONObject("rain").getDouble("3h");
            } catch (JSONException e) {
                return weatherData.getJSONObject("rain").getDouble("1h");
            }
        }
        return 0.0;
    }

    public int getCloudiness() throws JSONException {
        return weatherData.getJSONObject("clouds").getInt("all");
    }

    public int getWeatherID() throws JSONException {
        return weatherData.getJSONArray("weather").getJSONObject(0).getInt("id");
    }

    public String getWeatherMainDescription() throws JSONException {
        return weatherData.getJSONArray("weather").getJSONObject(0).getString("main");
    }

    public String getWeatherDetailedDescription() throws JSONException {
        return weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
    }

    public String getWeatherIcon() throws JSONException {
        return weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");
    }

    public int getDT() throws JSONException {
        return weatherData.getInt("dt");
    }

    // CITY DATA

    public int getTimezone() throws JSONException {
        JSONObject jsonObject = cityData == null ? weatherData : cityData;
        return jsonObject.getInt("timezone");
    }

    public String getLastUpdateTime() throws JSONException {
        JSONObject jsonObject = cityData == null ? weatherData : cityData;

        long unixTime = jsonObject.getLong("dt");
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTime), ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getCityName() throws JSONException {
        JSONObject jsonObject = cityData == null ? weatherData : cityData;
        return jsonObject.getString("name");
    }

    public int getCityID() throws JSONException {
        JSONObject jsonObject = cityData == null ? weatherData : cityData;
        return jsonObject.getInt("id");
    }

    public Coordinates getCoordinates() throws JSONException {
        JSONObject jsonObject = cityData == null ? weatherData : cityData;

        jsonObject = jsonObject.getJSONObject("coord");

        double lat = jsonObject.getDouble("lat");
        double lon = jsonObject.getDouble("lon");

        return new Coordinates(lat, lon);
    }

}
