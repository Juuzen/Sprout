package model;

import com.hcifedii.sprout.enumerations.GoalType;
import com.hcifedii.sprout.enumerations.HabitType;
import com.hcifedii.sprout.enumerations.Days;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Habit extends RealmObject {

    public Habit() {
        id = -1;
        habitCreationDate = Calendar.getInstance().getTimeInMillis();
        frequency = new RealmList<>();
    }

    @PrimaryKey
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Title of the habit
     */
    @Required
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Boolean that say if the habit is completed. A completed habit won't be shown inside the
     * MainActivity.
     */
    private boolean isCompleted = false;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * Type of the habit (classic or counter)
     */
    private String habitType = HabitType.CLASSIC.name();

    public HabitType getHabitType() {
        return HabitType.valueOf(habitType);
    }

    public void setHabitType(HabitType habitType) {
        this.habitType = habitType.name();
    }

    /**
     * Each time the user complete an action.
     */
    private int repetitions = 0;

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    private int maxRepetitions = 1; //if the habitType is "COUNTER", then this should be replaced with the new value

    public int getMaxRepetitions() {
        return maxRepetitions;
    }

    public void setMaxRepetitions(int maxRepetitions) {
        this.maxRepetitions = maxRepetitions;
    }

    private String frequencyTest;

    public String getFrequencyTest() { return frequencyTest; }

    public void setFrequencyTest(List<Days> input) {
        frequencyTest = "";
        StringBuilder tmp = new StringBuilder();
        for (Days day : input) {
            tmp.append(day.name());
        }
        frequencyTest = tmp.toString();
    }


    // Week frequency
    private RealmList<String> frequency;

    public List<Days> getFrequency() {

        List<Days> output = new ArrayList<>();
        // Convert the RealmList of String to a List of Days
        for (String day : frequency)
            output.add(Days.valueOf(day));
        return output;
    }

    public void setFrequency(List<Days> input) {
        if (frequency.size() > 0)
            frequency.clear();
        // Convert the List of Days to a RealmList of String
        for (Days day : input)
            frequency.add(day.name());
    }

    // Reminders
    private RealmList<Reminder> reminders;
    public RealmList<Reminder> getReminders() {
        return reminders;
    }
    public void setReminders(RealmList<Reminder> reminders) {
        this.reminders = reminders;
    }

    // Tree
    private Tree tree;
    public Tree getTree() { return tree; }
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    // Snoozed status
    private boolean isSnoozed = false;

    public boolean getIsSnoozed() { return isSnoozed; }
    public void setIsSnoozed(boolean snoozed) { isSnoozed = snoozed; }

    // Snoozed made during a week
    private int snoozesMade = 0;
    public int getSnoozesMade() { return snoozesMade; }
    public void setSnoozesMade(int snoozed) { snoozesMade = snoozed; }


    // Max snoozes in a week
    private int maxSnoozes = 0;
    public int getMaxSnoozes() {
        return maxSnoozes;
    }
    public void setMaxSnoozes(int maxSnoozes) {
        this.maxSnoozes = maxSnoozes;
    }

    private int snoozesPassedDays = 0;
    public int getSnoozesPassedDays() { return snoozesPassedDays; }
    public void setSnoozesPassedDays(int snoozesPassedDays) { this.snoozesPassedDays = snoozesPassedDays; }

    // Goal
    private String goalType = GoalType.NONE.name();
    public GoalType getGoalType() {
        return GoalType.valueOf(goalType);
    }
    public void setGoalType(GoalType val) {
        goalType = val.name();
    }

    private long goalValue = 0;
    public long getGoalValue() { return goalValue; }
    public void setGoalValue(long value) { this.goalValue = value; }

    // Max completed actions while having an habit TODO: col counter, gestire la logica
    private int maxAction;
    public int getMaxAction() {
        return maxAction;
    }
    public void setMaxAction(int maxAction) {
        this.maxAction = maxAction;
    }

    // Max value of streak while having an habit
    private int maxStreakValue;
    public int getMaxStreakValue() {
        return maxStreakValue;
    }
    public void setMaxStreakValue(int maxStreakValue) {
        this.maxStreakValue = maxStreakValue;
    }

    // Goal -- Deadline
    private long finalDate;     // Deadline in milliseconds
    public long getFinalDate() {
        return finalDate;
    }
    public void setFinalDate(long finalDate) {
        this.finalDate = finalDate;
    }

    // Icon showed inside the preset habit view
    private int imageResId;
    public int getImage() {
        return imageResId;
    }
    public void setImage(int image) {
        this.imageResId = image;
    }

    // Stats data
    private RealmList<Task> taskHistory;
    public RealmList<Task> getTaskHistory() {
        return taskHistory;
    }
    public void addTaskToHistory(Task task) {
        taskHistory.add(task);
    }
    public void setTaskHistory(RealmList<Task> taskHistory) {
        this.taskHistory = taskHistory;
    }

    private long habitCreationDate;     // Creation date in milliseconds
    public long getHabitCreationDate() {
        return habitCreationDate;
    }
    /*
    public void setHabitCreationDate(long date) {
        habitCreationDate = date;
    }
     */

    /*
    private int bestStreak;
    private int currentStreak;
    private int completedTasks;

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }
     */


}
