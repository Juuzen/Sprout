package model;

import io.realm.RealmObject;

public class Task extends RealmObject {
    public enum Status {
        PASSED,
        FAILED,
        SNOOZED
    }

    private long taskDate;
    private String taskStatus;
    private int id;

    public long getTaskDate() {
        return taskDate;
    }
    public void setTaskDate(long taskDate) {
        this.taskDate = taskDate;
    }

    public Task.Status getTaskStatus() {
        return Status.valueOf(this.taskStatus);
    }
    public void setTaskStatus(Task.Status taskStatus) { this.taskStatus = taskStatus.name(); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
