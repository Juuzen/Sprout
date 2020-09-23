package com.hcifedii.sprout.adapter;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcifedii.sprout.R;

import java.util.List;

import model.Habit;

public class PresetHabitAdapter extends RecyclerView.Adapter<PresetHabitAdapter.ViewHolder> {

    private OnClickListener listener;

    public interface OnClickListener {
        void onClick(Habit habit);
    }

    private int[] colorArray;
    private final List<Habit> presetHabitList;

    private Context context;

    public PresetHabitAdapter(List<Habit> presetHabitList) {
        this.presetHabitList = presetHabitList;
    }

    public void setListener(@NonNull OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_preset, parent, false);

        context = view.getContext();

        colorArray = context.getResources().getIntArray(R.array.materialColors);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Habit presetHabit = presetHabitList.get(position);

        holder.presetHabit = presetHabit;

        holder.titleTextView.setText(presetHabit.getTitle());

        // Set the card's icon
        if (presetHabit.getImage() > 0)
            holder.iconView.setImageDrawable(ContextCompat.getDrawable(context, presetHabit.getImage()));

        // Set card's background color
        holder.cardView.setCardBackgroundColor(colorArray[position % colorArray.length]);

        // Set the card's onClickListener
        holder.listener = listener;
    }

    @Override
    public int getItemCount() {
        return presetHabitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private OnClickListener listener;

        private final TextView titleTextView;
        private final ImageView iconView;
        private final CardView cardView;


        public Habit presetHabit;

        public ViewHolder(View view) {
            super(view);

            cardView = (CardView) view;

            titleTextView = view.findViewById(R.id.presetTitleTextView);
            iconView = view.findViewById(R.id.iconView);
            view.setOnClickListener(view1 -> listener.onClick(presetHabit));

        }

    }

}