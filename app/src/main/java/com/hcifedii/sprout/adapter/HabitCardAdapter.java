package com.hcifedii.sprout.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.hcifedii.sprout.EditHabitActivity;
import com.hcifedii.sprout.HabitStatsActivity;
import com.hcifedii.sprout.R;
import com.hcifedii.sprout.fragment.RepetitionHabitNumberPickerFragment;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import model.Habit;
import model.Tree;
import utils.TreeArcProgress;
import utils.TreeManager;

public class HabitCardAdapter extends RealmRecyclerViewAdapter<Habit, RecyclerView.ViewHolder> implements Filterable {

    private static final int CLASSIC_TYPE = 1;
    private static final int REPETITION_TYPE = 2;

    private Context context;

    private OrderedRealmCollection<Habit> list;
    private OrderedRealmCollection<Habit> filteredList; /* mandatory for the filter */

    private Realm mRealm;

    // View used by the Snackbar as anchor
    private View anchorFab;


    public HabitCardAdapter(@Nullable OrderedRealmCollection<Habit> data, Realm realm, View anchorFab) {
        super(data, true, true);
        list = data;
        filteredList = data;
        mRealm = realm;

        this.anchorFab = anchorFab;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == CLASSIC_TYPE) {
            View view = inflater.inflate(R.layout.fragment_habit_classic_card, parent, false);
            return new ClassicViewHolder(view, anchorFab);
        } else /* REPETITION_TYPE */ {
            View view = inflater.inflate(R.layout.fragment_habit_counter_card, parent, false);
            return new RepetitionViewHolder(view, anchorFab);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final Habit habit = getItem(position);
        if (habit == null)
            return;

        final int viewType = getItemViewType(position);
        if (viewType == CLASSIC_TYPE) {
            ((ClassicViewHolder) holder).setHabit(habit, context);
        } else if (viewType == REPETITION_TYPE) {
            ((RepetitionViewHolder) holder).setHabit(habit, context);
        }

    }

    protected static int getTreeAsset(Tree tree, Context context) {
        int result = -1;
        Tree.Growth growth = tree.getGrowth();
        Tree.Health health = tree.getHealth();
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
            case Configuration.UI_MODE_NIGHT_NO:
                switch (growth) {
                    case SPROUT:
                        result = R.drawable.tree_day_sprout;
                        break;
                    case SPARKLING:
                        result = R.drawable.tree_day_sparkling;
                        break;

                    case SMALL:
                        switch (health) {
                            case HEALTHY:
                                result = R.drawable.tree_day_small_healthy;
                                break;
                            case DRYING:
                                result = R.drawable.tree_day_small_drying;
                                break;
                            case WITHERED:
                                result = R.drawable.tree_day_small_withered;
                                break;
                        }
                        break;

                    case MEDIUM:
                        switch (health) {
                            case HEALTHY:
                                result = R.drawable.tree_day_medium_healthy;
                                break;
                            case DRYING:
                                result = R.drawable.tree_day_medium_drying;
                                break;
                            case WITHERED:
                                result = R.drawable.tree_day_medium_withered;
                                break;
                        }
                        break;

                    case MATURE:
                        switch (health) {
                            case HEALTHY:
                                result = R.drawable.tree_day_mature_healthy;
                                break;
                            case DRYING:
                                result = R.drawable.tree_day_mature_drying;
                                break;
                            case WITHERED:
                                result = R.drawable.tree_day_mature_withered;
                                break;
                        }
                        break;
                }
                break;

            case Configuration.UI_MODE_NIGHT_YES:
                switch (growth) {
                    case SPROUT:
                        result = R.drawable.tree_night_sprout;
                        break;
                    case SPARKLING:
                        result = R.drawable.tree_night_sparkling;
                        break;

                    case SMALL:
                        switch (health) {
                            case HEALTHY:
                                result = R.drawable.tree_night_small_healthy;
                                break;
                            case DRYING:
                                result = R.drawable.tree_night_small_drying;
                                break;
                            case WITHERED:
                                result = R.drawable.tree_night_small_withered;
                                break;
                        }
                        break;

                    case MEDIUM:
                        switch (health) {
                            case HEALTHY:
                                result = R.drawable.tree_night_medium_healthy;
                                break;
                            case DRYING:
                                result = R.drawable.tree_night_medium_drying;
                                break;
                            case WITHERED:
                                result = R.drawable.tree_night_medium_withered;
                                break;
                        }
                        break;

                    case MATURE:
                        switch (health) {
                            case HEALTHY:
                                result = R.drawable.tree_night_mature_healthy;
                                break;
                            case DRYING:
                                result = R.drawable.tree_night_mature_drying;
                                break;
                            case WITHERED:
                                result = R.drawable.tree_night_mature_withered;
                                break;
                        }
                        break;
                }
                break;
        }

        return result;
    }

    @Override
    public int getItemCount() {
        return this.filteredList.size();
    }

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

    @Override
    public void updateData(@Nullable OrderedRealmCollection<Habit> data) {
        filteredList = data;
        super.updateData(data);
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

    /**
     * Classic habit type view holder
     */
    private static class ClassicViewHolder extends RecyclerView.ViewHolder {

        private final CardView view;
        private final View anchorFab;

        private TextView habitTitle;
        private ImageButton editHabitButton;
        private Button checkButton;
        private Button snoozeButton;
        private MaterialTextView completedLabel;

        private ShapeableImageView treeImageView;
        private ShapeableImageView treeStatus;
        private TreeArcProgress treeProgress;

        public ClassicViewHolder(@NonNull View itemView, @NonNull View anchorFab) {
            super(itemView);

            view = (CardView) itemView;
            this.anchorFab = anchorFab;

            habitTitle = itemView.findViewById(R.id.classicHabitCardTitle);
            completedLabel = itemView.findViewById(R.id.classicHabitCompletedLabel);
            editHabitButton = itemView.findViewById(R.id.classicHabitEditButton);
            snoozeButton = itemView.findViewById(R.id.classicHabitSnoozeButton);
            checkButton = itemView.findViewById(R.id.classicHabitCheckButton);
            treeImageView = itemView.findViewById(R.id.classicHabitTreeImageView);
            treeStatus = itemView.findViewById(R.id.classicHabitTreeStatusImageView);
            treeProgress = itemView.findViewById(R.id.classicHabitTreeProgress);
        }

        public void setHabit(Habit habit, Context context) {
            habitTitle.setText(habit.getTitle());
            Tree tree = habit.getTree();
            if (tree != null) {
                Tree.Growth growth = tree.getGrowth();
                treeImageView.setImageResource(HabitCardAdapter.getTreeAsset(tree, context));
                if (growth == Tree.Growth.SPROUT || growth == Tree.Growth.SPARKLING) {
                    treeProgress.setVisibility(View.INVISIBLE);
                } else {
                    treeProgress.setVisibility(View.VISIBLE);
                    treeProgress.setMax(TreeManager.getRequiredExperience(growth));
                    treeProgress.setProgress(tree.getExperience());
                    treeProgress.setText("");
                    if (treeProgress.getProgress() == treeProgress.getMax()) {
                        treeProgress.setArcAngle(360);
                        treeProgress.setStrokeWidth(10);
                    } else {
                        treeProgress.setArcAngle(250);
                        treeProgress.setStrokeWidth(8);
                    }
                    switch (tree.getHealth()) {
                        case HEALTHY:
                            if (treeProgress.getProgress() == treeProgress.getMax()) {
                                treeProgress.setFinishedStrokeColor(context.getColor(R.color.maxExpColor));
                            } else {
                                treeProgress.setFinishedStrokeColor(context.getColor(R.color.redColor));
                            }
                            break;
                        case DRYING:
                            treeProgress.setFinishedStrokeColor(context.getColor(R.color.dryingExpColor));
                            break;
                        case WITHERED:
                            treeProgress.setFinishedStrokeColor(context.getColor(R.color.witheredExpColor));
                            break;
                        default:
                            break;
                    }
                }
            }
            else {
                treeImageView.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.redColor)));
                treeProgress.setVisibility(View.INVISIBLE);
            }


            if (habit.getIsSnoozed()) {
                completedLabel.setText(R.string.snoozed);
                completedLabel.setTextColor(context.getColor(R.color.secondaryColor));
                treeStatus.setVisibility(View.VISIBLE);
                treeStatus.setImageResource(R.drawable.tree_status_snoozed);
                snoozeButton.setEnabled(false);
                checkButton.setEnabled(false);

            } else if (habit.getRepetitions() != 0) {
                completedLabel.setText(R.string.completed);
                completedLabel.setTextColor(context.getColor(R.color.primaryColor));
                treeStatus.setVisibility(View.VISIBLE);
                treeStatus.setImageResource(R.drawable.tree_status_completed);
                snoozeButton.setEnabled(false);
                checkButton.setEnabled(false);
            } else {
                checkButton.setEnabled(true);
                snoozeButton.setEnabled(true);
                treeStatus.setVisibility(View.INVISIBLE);
            }

            view.setOnClickListener(view -> {
                // When the card is clicked, go to the habit's stats
                Intent intent = new Intent(context, HabitStatsActivity.class);
                intent.putExtra(HabitStatsActivity.EXTRA_HABIT_ID, habit.getId());

                Bundle bundle = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in,
                        android.R.anim.fade_out).toBundle();
                context.startActivity(intent, bundle);
            });

            editHabitButton.setOnClickListener(view -> {
                Intent intent = new Intent(context, EditHabitActivity.class);
                intent.putExtra(EditHabitActivity.EXTRA_HABIT_ID, habit.getId());

                Bundle bundle = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in,
                        android.R.anim.fade_out).toBundle();

                context.startActivity(intent, bundle);
            });

            if (habit.getMaxSnoozes() == 0) {
                snoozeButton.setVisibility(View.INVISIBLE);
            } else {
                snoozeButton.setVisibility(View.VISIBLE);
                snoozeButton.setOnClickListener(view1 -> {
                    if (habit.getSnoozesMade() < habit.getMaxSnoozes()) {
                        habit.getRealm().executeTransaction(realm -> habit.setIsSnoozed(true));

                        Snackbar.make(view, R.string.snackbar_snoozed_label, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(ContextCompat.getColor(context, R.color.primaryColor))
                                .setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                                .setAction(R.string.snackbar_cancel_action, view -> habit.getRealm().executeTransaction(realm -> habit.setIsSnoozed(false)))
                                .setActionTextColor(ContextCompat.getColor(context, R.color.secondaryColor))
                                .setAnchorView(anchorFab)
                                .show();
                    } else {
                        Snackbar.make(view, R.string.habit_cannot_be_snoozed_snackbar, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(ContextCompat.getColor(context, R.color.snackbarDefaultColor))
                                .setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                                .setAnchorView(anchorFab)
                                .show();
                    }
                });
            }

            checkButton.setOnClickListener(view -> {
                if (habit.getRepetitions() < 1) {
                    habit.getRealm().executeTransaction(realm -> habit.setRepetitions(1));

                    Snackbar.make(view, R.string.habit_updated_snackbar, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(context, R.color.primaryColor))
                            .setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                            .setAction(R.string.snackbar_cancel_action, view1 -> habit.getRealm().executeTransaction(realm -> habit.setRepetitions(0)))
                            .setActionTextColor(ContextCompat.getColor(context, R.color.secondaryColor))
                            .setAnchorView(anchorFab)
                            .show();
                }
            });
        }
    }


    /**
     * Counter habit type view holder
     */
    private static class RepetitionViewHolder extends RecyclerView.ViewHolder {

        private final CardView view;
        private final View anchorFab;

        private TextView habitTitle;
        private ProgressBar progressBar;
        private TextView progressLabel;
        private ImageButton editHabitButton;
        private Button checkButton;
        private Button snoozeButton;

        private ShapeableImageView treeImageView;
        private ShapeableImageView treeStatus;
        private TreeArcProgress treeProgress;

        public RepetitionViewHolder(@NonNull View itemView, @NonNull View anchorFab) {
            super(itemView);

            view = (CardView) itemView;
            this.anchorFab = anchorFab;

            habitTitle = itemView.findViewById(R.id.counterHabitCardTitle);
            editHabitButton = itemView.findViewById(R.id.counterHabitEditButton);
            progressBar = itemView.findViewById(R.id.counterHabitProgressBar);
            checkButton = itemView.findViewById(R.id.counterHabitCheckButton);
            snoozeButton = itemView.findViewById(R.id.counterHabitSnoozeButton);
            progressLabel = itemView.findViewById(R.id.counterHabitProgressLabel);
            treeImageView = itemView.findViewById(R.id.counterHabitTreeImageView);
            treeStatus = itemView.findViewById(R.id.counterHabitTreeStatusImageView);
            treeProgress = itemView.findViewById(R.id.counterHabitTreeProgress);
        }

        void setHabit(Habit habit, Context context) {
            this.habitTitle.setText(habit.getTitle());
            this.progressBar.setMax(habit.getMaxRepetitions());
            Tree tree = habit.getTree();

            if (tree != null) {
                Tree.Growth growth = tree.getGrowth();
                treeImageView.setImageResource(HabitCardAdapter.getTreeAsset(tree, context));
                if (growth == Tree.Growth.SPROUT || growth == Tree.Growth.SPARKLING) {
                    treeProgress.setVisibility(View.INVISIBLE);
                } else {
                    treeProgress.setVisibility(View.VISIBLE);
                    treeProgress.setMax(TreeManager.getRequiredExperience(growth));
                    treeProgress.setProgress(tree.getExperience());
                    treeProgress.setText("");
                    if (treeProgress.getProgress() == treeProgress.getMax()) {
                        treeProgress.setArcAngle(360);
                        treeProgress.setStrokeWidth(10);
                    } else {
                        treeProgress.setArcAngle(250);
                        treeProgress.setStrokeWidth(8);
                    }
                    switch (tree.getHealth()) {
                        case HEALTHY:
                            if (treeProgress.getProgress() == treeProgress.getMax()) {
                                treeProgress.setFinishedStrokeColor(context.getColor(R.color.maxExpColor));
                            } else {
                                treeProgress.setFinishedStrokeColor(context.getColor(R.color.redColor));
                            }
                            break;
                        case DRYING:
                            treeProgress.setFinishedStrokeColor(context.getColor(R.color.dryingExpColor));
                            break;
                        case WITHERED:
                            treeProgress.setFinishedStrokeColor(context.getColor(R.color.witheredExpColor));
                            break;
                        default:
                            break;
                    }
                }
            }
            else {
                treeImageView.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.redColor)));
                treeProgress.setVisibility(View.INVISIBLE);
            }


            String message;
            if (habit.getIsSnoozed()) {
                message = "Abitudine rinviata!";
                progressBar.setProgress(habit.getMaxRepetitions());
                progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.secondaryColor)));
                treeStatus.setVisibility(View.VISIBLE);
                treeStatus.setImageResource(R.drawable.tree_status_snoozed);
                checkButton.setEnabled(false);
                snoozeButton.setEnabled(false);
            } else if (habit.getRepetitions() == habit.getMaxRepetitions()) {
                message = "Abitudine completata!";
                progressBar.setProgress(habit.getRepetitions());
                treeStatus.setVisibility(View.VISIBLE);
                treeStatus.setImageResource(R.drawable.tree_status_completed);
                checkButton.setEnabled(false);
                snoozeButton.setEnabled(false);
            } else {
                message = context.getResources().getQuantityString(R.plurals.counterHabitRepetitionsLabel, habit.getRepetitions(), habit.getRepetitions(), habit.getMaxRepetitions());
                progressBar.setProgress(habit.getRepetitions());
                progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.primaryColor)));
                treeStatus.setVisibility(View.INVISIBLE);
                checkButton.setEnabled(true);
                snoozeButton.setEnabled(true);
            }
            this.progressLabel.setText(message);

            view.setOnClickListener(view -> {
                // When the card is clicked, go to the habit's stats
                Intent intent = new Intent(context, HabitStatsActivity.class);
                intent.putExtra(HabitStatsActivity.EXTRA_HABIT_ID, habit.getId());

                Bundle bundle = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in,
                        android.R.anim.fade_out).toBundle();
                context.startActivity(intent, bundle);
            });

            editHabitButton.setOnClickListener(view -> {
                Intent intent = new Intent(context, EditHabitActivity.class);
                intent.putExtra(EditHabitActivity.EXTRA_HABIT_ID, habit.getId());

                Bundle bundle = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in,
                        android.R.anim.fade_out).toBundle();

                context.startActivity(intent, bundle);
            });

            if (habit.getMaxSnoozes() == 0) {
                snoozeButton.setVisibility(View.INVISIBLE);
            } else {
                snoozeButton.setVisibility(View.VISIBLE);
                snoozeButton.setOnClickListener(view1 -> {
                    if (habit.getSnoozesMade() < habit.getMaxSnoozes()) {
                        habit.getRealm().executeTransaction(realm -> habit.setIsSnoozed(true));

                        Snackbar.make(view, R.string.snackbar_snoozed_label, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(ContextCompat.getColor(context, R.color.primaryColor))
                                .setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                                .setAction(R.string.snackbar_cancel_action, view -> habit.getRealm().executeTransaction(realm -> habit.setIsSnoozed(false)))
                                .setActionTextColor(ContextCompat.getColor(context, R.color.secondaryColor))
                                .setAnchorView(anchorFab)
                                .show();
                    } else {
                        Snackbar.make(view, R.string.habit_cannot_be_snoozed_snackbar, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(ContextCompat.getColor(context, R.color.snackbarDefaultColor))
                                .setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                                .setAnchorView(anchorFab)
                                .show();
                    }
                });
            }

            checkButton.setOnLongClickListener(view1 -> {
                int missingTasks = habit.getMaxRepetitions() - habit.getRepetitions();
                FragmentManager fragmentManager = ((FragmentActivity) view1.getContext()).getSupportFragmentManager();
                RepetitionHabitNumberPickerFragment picker = new RepetitionHabitNumberPickerFragment(missingTasks, habit.getId());
                picker.show(fragmentManager, "numberPicker");
                return true;
            });

            checkButton.setOnClickListener(view -> {
                int currRep = habit.getRepetitions();
                int maxReps = habit.getMaxRepetitions();
                if (currRep < maxReps) {
                    habit.getRealm().executeTransaction(realm ->  habit.setRepetitions(currRep + 1) );

                    Snackbar.make(view, R.string.habit_updated_snackbar, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(context, R.color.primaryColor))
                            .setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                            .setAction(R.string.snackbar_cancel_action, view1 -> habit.getRealm().executeTransaction(realm -> habit.setRepetitions(currRep)))
                            .setActionTextColor(ContextCompat.getColor(context, R.color.secondaryColor))
                            .setAnchorView(anchorFab)
                            .show();
                }
            });
        }
    }
}