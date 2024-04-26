package com.example.weatherapp.Utils;

import java.text.DecimalFormat;

public class Coordinates {

    private double lat;
    private double lon;

    public Coordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        format();
    }

    public Coordinates(Coordinates coordinates) {
        this.lat = coordinates.lat;
        this.lon = coordinates.lon;
        format();
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    private void format() {
        DecimalFormat df = new DecimalFormat("#.#####");
        this.lat = Double.parseDouble(df.format(this.lat).replace(",", "."));
        this.lon = Double.parseDouble(df.format(this.lon).replace(",", "."));
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
