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
import utils.DaysEnum;
import utils.GoalEnum;
import utils.HabitTypeEnum;

public class Habit extends RealmObject {

    public Habit() {
        id = -1;
        habitCreationDate = Calendar.getInstance().getTimeInMillis();
        habitTypeEnum = new HabitTypeEnum();
        goalType = new GoalEnum();
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
     * Type of the habit (classic or counter)
     */
    private HabitTypeEnum habitTypeEnum;
    public HabitType getHabitType() {
        return habitTypeEnum.getEnum();
    }
    public void setHabitType(HabitType habitType) {
        habitTypeEnum.saveType(habitType);
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
    public int getMaxRepetitions() { return maxRepetitions; }
    public void setMaxRepetitions(int maxRepetitions) { this.maxRepetitions = maxRepetitions; }

    // Week frequency
    private RealmList<DaysEnum> frequency;

    public List<Days> getFrequency() {
        List<Days> output = new ArrayList<>();
        // Convert the RealmList of DaysEnum to a List of Days
        for (DaysEnum en : frequency)
            output.add(en.getEnum());
        return output;
    }

    public void setFrequency(List<Days> input) {

        if (frequency.size() > 0)
            frequency.clear();
        // Convert the List of Days to a RealmList of DaysEnum
        for (Days da : input) {
            DaysEnum daysEnum = new DaysEnum();
            daysEnum.saveType(da);
            frequency.add(daysEnum);
        }
    }

    // Reminders
    private RealmList<Reminder> reminders;

    public RealmList<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(RealmList<Reminder> reminders) {
        this.reminders = reminders;
    }

    // Snoozed status
    private boolean isSnoozed = false;

    public boolean getIsSnoozed() { return isSnoozed; }

    public void setIsSnoozed(boolean snoozed) { isSnoozed = snoozed; }

    // Snoozed made during a X (week/month/etc)
    private int snoozedMade = 0;

    public int getSnoozedMade() { return snoozedMade; }

    public void setSnoozedMade(int snoozed) { snoozedMade = snoozed; }

    // Max snoozes (in a week? in a month?) TODO: specificare la logica per i rinvii
    private int maxSnoozes = 0;

    public int getMaxSnoozes() {
        return maxSnoozes;
    }

    public void setMaxSnoozes(int maxSnoozes) {
        this.maxSnoozes = maxSnoozes;
    }

    // Goal
    private GoalEnum goalType;

    public GoalType getGoalType() {
        return goalType.getEnum();
    }

    public void setGoalType(GoalType val) {
        goalType.saveType(val);
    }

    // TODO: volendo potremmo ridurre i campi interi di goal (maxAction e maxStreakValue) ad
    // un singolo valore intero (visto che un goal può essere di una sola tipologia alla volta) ed usare
    // goalType per decidere quale valore è al momento utilizzato.

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
        return null;
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
