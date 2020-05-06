package com.example.taskernative;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;

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
        String mCurrent = mTaskList.get(position);
        holder.taskItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }


    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView taskItemView;
        final TaskListAdapter mAdapter;
        public TextView taskDescriptionView;

        public TaskViewHolder(View itemView, TaskListAdapter adapter) {
            super(itemView);
            taskItemView = itemView.findViewById(R.id.task);
            taskDescriptionView = itemView.findViewById(R.id.taskdescription);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        // Get the position of the item that was clicked.
        int mPosition = getLayoutPosition();
        // Use that to access the affected item in mWordList.
        String element = mTaskList.get(mPosition);
        // Change the word in the mWordList.
        mTaskList.set(mPosition, "Clicked! " + element);
        // Notify the adapter, that the data has changed so it can
        // update the RecyclerView to display the data.
        mAdapter.notifyDataSetChanged();
        }
    }
}
