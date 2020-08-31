package com.hcifedii.sprout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.R;

import java.util.ArrayList;

import model.Habit;


public class HabitCardAdapter extends RecyclerView.Adapter<HabitCardAdapter.ViewHolder> {
    ArrayList<Habit> habitList;
    Context myContext;

    public HabitCardAdapter(Context ct, ArrayList<Habit> list) {
        myContext = ct;
        habitList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.fragment_habit_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.habitTitle.setText(habit.getTitle());
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTitle = itemView.findViewById(R.id.habitCardTitle);
        }
    }
}
