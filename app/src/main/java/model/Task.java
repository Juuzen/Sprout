package model;

import java.util.Date;

import io.realm.RealmObject;
import utils.TaskStatusEnum;

public class Task extends RealmObject {
    private Date checkedDate;
    private TaskStatusEnum taskStatus;

}
