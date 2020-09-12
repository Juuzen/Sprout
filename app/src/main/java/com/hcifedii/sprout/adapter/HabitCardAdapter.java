package com.hcifedii.sprout.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.EditHabitActivity;
import com.hcifedii.sprout.R;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import model.Habit;

public class HabitCardAdapter extends RealmRecyclerViewAdapter<Habit, RecyclerView.ViewHolder> implements Filterable {
    Context ct;
    OrderedRealmCollection<Habit> list;
    OrderedRealmCollection<Habit> filteredList; /* mandatory for the filter */
    Realm mRealm;

    private static int CLASSIC_TYPE = 1;
    private static int REPETITION_TYPE = 2;

    public HabitCardAdapter(@Nullable OrderedRealmCollection<Habit> data, Context context, Realm realm) {
        super(data, true, true);
        ct = context;
        list = data;
        filteredList = data;
        mRealm = realm;
    }

    @Override
    public int getItemCount() {
        return this.filteredList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == CLASSIC_TYPE) {
            view = inflater.inflate(R.layout.fragment_habit_classic_card, parent, false);
            return new ClassicViewHolder(view);
        } else /* if (viewType == REPETITION_TYPE) */ {
            view = inflater.inflate(R.layout.fragment_habit_counter_card, parent, false);
            return new RepetitionViewHolder(view);
        }
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Habit habit = getItem(position);
        final int viewType = getItemViewType(position);
        if (viewType == CLASSIC_TYPE) {
            ((ClassicViewHolder) holder).setHabit(habit, ct);
        } else if (viewType == REPETITION_TYPE) {
            ((RepetitionViewHolder) holder).setHabit(habit, ct);
        }
    }

    /*
    @Override
    public void onBindViewHolder(@NonNull HabitCardAdapter.ViewHolder holder, int position) {
        final Habit habit = getItem(position);
        if (habit != null) {
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
    }
    */

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        Habit habit = filteredList.get(position);
        if (habit != null) {
            String habitType = habit.getHabitType().toString();
            if (habitType.equals("CLASSIC")) type = CLASSIC_TYPE;
            else if (habitType.equals("COUNTER")) type = REPETITION_TYPE;
        }
        return type;
    }

    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        if (text == null || "".equals(text)) {
            filteredList = list;
        } else {
            filteredList = mRealm.where(Habit.class).beginsWith("title", text, Case.INSENSITIVE).sort("id").findAll();
        }
        updateData(filteredList);
    }

    public Filter getFilter() {
        return new HabitFilter(this);
    }

    private static class HabitFilter extends Filter {
        private final HabitCardAdapter adapter;

        private HabitFilter(HabitCardAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.filterResults(charSequence.toString());
        }
    }

    public class ClassicViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitle;
        ImageButton editHabitButton;
        Button checkButton;

        public ClassicViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTitle = itemView.findViewById(R.id.classicHabitCardTitle);
            editHabitButton = itemView.findViewById(R.id.classicHabitEditButton);
            checkButton = itemView.findViewById(R.id.classicHabitCheckButton);
        }

        public void setHabit(Habit habit, Context context) {
            habitTitle.setText(habit.getTitle());

            editHabitButton.setOnClickListener(view -> {
                Intent intent = new Intent(context, EditHabitActivity.class);
                intent.putExtra("HABIT_ID", habit.getId());

                Bundle bundle = ActivityOptions.makeCustomAnimation(ct, android.R.anim.fade_in,
                        android.R.anim.fade_out).toBundle();
                
                ct.startActivity(intent, bundle);
            });

            checkButton.setOnClickListener(view -> {
                //TODO: inserire il codice per il listener
                Toast.makeText(ct, "Funziono", Toast.LENGTH_SHORT).show();
            });
        }
    }

    public class RepetitionViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitle;
        ProgressBar progressBar;
        TextView progressLabel;
        ImageButton editHabitButton;
        Button checkButton;

        public RepetitionViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTitle = itemView.findViewById(R.id.counterHabitCardTitle);
            editHabitButton = itemView.findViewById(R.id.counterHabitEditButton);
            progressBar = itemView.findViewById(R.id.counterHabitProgressBar);
            checkButton = itemView.findViewById(R.id.counterHabitCheckButton);
            progressLabel = itemView.findViewById(R.id.counterHabitProgressLabel);
        }

        void setHabit(Habit habit, Context context) {
            this.habitTitle.setText(habit.getTitle());
            this.progressBar.setProgress(habit.getRepetitions());
            this.progressBar.setMax(habit.getMaxRepetitions());


            String message = context.getString(R.string.completed) + ' ' + habit.getRepetitions() + ' ' + context.getString(R.string.n_times) + ' ' + habit.getMaxRepetitions();

            this.progressLabel.setText(message);

            editHabitButton.setOnClickListener(view -> {
                Intent intent = new Intent(context, EditHabitActivity.class);
                intent.putExtra("HABIT_ID", habit.getId());

                Bundle bundle = ActivityOptions.makeCustomAnimation(ct, android.R.anim.fade_in,
                        android.R.anim.fade_out).toBundle();

                ct.startActivity(intent, bundle);
            });

            checkButton.setOnClickListener(view -> {
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
                            progressLabel.setText(newLabel);
                        }
                    });
                }
            });
        }
    }

    /*
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitle;
        ProgressBar progressBar;
        TextView progressLabel;
        ImageButton editHabitButton;
        Button checkButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTitle = itemView.findViewById(R.id.counterHabitCardTitle);
            editHabitButton = itemView.findViewById(R.id.counterHabitEditButton);
            progressBar = itemView.findViewById(R.id.counterHabitProgressBar);
            checkButton = itemView.findViewById(R.id.counterHabitCheckButton);
            progressLabel = itemView.findViewById(R.id.counterHabitProgressLabel);
        }

        void setHabit(Habit habit) {
            this.habitTitle.setText(habit.getTitle());
            this.progressBar.setProgress(habit.getRepetitions());
            this.progressBar.setMax(habit.getMaxRepetitions());
            this.progressLabel.setText("Completato " + habit.getRepetitions() + " volte su " + habit.getMaxRepetitions()); //FIXME: sposta la stringa
        }
    }
     */
}