package com.example.mytoday.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import static com.example.mytoday.Architecture.TodayConverters.fromString;

@Entity(tableName = "tasks")
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true) private int _id;
    @ColumnInfo(name = "today_date") private Date created;

    private String title;
    private String description;
    private int status;

    public Task(Date created, String title, String description, int status) {
        this.created = created;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String created, String title, String description, int status) {
        this.created = fromString(created);
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCreated(String created) {
        this.created = fromString(created);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
