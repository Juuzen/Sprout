package com.hcifedii.sprout.filter;

import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import model.Habit;

public abstract class HabitFilterHelper extends Filter {

    private final List<Habit> originalList;

    protected HabitFilterHelper(@NonNull List<Habit> originalList) {
        this.originalList = originalList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        FilterResults filterResults = new FilterResults();

        if (charSequence != null && charSequence.length() > 0) {
            // Perform filtering

            List<Habit> foundHabits = new ArrayList<>();
            String input = charSequence.toString().toLowerCase();

            for (Habit habit : originalList) {
                String habitTitle = habit.getTitle().toLowerCase();

                if (habitTitle.contains(input))
                    foundHabits.add(habit);
            }

            filterResults.count = foundHabits.size();
            filterResults.values = foundHabits;

        } else {

            filterResults.count = originalList.size();
            filterResults.values = originalList;
        }

        return filterResults;
    }

}
