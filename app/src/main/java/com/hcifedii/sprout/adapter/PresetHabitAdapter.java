package com.hcifedii.sprout.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcifedii.sprout.R;

import java.util.List;

import model.Habit;

public class PresetHabitAdapter extends RecyclerView.Adapter<PresetHabitAdapter.ViewHolder> {

    private final List<Habit> presetHabitList;

    public PresetHabitAdapter(List<Habit> presetHabitList) {
        this.presetHabitList = presetHabitList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_preset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Habit presetHabit = presetHabitList.get(position);

        holder.presetHabit = presetHabit;

        holder.titleTextView.setText(presetHabit.getTitle());
        //holder.iconView.setText(presetHabit.getHabitType());
    }

    @Override
    public int getItemCount() {
        return presetHabitList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView titleTextView;
        public final ImageView iconView;

        public Habit presetHabit;

        public ViewHolder(View view) {
            super(view);

            this.view = view;
            titleTextView = (TextView) view.findViewById(R.id.presetTitleTextView);
            iconView = (ImageView) view.findViewById(R.id.iconView);
        }
    }
}