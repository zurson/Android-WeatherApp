package com.example.weatherapp.Fragments;

import static com.example.weatherapp.Utils.GlobalUtilities.formatWord;
import static com.example.weatherapp.Utils.Settings.NEW_FAVOURITE_LOCATION_ADD_TEXT;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.Enums.Units;
import com.example.weatherapp.Exceptions.PreferencesNoDataFoundException;
import com.example.weatherapp.Interfaces.Cooldown;
import com.example.weatherapp.Interfaces.WeatherDataUpdater;
import com.example.weatherapp.R;
import com.example.weatherapp.Threads.CooldownResetThread;
import com.example.weatherapp.Utils.GlobalUtilities;
import com.example.weatherapp.Utils.JsonFileManager;
import com.example.weatherapp.Utils.Settings;
import com.example.weatherapp.Utils.SharedPreferencesManager;
import com.example.weatherapp.WeatherData.WeatherData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsLayoutFragment extends Fragment implements Cooldown {
    private static final long BUTTON_COOLDOWN_MS = 5000;


    private ArrayList<String> units;
    private AutoCompleteTextView unitsAcTextView;
    private ArrayAdapter<String> unitsAdapter;


    private ArrayList<String> locations;
    private AutoCompleteTextView locationAutoCompleteTextView;
    private ArrayAdapter<String> locationsAdapter;


    private SharedPreferencesManager preferencesManager;


    private Button refreshButton;
    private CooldownResetThread buttonCooldownThread;

    private WeatherDataUpdater weatherDataUpdater;


    public SettingsLayoutFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout, container, false);

        loadWeatherDataUpdater();

        this.preferencesManager = initializePreferencesManager(Settings.SETTINGS_FILENAME);

        initializeUnits(view);
        initializeLocations(view);
        initializeRefreshButton(view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (buttonCooldownThread != null)
            buttonCooldownThread.interrupt();
    }


    // Locations


    private void initializeLocations(View view) {
        this.locationAutoCompleteTextView = getLocationACTextView(view);
        this.locations = loadFavouriteLocations();

        setUsingLocationAsDisplayed();

        setupLocationsAdapter();
        initializeOnLocationChangeListener();
    }

    private void setupLocationsAdapter() {
        this.locationsAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, locations);
        this.locationAutoCompleteTextView.setAdapter(this.locationsAdapter);
    }

    private AutoCompleteTextView getLocationACTextView(View view) {
        return view.findViewById(R.id.location_auto_complete_text_view);
    }

    private ArrayList<String> loadFavouriteLocations() {
        ArrayList<String> list;

        try {
            list = preferencesManager.loadStringList(Settings.LOCATIONS_SET_KEY);
        } catch (PreferencesNoDataFoundException ignored) {
            list = new ArrayList<>(Settings.DEFAULT_LOCATIONS_SET);
        }

        if (!list.contains(NEW_FAVOURITE_LOCATION_ADD_TEXT))
            list.add(0, NEW_FAVOURITE_LOCATION_ADD_TEXT);

        list.remove(Settings.getUsingLocation());

        return list;
    }

    private void updateFavouriteLocationsList() {
        locations.clear();
        locations.addAll(loadFavouriteLocations());
        locationsAdapter.notifyDataSetChanged();
    }

    private void setUsingLocationAsDisplayed() {
        this.locationAutoCompleteTextView.setText(Settings.getUsingLocation(), false);
    }

    private void setNewUsingLocation(String location) {
        this.locationAutoCompleteTextView.setText(location, false);
        Settings.setUsingLocation(location);

        preferencesManager.saveString(Settings.LOCATIONS_USING_LOCATION_KEY, location);
        updateFavouriteLocationsList();

        GlobalUtilities.startUpdateThread(weatherDataUpdater, false);
    }

    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add new location");

        final EditText input = new EditText(getActivity());
        builder.setView(input);

        builder.setPositiveButton("ADD", (dialog, which) -> {
            String newLocation = input.getText().toString();
            if (!newLocation.isEmpty())
                addNewLocation(newLocation);
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void addNewLocation(String newLocation) {
        if (locations.contains(newLocation)) {
            GlobalUtilities.showAlertDialog(getActivity(), "Error", "Location already exists!");
            return;
        }

        newLocation = GlobalUtilities.formatWord(newLocation);


        if (!locationExists(newLocation)) {
            GlobalUtilities.showAlertDialog(getActivity(), "Error", "No data about location: " + newLocation);
            return;
        }

        locations.add(newLocation);
        locationAutoCompleteTextView.setText(newLocation, false);

        setNewUsingLocation(newLocation);
        saveLocationsList();
        updateFavouriteLocationsList();

        GlobalUtilities.showAlertDialog(getActivity(), "Favourite locations", "New favourite location: " + newLocation);
    }

    private boolean locationExists(String location) {
        return WeatherData.locationExists(location);
    }

    private void initializeOnLocationChangeListener() {
        locationAutoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedLocation = (String) parent.getItemAtPosition(position);

            if (selectedLocation.equals(NEW_FAVOURITE_LOCATION_ADD_TEXT))
                showAddLocationDialog();
            else
                showLocationManagerDialog(selectedLocation);

            setUsingLocationAsDisplayed();
        });
    }

    private void showLocationManagerDialog(String selectedLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Manage location");

        builder.setPositiveButton("SELECT", (dialog, which) -> {
            setNewUsingLocation(selectedLocation);
            GlobalUtilities.showAlertDialog(getContext(), "Location", String.format("Using location: %s", selectedLocation));
        });

        builder.setNegativeButton("DELETE", (dialog, which) -> {
            if (!canToDeleteLocation()) {
                GlobalUtilities.showAlertDialog(getContext(), "Error", "You can't delete your last location!");
                dialog.cancel();
                return;
            }

            deleteLocation(selectedLocation);
            GlobalUtilities.showAlertDialog(getContext(), "Location", String.format("Location '%s' has been deleted!", selectedLocation));
            dialog.cancel();
        });

        builder.show();

    }

    private void deleteLocation(String selectedLocation) {
        locations.remove(selectedLocation);

        saveLocationsList();
        updateFavouriteLocationsList();

        deleteLocationDataFiles(selectedLocation);
    }

    private void deleteLocationDataFiles(String location) {
        JsonFileManager jsonFileManager = new JsonFileManager();
        jsonFileManager.deleteFile(getContext(), location + Settings.CURRENT_WEATHER_JSON_FILENAME_SUFFIX);
        jsonFileManager.deleteFile(getContext(), location + Settings.LONG_TERM_WEATHER_JSON_FILENAME_SUFFIX);
    }

    private boolean canToDeleteLocation() {
        return locations.size() > 1;
    }

    private void saveLocationsList() {
        String usingLocation = Settings.getUsingLocation();

        locations.remove(NEW_FAVOURITE_LOCATION_ADD_TEXT);
        locations.add(usingLocation);

        preferencesManager.saveStringList(locations, Settings.LOCATIONS_SET_KEY);

        locations.remove(usingLocation);
        locations.add(0, NEW_FAVOURITE_LOCATION_ADD_TEXT);
    }


    // Units


    private void initializeUnits(View view) {
        units = prepareUnitsArray();
        unitsAcTextView = findUnitsACTextView(view);

        setupUnitsAdapter();
        updateDisplayedUsingUnits();

        initializeOnUnitChangeListener();
    }

    private ArrayList<String> prepareUnitsArray() {
        List<String> list = Arrays.asList(Arrays.stream(Units.values())
                .filter(units -> units != Settings.getUsingUnits())
                .map(unit -> formatWord(unit.name()))
                .toArray(String[]::new));

        return new ArrayList<>(list);
    }

    private void updateUnitsArray() {
        List<String> unitsList = prepareUnitsArray();
        units.clear();

        units.addAll(unitsList);

    }

    private void setupUnitsAdapter() {
        unitsAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, units);
        unitsAcTextView.setAdapter(unitsAdapter);
    }

    private void updateDisplayedUsingUnits() {
        unitsAcTextView.setText(formatWord(Settings.getUsingUnits().toString()), false);
    }

    private void initializeOnUnitChangeListener() {
        unitsAcTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            handleUnitsChange(Units.valueOf(item.toUpperCase()));
        });
    }

    private AutoCompleteTextView findUnitsACTextView(View view) {
        return view.findViewById(R.id.using_units_actextview);
    }

    private void handleUnitsChange(Units unit) {
        Settings.setUsingUnits(unit);
        updateUnitsArray();
        unitsAdapter.notifyDataSetChanged();

        this.preferencesManager.saveString(Settings.UNITS_KEY, unit.toString());

        GlobalUtilities.showAlertDialog(getActivity(), "Units changed", "Using units: " + unit);

        GlobalUtilities.startUpdateThread(weatherDataUpdater, false);
    }


    // Button


    private void initializeRefreshButton(View view) {
        refreshButton = view.findViewById(R.id.refresh_button);
        initializeOnRefreshButtonClickListener();
    }

    private void initializeOnRefreshButtonClickListener() {
        refreshButton.setOnClickListener(v -> {
            if (!refreshButton.isEnabled())
                return;

            refreshButton.setEnabled(false);
            GlobalUtilities.startUpdateThread(weatherDataUpdater, false);
            startButtonCooldownResetThread();
        });
    }

    private void startButtonCooldownResetThread() {
        buttonCooldownThread = new CooldownResetThread(this, BUTTON_COOLDOWN_MS);
        buttonCooldownThread.setDaemon(true);
        buttonCooldownThread.start();
    }


    // Utilities


    private SharedPreferencesManager initializePreferencesManager(String filename) {
        return new SharedPreferencesManager((Context) weatherDataUpdater, filename);
    }


    private void loadWeatherDataUpdater() {
        Bundle args = getArguments();
        weatherDataUpdater = (WeatherDataUpdater) args.getSerializable("weatherDataUpdater");
    }

    @Override
    public void resetCooldown() {
        getActivity().runOnUiThread(() -> {
            refreshButton.setEnabled(true);
            buttonCooldownThread = null;
        });
    }

}