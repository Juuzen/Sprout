package model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Reminder implements Serializable {

    private int hours;
    private int minutes;
    private boolean isActive = true;

    private boolean is24HourFormat;

    public Reminder(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void is24HourFormat(boolean is24Hour) {
        this.is24HourFormat = is24Hour;
    }

    public static String buildFormattedTimeString(int hour, int minutes, boolean is24HourFormat) {

        if (is24HourFormat)
            return get24HourFormattedString(hour, minutes);

        return get12HourFormattedString(hour, minutes);
    }

    private static String get24HourFormattedString(int hours, int minutes) {

        StringBuilder time = new StringBuilder();

        time.append(hours);

        time.append(':');

        if (minutes < 10) time.append('0');

        time.append(minutes);

        return time.toString();
    }

    private static String get12HourFormattedString(int hour, int minutes) {

        StringBuilder time = new StringBuilder();
        String timeSet;

        if (hour >= 12) timeSet = "PM";
        else if (hour == 0) timeSet = "AM";
        else timeSet = "AM";

        if (hour > 12) time.append(hour - 12);
        else if (hour == 0) time.append(hour + 12);
        else time.append(hour);

        time.append(':');

        if (minutes < 10) time.append('0');

        time.append(minutes);
        time.append(' ').append(timeSet);

        return time.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + hours + ":" + minutes + ", isActive=" + isActive + ", is24Hour=" + is24HourFormat + "]";
    }

}
