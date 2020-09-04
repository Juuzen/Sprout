package com.hcifedii.sprout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hcifedii.sprout.R;
import com.hcifedii.sprout.filter.HabitFilterHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import model.Habit;

public class HabitListAdapter extends RecyclerView.Adapter<HabitListAdapter.ViewHolder> implements Filterable {

    public interface OnClickListener {
        void onClick(int habitId);
    }

    private OnClickListener listener;

    /**
     * List showed inside the recycler view
     */
    private List<Habit> currentList;

    /**
     * Original unmodified List
     */
    private List<Habit> originalList;

    private Context context;

    public HabitListAdapter(@NonNull List<Habit> habitList) {
        this.currentList = habitList;
        this.originalList = habitList;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.habit_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitListAdapter.ViewHolder holder, int position) {

        Habit habit = currentList.get(position);

        holder.habitId = habit.getId();

        holder.titleTextView.setText(habit.getTitle());

        DateFormat dateFormatter = android.text.format.DateFormat.getDateFormat(context);
        holder.dateTextView.setText(dateFormatter.format(new Date(habit.getHabitCreationDate())));

        holder.listener = listener;

    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    @Override
    public Filter getFilter() {
        return new HabitFilterHelper(originalList) {
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                currentList = (List<Habit>) filterResults.values;
                notifyDataSetChanged();

                if (currentList == null || currentList.size() < 1) {
                    // Show message if no element was found
                    Toast.makeText(context, R.string.no_item_found_message, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private int habitId;
        private MaterialTextView titleTextView;
        private MaterialTextView dateTextView;

        private OnClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.habitItemTitle);
            dateTextView = itemView.findViewById(R.id.habitItemCreationDate);
            itemView.setOnClickListener(view -> {
                listener.onClick(habitId);
            });

        }

        public int getHabitId() {
            return habitId;
        }


    }

}
