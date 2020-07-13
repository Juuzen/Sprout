package com.hcifedii.sprout.adapter;

import android.app.TimePickerDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.R;

import java.util.List;

public class RemindersAdapter
        extends RecyclerView.Adapter<RemindersAdapter.RemindersViewHolder> {

    private List<Reminder> reminderList;

    private static final int DEFAULT_HOUR = 12;
    private static final int DEFAULT_MINUTES = 0;

    public RemindersAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary findViewById(int) calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RemindersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create a view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_reminders_item, parent, false);

        return new RemindersAdapter.RemindersViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the RecyclerView.ViewHolder#itemView to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {android.widget.ListView, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use RecyclerView.ViewHolder#getAdapterPosition() which will
     * have the updated adapter position.
     * <p>
     * Override onBindViewHolder(RecyclerView.ViewHolder, int, List) instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RemindersAdapter.RemindersViewHolder holder, int position) {
        // Set the contents inside a row of a reminder
        Reminder reminder = reminderList.get(position);

        holder.reminderItem.pauseButton = reminder.pauseButton;
        holder.reminderItem.content = reminder.content;
        holder.reminderItem.deleteButton = reminder.deleteButton;

        holder.reminderItem.hours = reminder.hours;
        holder.reminderItem.minutes = reminder.minutes;

    }

    // Return the size of the list
    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // Provide a reference to the views for each data item
    public class RemindersViewHolder extends RecyclerView.ViewHolder {

        View view;
        Reminder reminderItem;

        public RemindersViewHolder(View view) {
            super(view);

            this.view = view;

            reminderItem = new Reminder(view.findViewById(R.id.contentTextView),
                    view.findViewById(R.id.deleteButton), view.findViewById(R.id.pauseButton));

            // Set the content field of a reminder
            reminderItem.content.setText(R.string.defaultTimeString);
            reminderItem.content.setOnClickListener(view1 -> {

                TimePickerDialog timePickerDialog = new TimePickerDialog(view1.getContext(),
                        (timePicker, hour, minutes) -> {

                            // Save the time inside a reminderItem, for easy pick later.
                            reminderItem.setTime(hour, minutes);

                            // Update the time inside the TextView
                            TextView content = view1.findViewById(R.id.contentTextView);

                            // TODO: migliorare la formattazione per i minuti
                            String newTime = hour + ":" + minutes;


                            content.setText(newTime);
                        }, DEFAULT_HOUR, DEFAULT_MINUTES, true);

                timePickerDialog.show();

            });

            // Set the two buttons
            reminderItem.deleteButton.setOnClickListener(view1 -> {

                // TODO: rimuovere il reminder dal database

                reminderList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });

            reminderItem.pauseButton.setOnClickListener(view1 -> {

                // TODO: salvare il cambiamento sul database

                ImageButton button = view1.findViewById(R.id.pauseButton);
                // Change the state of the button
                button.setSelected(!button.isSelected());

            });

        }
    }


    public static class Reminder {

        private TextView content;
        private ImageButton deleteButton;
        private ImageButton pauseButton;

        private int hours = DEFAULT_HOUR;
        private int minutes = DEFAULT_MINUTES;

        public Reminder(TextView content, ImageButton deleteButton, ImageButton pauseButton) {
            this.content = content;
            this.deleteButton = deleteButton;
            this.pauseButton = pauseButton;
        }

        public void setTime(int hours, int minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }


    }

}
