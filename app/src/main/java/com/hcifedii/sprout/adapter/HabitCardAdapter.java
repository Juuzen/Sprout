package com.hcifedii.sprout.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.EditHabitActivity;
import com.hcifedii.sprout.R;

import java.util.ArrayList;
import java.util.List;

import model.Habit;


public class HabitCardAdapter extends RecyclerView.Adapter<HabitCardAdapter.ViewHolder> {
    List<Habit> habitList;
    Context myContext;

    public HabitCardAdapter(Context ct, List<Habit> list) {
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

        holder.habitCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myContext, EditHabitActivity.class);
                intent.putExtra("HABIT_ID", habit.getId());
                //TODO: Aggiungere l'animazione
                myContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitle;
        CardView habitCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTitle = itemView.findViewById(R.id.habitCardTitle);
            habitCardView = itemView.findViewById(R.id.habitCardView);
        }
    }
}
