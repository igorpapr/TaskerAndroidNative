package com.example.taskernative.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskernative.MainActivity;
import com.example.taskernative.R;
import com.example.taskernative.TaskDetailsActivity;
import com.example.taskernative.models.Task;

import java.util.ArrayList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private final ArrayList<Task> mTaskList;
    private LayoutInflater mInflater;
    private Context context;

    public TaskListAdapter(Context context,
                           ArrayList<Task> taskList) {
        mInflater = LayoutInflater.from(context);
        this.mTaskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.tasklist_item, parent, false);
        return new TaskViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.taskItemView.setText(mTaskList.get(position).getTitle());
     }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }


    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView taskItemView;
        public final TaskListAdapter mAdapter;

        public TaskViewHolder(View itemView, TaskListAdapter adapter) {
            super(itemView);
            taskItemView = itemView.findViewById(R.id.task);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();
            // Use that to access the affected item in mWordList.
            Task element = mTaskList.get(mPosition);
            // Change the word in the mWordList.
            Intent intent = new Intent(context, TaskDetailsActivity.class);
            intent.putExtra(MainActivity.EXTRA_TASK, element);
            context.startActivity(intent);
        }
    }
}
