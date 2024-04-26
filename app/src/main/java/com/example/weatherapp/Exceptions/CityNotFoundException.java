package com.example.weatherapp.Exceptions;

public class CityNotFoundException extends Exception {

    public CityNotFoundException(String message) {
        super(message);
    }

    public CityNotFoundException() {
        super();
    }

}
