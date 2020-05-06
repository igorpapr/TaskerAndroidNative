package com.example.taskernative;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

////Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                //        .setAction("Action", null).show();
// Way to alert

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Task> mTaskList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TaskListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - send data to the server and fetch new data
                //int wordListSize = mTaskList.size();
                // Add a new word to the wordList.
                //mTaskList.addLast("+ Word " + wordListSize);
                // Notify the adapter, that the data has changed.
               // mRecyclerView.getAdapter().notifyItemInserted(wordListSize);
                // Scroll to the bottom.
               // mRecyclerView.smoothScrollToPosition(wordListSize);
            }
        });

        //for (int i = 0; i < 20; i++) {
        //    mTaskList.addLast("Word " + i);
        //}
        try{
            loadDataTasks();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerview);

        // Create an adapter and supply the data to be displayed.
        mAdapter = new TaskListAdapter(this, mTaskList);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void loadDataTasks() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids){
                String response = NetworkUtils.getTasks();
                if (response.length() == 0){
                    Toast.makeText(MainActivity.this,
                            "Error while getting tasks from the server", Toast.LENGTH_LONG).show();;
                }
                try{
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        Task task = new Task(
                                object.getString("task_id"),
                                object.getString("title"),
                                object.getString("description"));
                        mTaskList.add(task);
                    }
                }catch (JSONException e1){
                    e1.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void s) {
                super.onPostExecute(s);
                mAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
