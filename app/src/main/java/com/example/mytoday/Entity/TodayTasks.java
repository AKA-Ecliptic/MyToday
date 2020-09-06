package com.example.mytoday.Entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

public class TodayTasks implements Serializable {

    @Embedded private Today today;
    @Relation(
            parentColumn = "_date",
            entityColumn = "today_date"
    )
    private List<Task> tasks;

    public Today getToday() {
        return today;
    }

    public void setToday(Today today) {
        this.today = today;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
