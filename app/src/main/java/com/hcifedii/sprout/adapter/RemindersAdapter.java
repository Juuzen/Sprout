package com.hcifedii.sprout.adapter;

import android.app.TimePickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.R;
import com.hcifedii.sprout.TimePickerFragment;

import java.util.List;

public class RemindersAdapter
        extends RecyclerView.Adapter<RemindersAdapter.RemindersViewHolder> {

    FragmentManager fragmentManager;

    private List<Reminder> reminderList;
    private static int id = 0;

    public RemindersAdapter(List<Reminder> reminderList, FragmentManager fragmentManager) {
        this.reminderList = reminderList;
        this.fragmentManager = fragmentManager;
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

        return new RemindersAdapter.RemindersViewHolder(view, id++);
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

    }

    // Return the size of the list
    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // Provide a reference to the views for each data item
    public class RemindersViewHolder extends RecyclerView.ViewHolder {

        View view;
        int id;
        Reminder reminderItem;

        public RemindersViewHolder(View view, int id) {
            super(view);

            this.view = view;
            // Position inside the RecyclerView
            this.id = id;

            reminderItem = new Reminder(view.findViewById(R.id.contentTextView),
                    view.findViewById(R.id.deleteButton), view.findViewById(R.id.pauseButton));

            //String message = "Id: " + Integer.toString(id);
            reminderItem.content.setText(R.string.defaultTimeString);

            reminderItem.content.setOnClickListener(view1 -> {

                // TODO: non funziona ancora

                TimePickerFragment timePickerFragment = new TimePickerFragment();
                TimePickerDialog dialog = (TimePickerDialog) timePickerFragment.onCreateDialog(null);



                timePickerFragment.show(fragmentManager, "timePicker");

                String time = timePickerFragment.getHour() + ":" + timePickerFragment.getMinute();



                TextView textView = view1.findViewById(R.id.contentTextView);
                textView.setText(time);

                String message2 = textView.getText().toString();

                Toast.makeText(view1.getContext(), message2, Toast.LENGTH_SHORT).show();
            });


            reminderItem.deleteButton.setOnClickListener(view1 -> {

                // TODO: da togliere queste due linee di codice
                String message3 = Integer.toString(getAdapterPosition()) + " position";
                Toast.makeText(view1.getContext(), message3, Toast.LENGTH_SHORT).show();

                reminderList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });

            reminderItem.pauseButton.setOnClickListener(view1 -> {

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

        public Reminder(TextView content, ImageButton deleteButton, ImageButton pauseButton) {
            this.content = content;
            this.deleteButton = deleteButton;
            this.pauseButton = pauseButton;
        }
    }

}
