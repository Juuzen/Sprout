package com.hcifedii.sprout.enumerations;

import com.hcifedii.sprout.R;

public enum HabitType {

    CLASSIC(R.string.classic),
    COUNTER(R.string.counter);

    private int resId;

    HabitType(int resId) {
        this.resId = resId;
    }

    public int getStringResourceId() {
        return resId;
    }
}
