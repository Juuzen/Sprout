package utils;

import com.hcifedii.sprout.enumerations.Days;

public class DaysEnum {

    private String day = Days.MONDAY.name();

    public void saveType(Days val) {
        this.day = val.name();
    }

    public Days getEnum() {
        return Days.valueOf(day);
    }

}
