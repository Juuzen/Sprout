package utils;

import com.hcifedii.sprout.enumerations.Days;

import io.realm.RealmObject;

public class DaysEnum extends RealmObject {

    private String day = Days.MONDAY.name();

    public void saveType(Days val) {
        this.day = val.name();
    }

    public Days getEnum() {
        return Days.valueOf(day);
    }

}
