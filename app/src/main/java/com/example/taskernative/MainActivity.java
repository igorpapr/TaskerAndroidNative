package com.example.taskernative;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.taskernative.utils.NetworkUtils;
import com.example.taskernative.models.Task;
import com.example.taskernative.utils.TaskListAdapter;
import com.example.taskernative.utils.exceptions.StatusCodeException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
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

////Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                //        .setAction("Action", null).show();
// Way to alert

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Task> mTaskList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TaskListAdapter mAdapter;

    public static final String EXTRA_TASK = "com.example.taskernative.extra.TASK";
    public static final int BOOL_REQUEST = 1;

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
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivityForResult(intent, BOOL_REQUEST);
            }
        });

        try{
            loadDataTasks();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new TaskListAdapter(this, mTaskList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BOOL_REQUEST) {
            if (resultCode == RESULT_OK) {
                try{
                    loadDataTasks();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void loadDataTasks() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>(){
            Exception probablyExcepionFromBackThread = null;

            @Override
            protected Void doInBackground(Void... voids){
                try{
                    String response = NetworkUtils.getTasks(getApplicationContext());
                    if (response.length() == 0){
                        Toast.makeText(MainActivity.this,
                                "Unknown error while getting tasks from the server", Toast.LENGTH_LONG).show();
                    }
                    else{
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            Task task = new Task(
                                    object.getString("taskId"),
                                    object.getString("title"),
                                    object.getString("description"));
                            mTaskList.add(task);
                        }
                    }
                } catch (JSONException e) {
                    probablyExcepionFromBackThread = e;
                } catch (StatusCodeException e1){
                    probablyExcepionFromBackThread = e1;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void s) {
                super.onPostExecute(s);
                if (probablyExcepionFromBackThread != null){
                    Toast.makeText(MainActivity.this, probablyExcepionFromBackThread.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Successfully fetched a list of tasks",
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        task.execute();
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
        if (id == R.id.action_logout) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("Tasker", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("token");
            editor.commit();
            Intent intent = new Intent (MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
