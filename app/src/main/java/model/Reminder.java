package model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

import io.realm.RealmObject;

public class Reminder extends RealmObject implements Serializable {

    private int hours;
    private int minutes;
    private boolean isActive = true;

    private boolean is24HourFormat;
    private int requestCode;

    // Default constructor for Realm
    public Reminder() {
    }

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
        return String.valueOf(hours) + ':' + String.format(Locale.getDefault(), "%02d", minutes);
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

        time.append(String.format(Locale.getDefault(), "%02d", minutes));

        time.append(' ').append(timeSet);

        return time.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + hours + ":" + minutes + ", isActive=" + isActive + ", is24Hour=" + is24HourFormat + ", requestCode=" + requestCode + "]";
    }

    public void setAlarmRequestCode(int alarmRequestCode) {
        this.requestCode = alarmRequestCode;
    }

    public int getAlarmRequestCode() {
        return requestCode;
    }

    public boolean isInThePast(int otherHours, int otherMinutes) {
        return this.hours < otherHours || this.minutes < otherMinutes;
    }
}
