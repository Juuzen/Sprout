package com.hcifedii.sprout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.google.android.material.textview.MaterialTextView;
import com.hcifedii.sprout.enumerations.GoalType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Habit;
import model.Task;
import utils.HabitRealmManager;
import model.Streak;

public class HabitStatsActivity extends SproutApplication {

    public static final String EXTRA_HABIT_ID = "habitID";

    private int habitId;

    // Calendar related fields
    private CalendarView calendarView;
    private List<Calendar> highlight;
    private List<EventDay> events;

    // Charts related fields
    private Map<Integer, Integer> actionMap;
    private List<Streak> streakList;

    private static final DecimalFormat format = new DecimalFormat("###");


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (habitId >= 0)
            outState.putInt(EXTRA_HABIT_ID, habitId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_stats);

        enableTopBackButton();

        habitId = getHabitIdFromBundles(savedInstanceState);

        if (habitId >= 0) {
            // Using a different thread for ui operations
            this.runOnUiThread(() -> {

                // Get habit from database
                Habit habit = HabitRealmManager.getHabit(habitId);

                if (habit == null) {
                    Log.e(this.getClass().getSimpleName(), "habit == null");
                    finish();
                    return;
                }
                // Set Activity's title
                setTitle(habit.getTitle());

                // Habit info card
                MaterialTextView habitTypeTextView = findViewById(R.id.habitTypeTextView);
                habitTypeTextView.setText(habit.getHabitType().getStringResourceId());

                MaterialTextView goalTypeTextView = findViewById(R.id.goalTextView);
                goalTypeTextView.setText(habit.getGoalType().getStringResourceId());

                // Calendar and Charts

                // Get the Calendar
                calendarView = findViewById(R.id.calendarView);

                // List of icons that will be showed inside the calendar
                events = new ArrayList<>();

                // List of the days that should be highlighted
                highlight = new ArrayList<>();

                // List of all the streaks
                streakList = new ArrayList<>();

                // Mark creation date inside the calendar
                markCreationDate(habit.getHabitCreationDate());
                // Mark end date inside the calendar
                if (habit.getGoalType() == GoalType.DEADLINE)
                    markEndDate(habit.getFinalDate());


                extractDataFromTaskHistory(habit.getTaskHistory());

                // Set the highlighted days and icons on the calendarView
                calendarView.setEvents(events);
                calendarView.setHighlightedDays(highlight);

                // Draw charts
                drawStreakChart();
                drawMonthlyChart();
            });

        } else {
            Log.e(this.getClass().getSimpleName(), "Habit id < 0");
            finish();
        }
    }


    private void markCreationDate(long habitCreationDate) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(habitCreationDate);
        calendarView.setMinimumDate(startDate);

        Drawable startIcon = getIcon(R.drawable.ic_add_24, R.color.primaryColor);
        events.add(new EventDay(startDate, startIcon));
    }

    private void markEndDate(long finalDate) {
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(finalDate);
        calendarView.setMaximumDate(endDate);

        Drawable endIcon = getIcon(R.drawable.ic_archive_24, R.color.primaryColor);
        events.add(new EventDay(endDate, endIcon));
    }

    /**
     * Draw a chart displaying the actions the user did every month for this habit.
     */
    private void drawMonthlyChart() {

        LineChart chart = findViewById(R.id.monthlyActionChart);

        setNoDataAvailableMessage(chart);

        if (actionMap == null || actionMap.size() < 12) {
            Log.e(this.getClass().getSimpleName(), "Invalid Action map");
            return;
        }

        // X-axis labels
        final String[] months = getResources().getStringArray(R.array.months);

        List<Entry> entries = new ArrayList<>();

        // Data
        float index = 0;
        for (Integer key : actionMap.keySet()) {
            Integer actions = actionMap.getOrDefault(key, 0);

            if (actions != null)
                entries.add(new Entry(index++, actions));
            else
                entries.add(new Entry(index++, 0));

            if (index > 11)
                break;
        }

        LineDataSet lineDataSet = new LineDataSet(entries, getString(R.string.month));

        lineDataSet.setColor(getColor(R.color.secondaryColor));
        lineDataSet.setCircleColor(getColor(R.color.secondaryColor));
        lineDataSet.setValueTextColor(getColor(R.color.primaryTextColor));
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return format.format(entry.getY());
            }
        });

        LineData lineData = new LineData(lineDataSet);

        chart.animateX(2000);

        setChartAttributes(chart);

        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return months[(int) value];
            }
        });

        chart.setData(lineData);
    }

    // TODO: i dati devono essere ordinati per numero di azioni in modo crescente
    // TODO: ordinare i dati prima di mostrarli
    // TODO: max 4
    private void drawStreakChart() {

        BarChart chart = findViewById(R.id.streakChart);

        setNoDataAvailableMessage(chart);

        if (streakList == null || streakList.size() <= 0) {
            return;
        }

        List<BarEntry> entries = new ArrayList<>();

        int index = 0;
        List<String> labels = new ArrayList<>();
        for (Streak streak : streakList) {

            entries.add(new BarEntry(index++, streak.getNumDay()));
            labels.add(streak.getLabel());

            // TODO: terminare il ciclo quando index > 4
        }

        while (index <= 4) {
            // Fill the last positions with empty bars
            entries.add(new BarEntry(index++, 0));
            labels.add("");
        }

        BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.days));

        final int[] chartColor = new int[]{
                getColor(R.color.primaryLightColor),
                getColor(R.color.primaryColor),
                getColor(R.color.secondaryLightColor),
                getColor(R.color.secondaryColor)
        };

        barDataSet.setColors(chartColor);

        barDataSet.setValueTextColor(getColor(R.color.primaryTextColor));
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {

                float value = barEntry.getY();
                if (value == 1)         // Write singular
                    return format.format(value) + ' ' + getString(R.string.day);
                else if (value == 0)    // No label
                    return "";

                return format.format(value) + ' ' + getString(R.string.days);
            }
        });

        BarData barData = new BarData(barDataSet);

        setChartAttributes(chart);

        chart.animateY(2000);

        XAxis xAxis = chart.getXAxis();

        xAxis.setTextSize(7);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(5, true);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        });

        chart.setData(barData);
    }

    /**
     * Set the default message to display inside a chart when no data is available
     *
     * @param chart The chart you want to set the message
     */
    private void setNoDataAvailableMessage(@NonNull BarLineChartBase<?> chart) {
        chart.setNoDataTextColor(getColor(R.color.primaryTextColor));
        chart.setNoDataText(getString(R.string.no_data_available));
    }

    /**
     * Set some common attributes to the two charts
     *
     * @param chart The chart you want to set the attributes
     */
    private void setChartAttributes(@NonNull BarLineChartBase<?> chart) {

        int primaryTextColor = getColor(R.color.primaryTextColor);

        chart.getXAxis().setTextColor(primaryTextColor);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setTextColor(primaryTextColor);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return Integer.toString((int) value);
            }
        });

        chart.setPinchZoom(false);
        chart.getLegend().setEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getDescription().setEnabled(false);
    }

    private int getHabitIdFromBundles(Bundle savedInstance) {

        int id = -1;
        if (savedInstance != null) {
            id = savedInstance.getInt(EXTRA_HABIT_ID, -1);
        } else {
            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                id = extra.getInt(EXTRA_HABIT_ID, -1);
            }
        }
        return id;
    }

    /**
     *
     */
    private void extractDataFromTaskHistory(@NonNull List<Task> tasks) {

        Calendar calendar = Calendar.getInstance();

        List<Task> taskHistory = new ArrayList<>();

        Task task1 = new Task(), task2 = new Task(), task3 = new Task(), task4 = new Task(), task5 = new Task();


        {
            calendar.set(Calendar.DAY_OF_YEAR, 260);

            task1.setTaskDate(calendar.getTimeInMillis());
            task1.setTaskStatus(Task.Status.PASSED);

            calendar.set(Calendar.DAY_OF_YEAR, 261);

            task2.setTaskDate(calendar.getTimeInMillis());
            task2.setTaskStatus(Task.Status.SNOOZED);

            calendar.set(Calendar.DAY_OF_YEAR, 262);

            task3.setTaskDate(calendar.getTimeInMillis());
            task3.setTaskStatus(Task.Status.PASSED);

            calendar.set(Calendar.DAY_OF_YEAR, 263);

            task4.setTaskDate(calendar.getTimeInMillis());
            task4.setTaskStatus(Task.Status.FAILED);

            calendar.set(Calendar.DAY_OF_YEAR, 200);

            task5.setTaskDate(calendar.getTimeInMillis());
            task5.setTaskStatus(Task.Status.PASSED);

            calendar.set(Calendar.DAY_OF_YEAR, 201);

            Task task6 = new Task();
            task6.setTaskDate(calendar.getTimeInMillis());
            task6.setTaskStatus(Task.Status.FAILED);

            taskHistory.add(task1);
            taskHistory.add(task2);
            taskHistory.add(task3);
            taskHistory.add(task4);
            taskHistory.add(task5);
            taskHistory.add(task6);
        }

        //taskHistory.sort((task, t1) -> Long.compare(task.getTaskDate(), t1.getTaskDate()));
        // Per le icone da mostrare nel calendario (rinvii e fallimenti)


        int monthAction = 0;
        // Una lista di max 4 elementi con le serie migliori

        actionMap = getInstanceOfActionMap();


        int lastMonth = Calendar.JANUARY;
        int currentMonth = Calendar.JANUARY;

        long minDayStreak = 0;

        Calendar cal = null;

        for (Task task : taskHistory) {

            cal = Calendar.getInstance();
            cal.setTimeInMillis(task.getTaskDate());


            // Get the month
            currentMonth = cal.get(Calendar.MONTH);
            if (currentMonth != lastMonth) {
                // Updating monthly actions
                actionMap.put(lastMonth, monthAction);

                lastMonth = currentMonth;
                monthAction = 0;
            }

            if (minDayStreak <= 0) {
                minDayStreak = cal.getTimeInMillis();
            }

            // TODO: controllare la differenza in giorni tra il task corrente e il task precedente. Se > 1 (non si dovrebbe verificare), allora fare qualcosa

            switch (task.getTaskStatus()) {
                case SNOOZED:
                    // Snoozed task are highlighted and have a small icons underneath the date
                    Drawable icon = getIcon(R.drawable.ic_sprout_small, R.color.secondaryColor);
                    events.add(new EventDay(cal, icon));
                case PASSED:
                    // Passed task are just highlighted
                    highlight.add(cal);
                    monthAction++;
                    break;

                default:
                    // Failed task are not highlighted. Instead, they have a red icon
                    Drawable failedIcon = getIcon(R.drawable.ic_clear_24dp, R.color.redColor);
                    events.add(new EventDay(cal, failedIcon));


                    streakList.add(new Streak(minDayStreak, cal.getTimeInMillis()));

                    minDayStreak = 0;


                    break;

            }


        }

        actionMap.put(currentMonth, monthAction);

        // TODO: Controllare le condizioni di terminazione
        if (cal != null)
            streakList.add(new Streak(minDayStreak, cal.getTimeInMillis()));



        Log.e("AASDA", Arrays.toString(streakList.toArray()));


    }

    private static Map<Integer, Integer> getInstanceOfActionMap() {

        Map<Integer, Integer> map = new HashMap<>();
        map.put(Calendar.JANUARY, 0);
        map.put(Calendar.FEBRUARY, 0);
        map.put(Calendar.MARCH, 0);
        map.put(Calendar.APRIL, 0);
        map.put(Calendar.MAY, 0);
        map.put(Calendar.JUNE, 0);
        map.put(Calendar.JULY, 0);
        map.put(Calendar.AUGUST, 0);
        map.put(Calendar.SEPTEMBER, 0);
        map.put(Calendar.OCTOBER, 0);
        map.put(Calendar.NOVEMBER, 0);
        map.put(Calendar.DECEMBER, 0);

        return map;
    }

    private Drawable getIcon(int drawableResId, int colorResId) {

        Drawable icon = ContextCompat.getDrawable(this, drawableResId);
        if (icon != null)
            icon.setTint(getResources().getColor(colorResId, getTheme()));

        return icon;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar_habit_stats, menu);
        return true;
    }

}