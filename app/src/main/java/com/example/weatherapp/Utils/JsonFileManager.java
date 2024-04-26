package com.example.weatherapp.Utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class JsonFileManager {

    public JsonFileManager() {
    }

    public void saveJsonObjectToFile(Context context, JSONObject jsonObject, String filename) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(jsonObject.toString());
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject loadJsonObjectFromFile(Context context, String filename) throws JSONException, IOException {
        FileInputStream fis = context.openFileInput(filename);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
        StringBuilder stringBuilder = new StringBuilder();

        String line;

        while ((line = bufferedReader.readLine()) != null)
            stringBuilder.append(line);

        fis.close();

        String json = stringBuilder.toString();
        JSONObject jsonObject = new JSONObject(json);

        return jsonObject;
    }

    public void deleteFile(Context context, String filename) {
        File file = new File(context.getFilesDir(), filename);
        if (file.exists())
            file.delete();

    }

}
