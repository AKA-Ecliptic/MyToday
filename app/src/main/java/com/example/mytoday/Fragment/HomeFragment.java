package com.example.mytoday.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytoday.Adapter.HomeListAdapter;
import com.example.mytoday.Architecture.TodayViewModel;
import com.example.mytoday.Entity.Today;
import com.example.mytoday.Entity.TodayTasks;
import com.example.mytoday.Enum.TaskStatus;
import com.example.mytoday.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.mytoday.Architecture.TodayConverters.dateToString;
import static com.example.mytoday.CustomView.DayPast.dayFormatter;
import static com.example.mytoday.CustomView.DayPast.formatTasks;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String PASSED_TODAY = "TODAY_DATA_OUT";

    private HomeListAdapter adapter;
    private TodayViewModel todayViewModel;

    private TextView todayGoal;
    private TextView todayDate;
    private TextView todayDay;
    private TextView todayTasks;
    private ImageView todayEdit;

    private TodayTasks currentTodayTask = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setUpViewModelLink();
    }

    private void initViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.home_recyclerview);
        adapter = new HomeListAdapter(view.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        todayGoal = view.findViewById(R.id.home_current_goal);
        todayDate = view.findViewById(R.id.home_current_date);
        todayDay = view.findViewById(R.id.home_current_day);
        todayTasks = view.findViewById(R.id.home_current_task);
        todayEdit = view.findViewById(R.id.home_current_edit);
    }

    private void setUpViewModelLink() {
        todayViewModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        todayViewModel.getToday(dateToString(new Date())).observe(getViewLifecycleOwner(), today -> {
            currentTodayTask = today;
            setUpConditionals();
            setUpAdapter();
        });
    }

    private void setUpConditionals() {
        if(currentTodayTask != null){
            populateViews(currentTodayTask);
        }else {
            todayEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_add));
            todayEdit.setOnClickListener( v -> {
                currentTodayTask = new TodayTasks();
                currentTodayTask.setToday(new Today(new Date(), requireContext().getString(R.string.no_goal_set)));
                currentTodayTask.setTasks(new ArrayList<>());

                todayViewModel.insert(currentTodayTask.getToday());
                showNewTodaySnackbar();
                openTaskFragment(currentTodayTask);
            });
        }
    }

    private void setUpAdapter() {
        todayViewModel.refreshTodaysWithTasks().observe(getViewLifecycleOwner(), todays -> {
            currentTodayTask = todays.stream()
                    .filter(tT -> dateToString(tT.getToday().getDate()).equals(dateToString(new Date())))
                    .findFirst()
                    .orElse(null);

            if (currentTodayTask != null)
                todays.remove(currentTodayTask);

            adapter.setTodayTasks(todays);
            adapter.setOnItemClick((view, pos) -> openTaskFragment(adapter.getItem(pos)));
            adapter.setOnItemLongClick((view, pos) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

                View v = requireActivity().getLayoutInflater().inflate(R.layout.popup_delete, null);
                AlertDialog deleteDialog = builder.setView(v).create();

                deleteDialog.show();

                Button confirm = v.findViewById(R.id.delete_popup_confirm);
                Button cancel = v.findViewById(R.id.delete_popup_cancel);

                confirm.setOnClickListener(vw -> {
                    todayViewModel.deleteToday(adapter.getItem(pos));

                    deleteDialog.dismiss();
                });

                cancel.setOnClickListener( vw -> {
                    deleteDialog.cancel();
                });
            });
        });
    }

    private void populateViews(TodayTasks current) {
        todayGoal.setText(current.getToday().getGoal());
        todayDate.setText(dateToString(new Date()));
        todayDay.setText(dayFormatter.format(new Date()));

        int total = current.getTasks().size();
        int complete = ( int ) current.getTasks()
                .stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETE.getValue())
                .count();

        todayTasks.setText(String.format(Locale.ENGLISH,"%s / %s Tasks", formatTasks(complete), formatTasks(total)));

        todayEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit));
        todayEdit.setOnClickListener( l -> {
            openTaskFragment(current);
        });
    }

    private void openTaskFragment(TodayTasks todayTask) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PASSED_TODAY, todayTask);
        Navigation.findNavController(requireView()).navigate(R.id.action_home_fragment_to_today_fragment, bundle);
    }

    private void showNewTodaySnackbar(){
        Snackbar snackbar = Snackbar.make(
                requireActivity().findViewById(R.id.main_coordinator),
                R.string.snack_bar_new_today,
                Snackbar.LENGTH_LONG);
        snackbar.getView().findViewById(R.id.snackbar_text).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }


}
