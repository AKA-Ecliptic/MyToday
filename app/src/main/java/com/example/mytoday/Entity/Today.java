package com.example.mytoday.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.example.mytoday.Architecture.TodayConverters.fromString;

@Entity(tableName = "todays")
public class Today implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "_date")
    @NonNull
    private Date date;

    private String goal;
    @Ignore private List<Task> tasks;

    public Today(@NonNull Date date, String goal) {
        this.date = date;
        this.goal = goal;
    }

    public Today(String date, String goal) {
        this.date = fromString(date);
        this.goal = goal;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public void setDate(@NonNull String date) {
        this.date = fromString(date);
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
