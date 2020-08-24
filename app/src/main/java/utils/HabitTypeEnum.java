package utils;

import com.hcifedii.sprout.enumerations.HabitType;

import io.realm.RealmObject;

/**
 * Need a class to save the enum as a String and to get it back as an enum
 */
public class HabitTypeEnum extends RealmObject {
    private String habitType = HabitType.CLASSIC.name();

    public void saveType(HabitType val) {
        this.habitType = val.toString();
    }

    public HabitType getEnum() {
        return HabitType.valueOf(habitType);
    }
}
