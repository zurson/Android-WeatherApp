package com.example.weatherapp.Fragments.DetailedInfoFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.Utils.Settings;

public class DetailFragment extends Fragment {

    private ImageView imageView;
    private TextView descriptionTextView;
    private TextView valueTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detailed_weather_info_single_layout, container, false);

        findElements(view);
        clearData();
        return view;
    }

    private void findElements(View view) {
        imageView = view.findViewById(R.id.detailed_weather_image);
        descriptionTextView = view.findViewById(R.id.detailed_weather_title_text_view);
        valueTextView = view.findViewById(R.id.detailed_weather_value_text_view);
    }

    private void clearData() {
        imageView.setVisibility(View.INVISIBLE);
        descriptionTextView.setText(Settings.NO_DATA_TEXT);
        valueTextView.setText(Settings.NO_DATA_TEXT);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getDescriptionTextView() {
        return descriptionTextView;
    }

    public TextView getValueTextView() {
        return valueTextView;
    }
}
