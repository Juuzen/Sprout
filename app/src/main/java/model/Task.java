package model;

import java.util.Date;

import io.realm.RealmObject;
import utils.TaskStatus;

public class Task extends RealmObject {
    private Date checkedDate;
    private TaskStatus taskStatus;

}
