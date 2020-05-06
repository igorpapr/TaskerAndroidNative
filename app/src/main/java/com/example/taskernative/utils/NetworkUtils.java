package com.example.taskernative.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.taskernative.LoginActivity;
import com.example.taskernative.utils.exceptions.StatusCodeException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG =
            NetworkUtils.class.getSimpleName();
    private static final String TASKS_BASE_URL =  "http://192.168.1.4:8080/api/tasks";
    private static final String LOGIN_URL = "http://192.168.1.4:8080/api/auth/login";

    public static String getTasks() throws StatusCodeException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tasksJSONString = "";
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

            int code = urlConnection.getResponseCode();

            if(code == 200){
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
                if (builder.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                tasksJSONString = builder.toString();

            }else if (code == 401){
                throw new StatusCodeException("Username or/and password are incorrect, please, try again");
            } else{
                throw new StatusCodeException("Internal server error. Please try another time. We are sorry for that");
            }
        } catch (IOException e1) {
            Log.e(LOG_TAG,"Couldn't get data from the server " + e1.getMessage());
            e1.printStackTrace();
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

    public static boolean addTask(String title, String description) throws JSONException, StatusCodeException {
        HttpURLConnection urlConnection = null;
        OutputStream out;
        Boolean toUpdate = false;
        //TODO - GET TOKEN
        String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOiI5ZGRhOTYyYi1hZmMwLTQ4MzItYjc3NC0xYzg2ZWRhZWU2YTEiLCJlbWFpbCI6InVzZXJAZ21haWwuY29tIiwidXNlcm5hbWUiOiJ1c2VyIiwiaWF0IjoxNTg4Nzc3MTc0LCJleHAiOjE1ODkwNzcxNzR9.xVETW-q8YKq_vv1J1Rkmq1tTOUmOuGVI9H0a41dJQU2ggU48tUxX-520lRKU_rdEFUN8TOcY2y1mX78aThzdYg";

        JSONObject taskToSendJson = new JSONObject();
        taskToSendJson.put("title", title);
        taskToSendJson.put("description", description);
        String jsonToSendString = taskToSendJson.toString();


        try {
            URL requestUrl = new URL(TASKS_BASE_URL);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", token);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);

            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(jsonToSendString);
            writer.flush();
            writer.close();
            out.close();

            urlConnection.connect();

            int code = urlConnection.getResponseCode();
            if(code == 200){
                toUpdate = true;
            }else if (code == 401){
                throw new StatusCodeException("Username or/and password are incorrect, please, try again");
            } else if (code == 400){
                throw new StatusCodeException("Input data is of incorrect format. Please, try again with correct data");
            } else{
                throw new StatusCodeException("Internal server error. Please try another time. We are sorry for that");
            }
        } catch (IOException e1) {
            Log.e(LOG_TAG,"Couldn't get data from the server " + e1.getMessage());
            e1.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return toUpdate;
    }

    public static String authenticateUser(String username, String password) throws JSONException, StatusCodeException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tokenJSONString = "";
        OutputStream out;

        JSONObject credsJson = new JSONObject();
        credsJson.put("username", username);
        credsJson.put("password", password);
        String credsToSendString = credsJson.toString();

        try {
            URL requestUrl = new URL(LOGIN_URL);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(credsToSendString);
            writer.flush();
            writer.close();
            out.close();

            urlConnection.connect();

            int code = urlConnection.getResponseCode();

            if(code == 200){
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
                if (builder.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                tokenJSONString = builder.toString();
            }else if (code == 401){
                throw new StatusCodeException("Username or/and password are incorrect, please, try again");
            } else if (code == 400){
                throw new StatusCodeException("Input data is of incorrect format. Please, try again with correct data");
            } else{
                throw new StatusCodeException("Internal server error. Please try another time. We are sorry for that");
            }
        } catch (IOException e1) {
            Log.e(LOG_TAG,"Couldn't get data from the server " + e1.getMessage());
            e1.printStackTrace();
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
        Log.d(LOG_TAG, tokenJSONString);
        return tokenJSONString;
    }
}
