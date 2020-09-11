package com.hcifedii.sprout.enumerations;

import com.hcifedii.sprout.R;

public enum GoalType {
    NONE(R.string.none),
    ACTION(R.string.action),
    STREAK(R.string.streak),
    DEADLINE(R.string.deadline);

    private final int resId;

    GoalType(int resId){
        this.resId = resId;
    }

    public int getStringResourceId(){
        return resId;
    }
}
