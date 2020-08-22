package utils;

import com.hcifedii.sprout.Days;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class DaysEnum {

    private String day;

    public void saveType(Days val) {
        this.day = val.name();
    }

    public Days getEnum() {
        return Days.valueOf(day);
    }

}
