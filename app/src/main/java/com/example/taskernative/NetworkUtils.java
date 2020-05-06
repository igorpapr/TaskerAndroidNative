package com.example.taskernative;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG =
            NetworkUtils.class.getSimpleName();
    private static final String TASKS_BASE_URL =  "http://192.168.1.4:8080/api/tasks";
    private static final String LOGIN_URL = "http://192.168.1.4:8080/api/auth/login";

    public static String getTasks(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tasksJSONString = null;
        //TODO - GET TOKEN
        String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOiI5ZGRhOTYyYi1hZmMwLTQ4MzItYjc3NC0xYzg2ZWRhZWU2YTEiLCJlbWFpbCI6InVzZXJAZ21haWwuY29tIiwidXNlcm5hbWUiOiJ1c2VyIiwiaWF0IjoxNTg4Nzc3MTc0LCJleHAiOjE1ODkwNzcxNzR9.xVETW-q8YKq_vv1J1Rkmq1tTOUmOuGVI9H0a41dJQU2ggU48tUxX-520lRKU_rdEFUN8TOcY2y1mX78aThzdYg";

        try {
            URL requestUrl = new URL(TASKS_BASE_URL);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", token);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();
            // Create a buffered reader from that input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // Use a StringBuilder to hold the incoming response.
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                // Since it's JSON, adding a newline isn't necessary (it won't
                // affect parsing) but it does make debugging a *lot* easier
                // if you print out the completed buffer for debugging.
                builder.append("\n");
            }
            if (builder.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            tasksJSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(LOG_TAG, tasksJSONString);
        return tasksJSONString;
    }
}
