package com.example.taskernative;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskernative.utils.NetworkUtils;
import com.example.taskernative.utils.exceptions.StatusCodeException;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        preferences = getApplicationContext().getSharedPreferences("Tasker", 0);
        if(preferences.getString("token",null) != null){
            navigateToMainPage();
        }
    }

    public void login(View view) {
        if(preferences.getString("token",null) != null){
            navigateToMainPage();
        }else{
            fetchToken();
        }
    }

    private void fetchToken(){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,String> task = new AsyncTask<Void, Void, String>(){

            Exception probablyExcepionFromBackThread = null;

            @Override
            protected String doInBackground(Void... voids){
                try {
                    String response = NetworkUtils.authenticateUser(username.getText().toString(),
                                                             password.getText().toString());
                    if (response != null){
                        if (response.length() == 0) {
                            Toast.makeText(LoginActivity.this,
                                    "Couldn't authenticate user, try again", Toast.LENGTH_LONG).show();
                        }
                        JSONObject obj = new JSONObject(response);
                        return obj.getString("token");
                    }
                } catch (JSONException e) {
                    probablyExcepionFromBackThread = e;
                } catch (StatusCodeException e1){
                    probablyExcepionFromBackThread = e1;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (probablyExcepionFromBackThread != null){
                    Toast.makeText(LoginActivity.this,
                            probablyExcepionFromBackThread.getMessage(), Toast.LENGTH_LONG).show();
                }else if (s != null){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", s);
                    editor.apply();
                    navigateToMainPage();
                }
            }
        };
        task.execute();
    }

    private void navigateToMainPage(){
        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
