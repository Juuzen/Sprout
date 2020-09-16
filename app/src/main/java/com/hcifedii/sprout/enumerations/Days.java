package com.hcifedii.sprout.enumerations;

import androidx.annotation.NonNull;

import java.util.Calendar;

public enum Days {

    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static Days today(@NonNull Calendar calendar) {
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch (today) {
            default:
                return Days.MONDAY;
            case Calendar.TUESDAY:
                return Days.TUESDAY;
            case Calendar.WEDNESDAY:
                return Days.WEDNESDAY;
            case Calendar.THURSDAY:
                return Days.THURSDAY;
            case Calendar.FRIDAY:
                return Days.FRIDAY;
            case Calendar.SATURDAY:
                return Days.SATURDAY;
            case Calendar.SUNDAY:
                return Days.SUNDAY;
        }
    }

}
