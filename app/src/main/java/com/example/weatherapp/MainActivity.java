package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    EditText inputCity;
    TextView textViewCity;
    TextView textViewTemperature;
    TextView textViewDescription;


    String key = "9d4ec55acf15bf987bf2aa7d0a7b55a8";
    String mockCity = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";
    String mockZip = "https://api.openweathermap.org/data/2.5/weather?zip=&appid=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputCity = findViewById(R.id.editTextCity);
        textViewCity = findViewById(R.id.textViewCity);
        textViewTemperature = findViewById(R.id.textViewTemp);
        textViewDescription = findViewById(R.id.textViewDescription);
    }



    public void weatherUpload(View view) {
        String city = inputCity.getText().toString().trim();

        if (!city.isEmpty()) {
            String link = String.format(mockCity, city, key);

            DownloadJSON task = new DownloadJSON();
            task.execute(link);
        }
    }

    public class DownloadJSON extends AsyncTask<String, Void, String > {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            URLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();

            try {
                url = new URL(strings[0]);
                urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line = bufferedReader.readLine();

                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }

                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                String city = jsonObject.getString("name");
                int temp = jsonObject.getJSONObject("main").getInt("temp") - 275;
                String temperature = Integer.toString(temp) + "°";
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

                textViewCity.setText(city);
                textViewTemperature.setText(temperature);
                textViewDescription.setText(description);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), R.string.invalid_name, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
