package model;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Streak implements Comparable<Streak> {

    private final int numDay;
    private final String label;

    /**
     * Create a new Streak object from minDay to the current day (excluded)
     *
     * @param numDay     The number of the days for this streak
     * @param minDay     Starting day of the streak
     * @param currentDay Current day of the streak
     */
    public Streak(int numDay, long minDay, long currentDay) {

        this.numDay = numDay;

        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minDay);

        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(currentDay);

        // Label showed inside the chart
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        if (numDay < 2) {
            // If it's just one day, then the label will have only that day displayed
            label = dayFormat.format(min.getTime());

        } else {
            max.set(Calendar.DAY_OF_YEAR, max.get(Calendar.DAY_OF_YEAR) - 1);

            label = dayFormat.format(min.getTime()).concat(" - ") +
                    dayFormat.format(max.getTime());
        }
    }

    public int getNumDay() {
        return numDay;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int compareTo(Streak otherStreak) {
        return Integer.compare(this.numDay, otherStreak.numDay);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Streak))
            return false;

        Streak otherStreak = (Streak) other;
        return this.numDay == otherStreak.numDay;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numDay);
    }

    @NonNull
    @Override
    public String toString() {
        return '[' + numDay + " days, " + label + ']';
    }
}
