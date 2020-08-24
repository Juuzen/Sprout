package model;

import com.hcifedii.sprout.enumerations.GoalType;
import com.hcifedii.sprout.enumerations.HabitType;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import utils.GoalEnum;
import utils.HabitTypeEnum;

// TODO: potrebbe essere necessario un costruttore per inizializzare i vari campi enum / list

public class Habit extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Habit type + repetitions
    private HabitTypeEnum habitTypeEnum = new HabitTypeEnum();

    public HabitType getHabitType() {
        return habitTypeEnum.getEnum();
    }

    public void setHabitType(HabitType habitType) {
        habitTypeEnum.saveType(habitType);
    }

    private int repetitions = 1;    // 1 default value

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
    // Reminders
    private RealmList<Reminder> reminders;

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(RealmList<Reminder> reminders) {
        this.reminders = reminders;
    }

    // Snoozes
    private int maxSnoozes = 0;

    public int getMaxSnoozes() {
        return maxSnoozes;
    }

    public void setMaxSnoozes(int maxSnoozes) {
        this.maxSnoozes = maxSnoozes;
    }

    // Goal
    private GoalEnum goalType = new GoalEnum();

    public GoalType getGoalType() {
        return goalType.getEnum();
    }

    public void setGoalType(GoalType val) {
        goalType.saveType(val);
    }

    // Goal -- Max completed actions TODO
    private int maxCompletedActions;

    public int getMaxCompletedActions() {
        return maxCompletedActions;
    }

    public void setMaxCompletedActions(int maxCompletedActions) {
        this.maxCompletedActions = maxCompletedActions;
    }

    // Goal -- Max streak value TODO
    private int maxStreakValue;

    public int getMaxStreakValue() {
        return maxStreakValue;
    }

    public void setMaxStreakValue(int maxStreakValue) {
        this.maxStreakValue = maxStreakValue;
    }

    // Goal -- Deadline TODO
    private Date finalDate;

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }


    public RealmList<Task> getTaskHistory() {
        return null;
    }

    public void setTaskHistory(RealmList<Task> taskHistory) {
        this.taskHistory = taskHistory;
    }

    // Icon showed inside the preset habit view
    private int imageResId;

    public int getImage() {
        return imageResId;
    }

    public void setImage(int image) {
        this.imageResId = image;
    }

    // Stats data TODO
    private RealmList<Task> taskHistory;

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
}
