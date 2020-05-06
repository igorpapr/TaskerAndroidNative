package com.example.taskernative;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TaskDetailsActivity extends AppCompatActivity {

    private TextView taskIdView;
    private TextView taskTitleView;
    private TextView taskDescriptionView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        taskIdView = findViewById(R.id.taskid_value);
        taskTitleView = findViewById(R.id.title_value);
        taskDescriptionView = findViewById(R.id.description_value);

        Intent intent = getIntent();
        Task item = (Task) intent.getSerializableExtra(MainActivity.EXTRA_TASK);
        taskIdView.setText(item.getId());
        taskTitleView.setText(item.getTitle());
        taskDescriptionView.setText(item.getDescription());
    }
}
