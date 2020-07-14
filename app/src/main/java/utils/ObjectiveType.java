package utils;

import io.realm.RealmObject;

enum objectiveEnum {
    NONE, MAXSTREAK, MAXTASKS, FINALDATE
}

public class ObjectiveType extends RealmObject {
    private String objectiveType;

    public void saveType(objectiveEnum val) {
        this.objectiveType = val.toString();
    }

    public objectiveEnum getEnum() {
        return objectiveEnum.valueOf(objectiveType);
    }
}
