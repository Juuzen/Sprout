package com.hcifedii.sprout.adapter;

import android.app.TimePickerDialog;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.R;

import java.io.Serializable;
import java.util.List;

public class RemindersAdapter
        extends RecyclerView.Adapter<RemindersAdapter.RemindersViewHolder> {

    private List<Reminder> reminders;

    public static class Reminder implements Serializable {

        private int hours;
        private int minutes;
        private boolean isActive = true;

        private boolean is24HourFormat;

        public Reminder(int hours, int minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }

        public int getHours() {
            return hours;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public void is24HourFormat(boolean is24Hour) {
            this.is24HourFormat = is24Hour;
        }

        public static String buildFormattedTimeString(int hour, int minutes, boolean is24HourFormat) {

            if (is24HourFormat)
                return get24HourFormattedString(hour, minutes);

            return get12HourFormattedString(hour, minutes);
        }

        private static String get24HourFormattedString(int hours, int minutes) {

            StringBuilder time = new StringBuilder();

            time.append(hours);

            time.append(':');

            if (minutes < 10) time.append('0');

            time.append(minutes);

            return time.toString();
        }

        private static String get12HourFormattedString(int hour, int minutes) {

            StringBuilder time = new StringBuilder();
            String timeSet;

            if (hour >= 12) timeSet = "PM";
            else if (hour == 0) timeSet = "AM";
            else timeSet = "AM";

            if (hour > 12) time.append(hour - 12);
            else if (hour == 0) time.append(hour + 12);
            else time.append(hour);

            time.append(':');

            if (minutes < 10) time.append('0');

            time.append(minutes);
            time.append(' ').append(timeSet);

            return time.toString();
        }

        @NonNull
        @Override
        public String toString() {
            return "[" + hours + ":" + minutes + ", isActive=" + isActive + ", is24Hour=" + is24HourFormat + "]";
        }

    }

    public RemindersAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
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

        // Create a view with the TextView/ImageButton already instantiated
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
        Reminder reminder = reminders.get(position);

        boolean is24HourFormat = DateFormat.is24HourFormat(holder.context);
        holder.content.setText(Reminder.buildFormattedTimeString(reminder.getHours(), reminder.getMinutes(), is24HourFormat));
        holder.pauseButton.setSelected(reminder.isActive());
    }

    // Return the size of the list
    @Override
    public int getItemCount() {
        return reminders.size();
    }

    // Provide a reference to the views for each data item
    public class RemindersViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private TextView content;
        private ImageButton pauseButton;

        public RemindersViewHolder(View view) {
            super(view);

            context = view.getContext();

            pauseButton = view.findViewById(R.id.pauseButton);
            content = view.findViewById(R.id.contentTextView);
            ImageButton deleteButton = view.findViewById(R.id.deleteButton);

            boolean is24HourFormat = DateFormat.is24HourFormat(context);

            // Set up content TextView
            content.setOnClickListener(contentView -> {

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        (timePicker, hour, minutes) -> {

                            int position = getAdapterPosition();

                            content.setText(Reminder.buildFormattedTimeString(hour, minutes, is24HourFormat));

                            // Update values inside the reminder list
                            Reminder reminder = reminders.get(position);
                            reminder.setHours(hour);
                            reminder.setMinutes(minutes);
                            reminder.is24HourFormat(is24HourFormat);

                            notifyItemChanged(position);

                        }, 12, 0, is24HourFormat);

                timePickerDialog.show();

            });

            // Set up delete button
            deleteButton.setOnClickListener(deleteView -> {

                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    reminders.remove(position);
                    notifyItemRemoved(position);
                }
            });

            // Set up pause button
            pauseButton.setOnClickListener(pauseView -> {

                int position = getAdapterPosition();
                // Save the new state inside the reminder list
                if (position != RecyclerView.NO_POSITION) {
                    Reminder reminder = reminders.get(position);
                    boolean isActive = !reminder.isActive();

                    reminder.setActive(isActive);
                    notifyItemChanged(position);
                }
            });

        }

    }

}
