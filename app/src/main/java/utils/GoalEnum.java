package utils;

import com.hcifedii.sprout.enumerations.GoalType;

import io.realm.RealmObject;

public class GoalEnum extends RealmObject {
    private String goal = GoalType.NONE.name();

    public void saveType(GoalType val) {
        this.goal = val.toString();
    }

    public GoalType getEnum() {
        return GoalType.valueOf(goal);
    }
}
