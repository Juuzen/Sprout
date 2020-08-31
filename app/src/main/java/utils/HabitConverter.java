package utils;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;

public class HabitConverter {
    Realm realm;
    RealmResults<Habit> habits;

    public HabitConverter(Realm realm) {
        this.realm = realm;
    }

    public void loadItemsFromDB() {
        habits = realm.where(Habit.class).findAll();
    }

    public ArrayList<Habit> getList() {
        return new ArrayList<>(habits);
    }

    public int getListSize() {
        return habits.size();
    }
}
