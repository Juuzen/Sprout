package model;

import androidx.annotation.Nullable;

import com.hcifedii.sprout.Days;
import com.hcifedii.sprout.HabitType;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import utils.DaysEnum;
import utils.HabitTypeEnum;

public
class Habit extends RealmObject {

    @PrimaryKey
    private int id;

    // Title
    @Required
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Habit type + repetitions
    // TODO: aggiungere un costruttore
    private HabitTypeEnum habitTypeEnum = new HabitTypeEnum();

    public HabitType getHabitType() {
        return habitTypeEnum.getEnum();
    }

    public void setHabitType(HabitType habitType) {
        habitTypeEnum.saveType(habitType);
    }

    @Required
    private int repetitions = 1;

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    // Week frequency
//    private List<Days> frequency;
//
//    public List<Days> getFrequency() {
//        return frequency;
//    }
//
//    public void setFrequency(List<Days> frequency) {
//        this.frequency = frequency;
//    }
//
//    // Reminders
//    private List<Reminder> reminders;
//
//    public List<Reminder> getReminders() {
//        return reminders;
//    }
//
//    public void setReminders(List<Reminder> reminders) {
//        this.reminders = reminders;
//    }


    // Snoozes
    @Nullable
    private int maxSnoozes;

    public int getMaxSnoozes() {
        return maxSnoozes;
    }

    public void setMaxSnoozes(int maxSnoozes) {
        this.maxSnoozes = maxSnoozes;
    }

    // Goal
    //private ObjectiveType objectiveType; //TODO: Use the util

    private int maxCompletedTasks;
    private int maxStreakValue;
    private Date finalDate;

    // Stats data
    private RealmList<Task> taskHistory;

    private int bestStreak;
    private int currentStreak;
    private int completedTasks;


    public int getImage() {
        return imageResId;
    }

    public void setImage(int image) {
        this.imageResId = image;
    }

    private int imageResId;


    public int getMaxCompletedTasks() {
        return maxCompletedTasks;
    }

    public void setMaxCompletedTasks(int maxCompletedTasks) {
        this.maxCompletedTasks = maxCompletedTasks;
    }

    public int getMaxStreakValue() {
        return maxStreakValue;
    }

    public void setMaxStreakValue(int maxStreakValue) {
        this.maxStreakValue = maxStreakValue;
    }

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

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
}
