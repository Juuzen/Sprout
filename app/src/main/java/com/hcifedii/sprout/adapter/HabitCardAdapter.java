package com.hcifedii.sprout.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.EditHabitActivity;
import com.hcifedii.sprout.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import model.Habit;
import utils.HabitRealmManager;

/*
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
        //TODO: inflatare diversi tipi di carte a seconda del habitType
        View view = inflater.inflate(R.layout.fragment_habit_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.habitTitle.setText(habit.getTitle());

        holder.habitCardView.setOnClickListener(view -> {
            Intent intent = new Intent(myContext, EditHabitActivity.class);
            intent.putExtra("HABIT_ID", habit.getId());
            //TODO: Aggiungere l'animazione
            myContext.startActivity(intent);
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
*/

public class HabitCardAdapter extends RealmRecyclerViewAdapter<Habit, HabitCardAdapter.ViewHolder> {
    Context ct;

    public HabitCardAdapter(@Nullable OrderedRealmCollection<Habit> data, boolean autoUpdate, Context context) {
        super(data, autoUpdate); //autoUpdate to true
        ct = context;
    }

    @NonNull
    @Override
    public HabitCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //TODO: inflatare diversi tipi di carte a seconda del habitType
        View view = inflater.inflate(R.layout.fragment_habit_counter_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitCardAdapter.ViewHolder holder, int position) {
        final Habit habit = getItem(position);
        holder.setHabit(habit);

        holder.editHabitButton.setOnClickListener(view -> {
            Intent intent = new Intent(ct, EditHabitActivity.class);
            intent.putExtra("HABIT_ID", habit.getId());
            //TODO: Aggiungere l'animazione
            ct.startActivity(intent);
        });

        holder.checkButton.setOnClickListener(view -> {
            int habitId = habit.getId();
            int newRepValue = habit.getRepetitions() + 1;
            int maxReps = habit.getMaxRepetitions();
            Log.d("Testing", newRepValue + " - " + maxReps);
            if (newRepValue <= habit.getMaxRepetitions()) {
                habit.getRealm().executeTransaction(realm -> {
                    Habit result = realm.where(Habit.class).equalTo("id", habitId).findFirst();
                    if (result != null) {
                        result.setRepetitions(newRepValue);
                        String newLabel = "Completato " + newRepValue + " volte su " + maxReps;
                        holder.progressLabel.setText(newLabel);
                    }
                });
            }

        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitle;
        ProgressBar progressBar;
        TextView progressLabel;
        ImageButton editHabitButton;
        Button checkButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTitle = itemView.findViewById(R.id.habitCardTitle);
            editHabitButton = itemView.findViewById(R.id.counterHabitEditButton);
            progressBar = itemView.findViewById(R.id.counterHabitProgressBar);
            checkButton = itemView.findViewById(R.id.checkButton);
            progressLabel = itemView.findViewById(R.id.counterHabitProgressLabel);
        }

        void setHabit(Habit habit) {
            this.habitTitle.setText(habit.getTitle());
            this.progressBar.setProgress(habit.getRepetitions());
            this.progressBar.setMax(habit.getMaxRepetitions());
            this.progressLabel.setText("Completato " + habit.getRepetitions() + " volte su " + habit.getMaxRepetitions());
        }
    }
}

/*
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
        //TODO: inflatare diversi tipi di carte a seconda del habitType
        View view = inflater.inflate(R.layout.fragment_habit_counter_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.habitTitle.setText(habit.getTitle());
        holder.editHabitButton.setOnClickListener(view -> {
            Intent intent = new Intent(myContext, EditHabitActivity.class);
            intent.putExtra("HABIT_ID", habit.getId());
            //TODO: Aggiungere l'animazione
            myContext.startActivity(intent);
        });
        holder.progressBar.setMax(habit.getMaxRepetitions());
        holder.progressBar.setProgress(habit.getRepetitions());
        holder.progressLabel.setText("Completato " + habit.getRepetitions() + " volte su " + habit.getMaxRepetitions());
        holder.checkButton.setOnClickListener(view -> {
            String newText = "Completato " + habit.getRepetitions() + " volte su " + habit.getMaxRepetitions();
            int newValue = habit.getRepetitions() + 1;
            HabitRealmManager.setHabitRepetition(habit.getId(), newValue);
            holder.progressBar.setProgress(habit.getRepetitions());
            holder.progressLabel.setText(newText);
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitle;
        ProgressBar progressBar;
        ImageButton editHabitButton;
        Button checkButton;
        TextView progressLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTitle = itemView.findViewById(R.id.habitCardTitle);
            editHabitButton = itemView.findViewById(R.id.counterHabitEditButton);
            progressBar = itemView.findViewById(R.id.counterHabitProgressBar);
            checkButton = itemView.findViewById(R.id.checkButton);
            progressLabel = itemView.findViewById(R.id.counterHabitProgressLabel);
        }
    }
}

 */