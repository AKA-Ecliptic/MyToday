package com.example.mytoday.Architecture;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mytoday.Entity.Task;
import com.example.mytoday.Entity.Today;
import com.example.mytoday.Entity.TodayTasks;

import java.util.Date;
import java.util.List;

public class TodayViewModel extends AndroidViewModel {

    private TodayRepository todayRepository;
    private LiveData<List<TodayTasks>> todayTasks;

    public TodayViewModel(@NonNull Application application) {
        super(application);
        todayRepository = new TodayRepository(application);
        todayTasks = todayRepository.getAllTodaysWithTasks();
    }

    public LiveData<TodayTasks> getToday(Date date){ return todayRepository.getToday(date); }

    public LiveData<TodayTasks> getToday(String date){ return todayRepository.getToday(date); }

    public LiveData<List<TodayTasks>> getAllTodaysWithTasks() { return  todayTasks; }

    public LiveData<List<TodayTasks>> refreshTodaysWithTasks() {
        todayTasks = todayRepository.refreshTodaysWithTasks();
        return todayTasks;
    }

    public void insert(Today today) { todayRepository.insertToday(today); }

    public void updateTasks(Task... tasks) {
        todayRepository.updateTasks(tasks);
    }

    public void updateToday(Today today) { todayRepository.updateToday(today); }

    public void insert(Task task) { todayRepository.insert(task); }

    public void deleteTasks(Task... tasks) {
        todayRepository.deleteTasks(tasks);
    }

    public void deleteToday(TodayTasks today) {
        todayRepository.deleteToday(today);
    }
}
