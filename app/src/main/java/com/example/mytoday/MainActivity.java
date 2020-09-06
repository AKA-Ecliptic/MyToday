package com.example.mytoday;

import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mytoday.Architecture.TodayViewModel;
import com.example.mytoday.Entity.TodayTasks;

import java.util.Date;

import static com.example.mytoday.Architecture.TodayConverters.dateToString;
import static com.example.mytoday.CustomView.DayPast.dayFormatter;
import static com.example.mytoday.Fragment.HomeFragment.PASSED_TODAY;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private NavController navController;
    private TodayTasks currentTodayTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Adding my custom toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        toolbarTitle.setText(R.string.app_name);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Setting up sub toolbar
        TextView date = findViewById(R.id.main_date);
        TextView dateDay = findViewById(R.id.main_date_day);

        date.setText(dateToString(new Date()));
        dateDay.setText(dayFormatter.format(new Date()));

        //Setting up Navigation
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        TextView navHome = findViewById(R.id.drawer_home_fragment);
        TextView navToday = findViewById(R.id.drawer_today_fragment);

        navHome.setOnClickListener(onNav);
        navToday.setOnClickListener(onNav);

        //Setting up navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        //Set up functions
        setUpViewModelLink();
    }

    private void setUpViewModelLink() {
        TodayViewModel todayViewModel = new ViewModelProvider(this).get(TodayViewModel.class);
        todayViewModel.refreshTodaysWithTasks().observe(this, todayTasks -> {
                    currentTodayTask = todayTasks.stream()
                .filter(tT -> dateToString(tT.getToday().getDate()).equals(dateToString(new Date())))
                .findFirst()
                .orElse(null);
        });
    }

    private OnClickListener onNav = view -> {
        int currentDest = (navController.getCurrentDestination() != null) ? navController.getCurrentDestination().getId() : -1;
        switch (view.getId()) {
            case R.id.drawer_home_fragment:
                if (currentDest != R.id.home_fragment)
                    navController.popBackStack();
                drawerLayout.close();
                break;
            case R.id.drawer_today_fragment:
                if (currentDest != R.id.today_fragment) {
                    if(currentTodayTask != null){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(PASSED_TODAY, currentTodayTask);
                        navController.navigate(R.id.action_home_fragment_to_today_fragment, bundle);
                    }
                }
                drawerLayout.close();
                break;
        }
    };

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
}
