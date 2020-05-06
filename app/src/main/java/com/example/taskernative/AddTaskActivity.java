package com.example.taskernative;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskernative.utils.NetworkUtils;
import com.example.taskernative.utils.exceptions.StatusCodeException;

import org.json.JSONException;

public class AddTaskActivity extends AppCompatActivity {
    private EditText title;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        title = findViewById(R.id.editText_title);
        description = findViewById(R.id.editText_description);
    }

    public void sendNewTask(View view) {
        final String titleValue = title.getText().toString();
        final String descriptionValue = description.getText().toString();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Boolean> task = new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids){
                boolean response = false;
                try {
                    response = NetworkUtils.addTask(titleValue,descriptionValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (StatusCodeException e1){
                    Toast.makeText(AddTaskActivity.this,
                            e1.getMessage(), Toast.LENGTH_LONG).show();
                }
                return response;
            }

            @Override
            protected void onPostExecute(Boolean res) {
                super.onPostExecute(res);
                if (!res){
                    Toast.makeText(AddTaskActivity.this,
                            "Error while adding task", Toast.LENGTH_LONG).show();;
                }
                else{
                    Intent replyIntent = new Intent();
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
            }
        };
        task.execute();
    }


}
