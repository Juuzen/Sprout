package com.hcifedii.sprout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textview.MaterialTextView;
import com.hcifedii.sprout.enumerations.GoalType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.Habit;
import utils.HabitRealmManager;

public class HabitStatsActivity extends AppCompatActivity {

    public static final String EXTRA_HABIT_ID = "habitID";

    private static final DecimalFormat format = new DecimalFormat("###");

    private Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_stats);

        enableTopBackButton();

        int habitId = getHabitIdFromBundles(savedInstanceState);

        if (habitId >= 0) {
            // Using a different thread for ui operations
            this.runOnUiThread(() -> {

                // Get habit from database
                habit = HabitRealmManager.getHabit(habitId);

                if (habit == null)
                    return;         // TODO: forse ci andrebbe meglio un'eccezione

                // Set Activity's title
                setTitle(habit.getTitle());

                MaterialTextView habitTypeTextView = findViewById(R.id.habitTypeTextView);

                habitTypeTextView.setText(habit.getHabitType().getStringResourceId());

                // Get the Calendar
                CalendarView calendarView = findViewById(R.id.calendarView);

                // List of icons that will be showed inside the calendar
                List<EventDay> events = new ArrayList<>();

                // List of the days that should be highlighted
                List<Calendar> highlight = new ArrayList<>();

                // Mark creation date inside the calendar
                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(habit.getHabitCreationDate());
                calendarView.setMinimumDate(startDate);

                Drawable startIcon = getIcon(R.drawable.ic_add_24, R.color.primaryColor);
                events.add(new EventDay(startDate, startIcon));

                // Mark the end date if available
                if (habit.getGoalType() == GoalType.DEADLINE) {
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTimeInMillis(habit.getFinalDate());
                    calendarView.setMaximumDate(endDate);

                    Drawable endIcon = getIcon(R.drawable.ic_archive_24, R.color.primaryColor);
                    events.add(new EventDay(endDate, endIcon));
                }

                // Get the task
                ///
//                Calendar cal = Calendar.getInstance();
//                cal.set(2020, 9,25);
//                highlight.add(cal);


                calendarView.setEvents(events);
                calendarView.setHighlightedDays(highlight);


                // Draw charts
                drawStreakChart(habit);
                drawMonthlyChart(habit);


            });


        }


    }

    // TODO: mostra il numero di azioni negli ultimi 12 mesi. Quindi accetta un arraylist di 12 elementi.
    private void drawMonthlyChart(@NonNull Habit habit) {

        LineChart chart = findViewById(R.id.monthlyActionChart);

        setNoDataAvailableMessage(chart);

        // X-axis labels
        final String[] months = getResources().getStringArray(R.array.months);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 1));
        entries.add(new Entry(1f, 5));
        entries.add(new Entry(2f, 4));
        entries.add(new Entry(3f, 8));
        entries.add(new Entry(4f, 10));
        entries.add(new Entry(5f, 15));
        entries.add(new Entry(6f, 15));
        entries.add(new Entry(7f, 12));
        entries.add(new Entry(8f, 11));
        entries.add(new Entry(9f, 0));
        entries.add(new Entry(10f, 6));
        entries.add(new Entry(11f, 9));

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
    // TODO: max 4
    private void drawStreakChart(@NonNull Habit habit) {

        BarChart chart = findViewById(R.id.streakChart);

        setNoDataAvailableMessage(chart);

        ArrayList<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(0f, 10f));
        entries.add(new BarEntry(1f, 22f));
        entries.add(new BarEntry(2f, 5f));
        entries.add(new BarEntry(3f, 11f));

        final String[] labels = {"22 ago - 25 ago", "27 ago - 31 ago", "1 set - 3 set", "4 set - 5 set"};

        BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.days));

        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(getColor(R.color.primaryTextColor));
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return format.format(barEntry.getY());
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
                return labels[(int) value];
            }
        });

        chart.setData(barData);
    }

    private void setNoDataAvailableMessage(@NonNull BarLineChartBase<?> chart) {
        chart.setNoDataTextColor(getColor(R.color.primaryTextColor));
        chart.setNoDataText(getString(R.string.no_data_available));
    }

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


    // TODO: elementi da mostrare nel calendario
    private List<Calendar> getPassedDays(@NonNull Habit habit) {

        return null;
    }

    private List<Calendar> getSnoozedDays(@NonNull Habit habit) {

        return null;
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

    private void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }

}