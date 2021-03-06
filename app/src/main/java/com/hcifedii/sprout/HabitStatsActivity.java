package com.hcifedii.sprout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Habit;
import model.Task;
import model.Tree;
import utils.HabitRealmManager;
import model.Streak;
import utils.TaskRealmManager;
import utils.TreeRealmManager;

public class HabitStatsActivity extends SproutApplication {

    public static final String EXTRA_HABIT_ID = "habitID";

    private int habitId;
    private boolean isHabitArchived = false;

    // Calendar related fields
    private CalendarView calendarView;
    private List<Calendar> highlight;
    private List<EventDay> events;

    // Charts related fields
    private static final int MAX_BAR_CHART_DIM = 4;

    private Map<Integer, Integer> monthlyActionsMap;
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

                isHabitArchived = habit.isArchived();

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
                markEndDate(habit.getFinalDate());

                extractDataFromTaskHistory(habit.getTaskHistory());

                // Set the highlighted days and icons on the calendarView
                calendarView.setEvents(events);
                calendarView.setHighlightedDays(highlight);

                // Draw charts
                drawStreakChart();
                drawMonthlyChart();

                drawTree(habit.getTree());

            });

        } else {
            Log.e(this.getClass().getSimpleName(), "Habit id < 0");
            finish();
        }
    }


    private void markCreationDate(long habitCreationDate) {
        if (habitCreationDate > 0) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTimeInMillis(habitCreationDate);
            calendarView.setMinimumDate(startDate);

            Drawable startIcon = getIcon(R.drawable.ic_add_24, R.color.primaryColor);
            events.add(new EventDay(startDate, startIcon));
        }
    }

    private void markEndDate(long finalDate) {
        if (finalDate > 0) {
            Calendar endDate = Calendar.getInstance();
            endDate.setTimeInMillis(finalDate);
            calendarView.setMaximumDate(endDate);

            Drawable endIcon = getIcon(R.drawable.ic_archive_24, R.color.primaryColor);
            events.add(new EventDay(endDate, endIcon));
        }
    }

    /**
     * Draw a chart displaying the actions the user did every month for this habit.
     */
    private void drawMonthlyChart() {

        LineChart chart = findViewById(R.id.monthlyActionChart);

        setNoDataAvailableMessage(chart);

        if (monthlyActionsMap == null || monthlyActionsMap.size() < 12) {
            //Log.e(this.getClass().getSimpleName(), "Invalid Action map");
            return;
        }

        // X-axis labels
        final String[] months = getResources().getStringArray(R.array.months);

        List<Entry> entries = new ArrayList<>();

        // Data
        float index = 0;
        for (Integer key : monthlyActionsMap.keySet()) {
            Integer actions = monthlyActionsMap.getOrDefault(key, 0);

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

    /**
     * Draw a bar chart displaying the 4 best streak of the user
     */
    private void drawStreakChart() {

        BarChart chart = findViewById(R.id.streakChart);

        setNoDataAvailableMessage(chart);

        if (streakList == null || streakList.size() <= 0) {
            return;
        }

        // Order the streak list
        streakList.sort(Streak::compareTo);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;

        for (Streak streak : streakList) {

            entries.add(new BarEntry(index++, streak.getNumDay()));
            labels.add(streak.getLabel());

            /* Allow only 4 elements inside the chart. Otherwise the bar's labels will end up
             * overlapping each others. */
            if (index >= MAX_BAR_CHART_DIM)
                break;
        }

        while (index < MAX_BAR_CHART_DIM) {
            // Fill the last positions with empty bars
            entries.add(new BarEntry(index++, 0));
            labels.add("");
        }

        BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.days));

        barDataSet.setColors(getColor(R.color.primaryLightColor),
                getColor(R.color.primaryColor),
                getColor(R.color.secondaryLightColor),
                getColor(R.color.secondaryColor));

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
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(MAX_BAR_CHART_DIM);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
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

        chart.setPinchZoom(false);
        chart.getLegend().setEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getDescription().setEnabled(false);
    }


    private void drawTree(Tree tree) {

        if (tree == null) {
            return;
        }

        ImageView treeImageView = findViewById(R.id.treeImageView);
        ImageView skyImageView = findViewById(R.id.skyImageView);

        int treeImageResId = TreeRealmManager.getTreeImageResourceId(tree);
        int skyImageResId = TreeRealmManager.getSkyResourceId(tree.getHealth());

        treeImageView.setImageDrawable(ContextCompat.getDrawable(this, treeImageResId));
        skyImageView.setImageDrawable(ContextCompat.getDrawable(this, skyImageResId));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) treeImageView.getLayoutParams();

        // Move the tree's image up or down by a fixed offset
        float offset = 0;

        switch (tree.getGrowth()) {
            case SPARKLING:
                offset = 50;
                break;
            case MEDIUM:
                offset = -70;
                break;
            case SMALL:
                offset = -180;
                break;
            case SPROUT:
                offset = -190;
                break;
        }

        // Set the margin bottom in "dp"
        float dpRatio = getResources().getDisplayMetrics().density;

        params.bottomMargin = (int) (offset * dpRatio);

        treeImageView.setLayoutParams(params);

    }

    private int getHabitIdFromBundles(Bundle savedInstance) {

        if (savedInstance != null)
            return savedInstance.getInt(EXTRA_HABIT_ID, -1);

        Bundle extra = getIntent().getExtras();
        if (extra != null)
            return extra.getInt(EXTRA_HABIT_ID, -1);

        return -1;
    }

    /**
     *
     */
    private void extractDataFromTaskHistory(@NonNull List<Task> taskHistory) {

        if (streakList == null) {
            //Log.e(this.getClass().getSimpleName(), "Streak list == null");
            return;
        }

        if (taskHistory.size() < 1) {
            //Log.e(LOGCAT_TAG, "taskHistory == null");
            return;
        }
        //taskHistory.sort((task, t1) -> Long.compare(task.getTaskDate(), t1.getTaskDate()));
        // Per le icone da mostrare nel calendario (rinvii e fallimenti)


        monthlyActionsMap = getInstanceOfActionMap();

        int lastMonth = Calendar.JANUARY;
        int currentMonth = Calendar.JANUARY;

        long minDayStreak = 0;
        int monthlyActions = 0;
        int streakDaysCounter = 0;

        Calendar calendar = null;

        for (Task task : taskHistory) {

            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(task.getTaskDate());

            //Log.i(LOGCAT_TAG, task.toString());

            // Get the month
            currentMonth = calendar.get(Calendar.MONTH);
            if (currentMonth != lastMonth) {
                // Updating monthly actions
                monthlyActionsMap.put(lastMonth, monthlyActions);

                lastMonth = currentMonth;
                monthlyActions = 0;
            }

            if (minDayStreak <= 0) {
                // Streak date starting point
                minDayStreak = calendar.getTimeInMillis();
            }


            switch (task.getTaskStatus()) {
                case SNOOZED:
                    // Snoozed task are highlighted and have a small icons underneath the date
                    Drawable snoozeIcon = getIcon(R.drawable.ic_sprout_small, R.color.secondaryColor);
                    events.add(new EventDay(calendar, snoozeIcon));
                case PASSED:
                    // Passed task are just highlighted
                    highlight.add(calendar);
                    monthlyActions++;
                    streakDaysCounter++;
                    break;

                default:
                    // Failed task are not highlighted. Instead, they have a red icon
                    Drawable failedIcon = getIcon(R.drawable.ic_clear_24dp, R.color.redColor);
                    events.add(new EventDay(calendar, failedIcon));

                    if (streakDaysCounter > 0) {
                        streakList.add(new Streak(streakDaysCounter, minDayStreak, calendar.getTimeInMillis()));
                        streakDaysCounter = 0;
                    }

                    minDayStreak = 0;
                    calendar = null;

                    break;
            }
        }

        // Update the map and the streak list with the remaining data (if available)
        monthlyActionsMap.put(currentMonth, monthlyActions);
        if (calendar != null)
            streakList.add(new Streak(streakDaysCounter, minDayStreak, calendar.getTimeInMillis()));

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

        if (isHabitArchived) {
            // Disable the menu item if the habit was archived
            menu.findItem(R.id.editHabitMenuItem).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.editHabitMenuItem:
                Intent intent = new Intent(this, EditHabitActivity.class);
                intent.putExtra(EditHabitActivity.EXTRA_HABIT_ID, habitId);

                Bundle bundle = ActivityOptions.makeCustomAnimation(this,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

                startActivity(intent, bundle);

                return true;
            case R.id.deleteStatsMenuItem:

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Sprout");
                builder.setMessage(R.string.delete_stats_dialog_message);
                builder.setPositiveButton(R.string.positive_dialog_button, (dialogInterface, i) -> {
                    if (habitId >= 0) {
                        TaskRealmManager.deleteHabitTaskHistory(habitId);

                        Toast.makeText(this, R.string.delete_stats_result_message, Toast.LENGTH_SHORT).show();

                        // Refresh the activity
                        finish();
                        startActivity(getIntent());
                    }
                });
                builder.setNeutralButton(R.string.neutral_dialog_button, (dialogInterface, i) -> dialogInterface.dismiss());

                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}