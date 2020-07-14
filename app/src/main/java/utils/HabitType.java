package utils;

import io.realm.RealmObject;

enum habitEnum {
    CLASSIC, COUNTER
}

public class HabitType extends RealmObject {
    private String habitType;

    public void saveType(habitEnum val) {
        this.habitType = val.toString();
    }

    public habitEnum getEnum() {
        return habitEnum.valueOf(habitType);
    }
}
