package com.example.mytoday.Architecture;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.mytoday.Entity.Task;
import com.example.mytoday.Entity.Today;
import com.example.mytoday.Entity.TodayTasks;

import java.util.List;

@Dao
public interface TodayDAO {

    @Insert
    void insertTodays(Today... today);

    @Insert
    void insertTasks(Task... tasks);

    @Insert
    void insertTodayWithTasks(Today today, List<Task> tasks);

    @Query("SELECT * FROM todays")
    LiveData<List<Today>> getAllTodays();

    @Transaction
    @Query("SELECT * FROM todays ORDER BY _date")
    LiveData<List<TodayTasks>> getAllTodaysWithTasks();

    @Update
    void updateToday(Today today);

    @Update
    void updateTasks(Task... tasks);

    @Delete
    void deleteToday(Today today, List<Task> tasks);

    @Delete
    void deleteTasks(Task... tasks);

    @Query("DELETE FROM todays")
    void deleteAllTodays();

    @Query("DELETE FROM tasks")
    void deleteAllTasks();
}
