package utils;

import io.realm.RealmObject;

enum statusEnum {
    UNCOMPLETED, PASSED, FAILED, SNOOZED
}

public class TaskStatus extends RealmObject {
    private String taskStatus;

    public void saveStatus(statusEnum val) {
        this.taskStatus = val.toString();
    }

    public statusEnum getEnum() {
        return statusEnum.valueOf(taskStatus);
    }
}
