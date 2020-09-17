package model;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Streak implements Comparable<Streak> {

    private final int numDay;

    private final String label;

    /**
     * Create a new Streak object from minDay to the failed day (excluded)
     *
     * @param minDay    Starting day of the streak
     * @param failedDay Failed day of the streak
     */
    public Streak(long minDay, long failedDay) {

        // Difference between two long to get the number of Days
        // NOTE: From API 26 there is a better way for doing this.
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(minDay);
        min.set(Calendar.HOUR_OF_DAY, 0);
        min.set(Calendar.MINUTE, 0);
        min.set(Calendar.SECOND, 0);
        min.set(Calendar.MILLISECOND, 0);

        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(failedDay);
        max.set(Calendar.HOUR_OF_DAY, 0);
        max.set(Calendar.MINUTE, 0);
        max.set(Calendar.SECOND, 0);
        max.set(Calendar.MILLISECOND, 0);

        long diffInMilis = max.getTimeInMillis() - min.getTimeInMillis();

        numDay = (int) (diffInMilis / (24 * 60 * 60 * 1000));

        // Label showed inside the chart
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MMM", Locale.getDefault());

        max.set(Calendar.DAY_OF_YEAR, max.get(Calendar.DAY_OF_YEAR) - 1);

        label = dayFormat.format(min.getTime()).concat(" - ") +
                dayFormat.format(max.getTime());
    }

    public int getNumDay() {
        return numDay;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int compareTo(Streak streak) {
        return Integer.compare(numDay, streak.numDay);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Streak))
            return false;

        Streak streak = (Streak) other;
        return numDay == streak.numDay;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numDay);
    }

    @NonNull
    @Override
    public String toString() {
        return numDay + " days, " + label;
    }
}
