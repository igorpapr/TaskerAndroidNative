package com.example.taskernative.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
    //private static final String TASKS_BASE_URL =  "http://192.168.1.4:8080/api/tasks";
    //private static final String LOGIN_URL = "http://192.168.1.4:8080/api/auth/login";
    private static final String TASKS_BASE_URL =  "https://taskerappbc.herokuapp.com/api/tasks";
    private static final String LOGIN_URL = "https://taskerappbc.herokuapp.com/api/auth/login";
    public static String getTasks(Context context) throws StatusCodeException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String tasksJSONString = "";
        SharedPreferences prefs = context.getSharedPreferences("Tasker", 0);
        String token = prefs.getString("token",null);
        try {
            URL requestUrl = new URL(TASKS_BASE_URL);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
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

    public static boolean addTask(String title, String description, Context context) throws JSONException, StatusCodeException {
        HttpURLConnection urlConnection = null;
        OutputStream out;
        Boolean toUpdate = false;

        SharedPreferences prefs = context.getSharedPreferences("Tasker", 0);
        String token = prefs.getString("token",null);


        JSONObject taskToSendJson = new JSONObject();
        taskToSendJson.put("title", title);
        taskToSendJson.put("description", description);
        String jsonToSendString = taskToSendJson.toString();


        try {
            URL requestUrl = new URL(TASKS_BASE_URL);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
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
            }else if (code == 401 || code == 404){
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
