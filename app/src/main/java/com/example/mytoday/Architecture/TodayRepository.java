package com.example.mytoday.Architecture;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mytoday.Entity.Task;
import com.example.mytoday.Entity.Today;
import com.example.mytoday.Entity.TodayTasks;

import java.util.List;

class TodayRepository {

    private TodayDAO todayDAO;
    private LiveData<List<TodayTasks>> todayTasks;

    TodayRepository(Application application) {
        TodayRoomDatabase db = TodayRoomDatabase.getDatabase(application);
        todayDAO = db.todayDAO();
        todayTasks = todayDAO.getAllTodaysWithTasks();
    }

    LiveData<List<TodayTasks>> getAllTodaysWithTasks() {
        return todayTasks;
    }

    LiveData<List<TodayTasks>> refreshTodaysWithTasks() {
        todayTasks = todayDAO.getAllTodaysWithTasks();
        return todayTasks;
    }

    void insertToday(Today today) {
        TodayRoomDatabase.databaseWriteExecutor.execute(() -> {
            todayDAO.insertTodays(today);
        });
    }

    void updateTasks(Task... tasks) {
        TodayRoomDatabase.databaseWriteExecutor.execute(() -> {
            todayDAO.updateTasks(tasks);
            refreshTodaysWithTasks();
        });
    }

    void updateToday(Today today) {
        TodayRoomDatabase.databaseWriteExecutor.execute(() -> {
            todayDAO.updateToday(today);
            refreshTodaysWithTasks();
        });
    }

    void insert(Task task) {
        TodayRoomDatabase.databaseWriteExecutor.execute(() -> {
            todayDAO.insertTasks(task);
            refreshTodaysWithTasks();
        });
    }

    void deleteTasks(Task... tasks) {
        TodayRoomDatabase.databaseWriteExecutor.execute(() -> {
            todayDAO.deleteTasks(tasks);
            refreshTodaysWithTasks();
        });
    }
}
