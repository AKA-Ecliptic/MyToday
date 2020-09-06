package com.example.mytoday.Architecture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mytoday.Entity.Task;
import com.example.mytoday.Entity.Today;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Today.class, Task.class}, version = 1, exportSchema = false)
@TypeConverters({TodayConverters.class})
abstract class TodayRoomDatabase extends RoomDatabase {

    abstract TodayDAO todayDAO();

    private static volatile TodayRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TodayRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (TodayRoomDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TodayRoomDatabase.class, "today_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            databaseWriteExecutor.execute(() -> {
                //NOTE: DEBUGGING PURPOSES ONLY.
                /*TodayDAO dao = INSTANCE.todayDAO();
                dao.deleteAllTodays();
                dao.deleteAllTasks();*/
            });
        }
    };
}
