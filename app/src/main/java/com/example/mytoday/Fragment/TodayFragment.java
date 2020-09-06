package com.example.mytoday.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytoday.Architecture.TodayViewModel;
import com.example.mytoday.Entity.Task;
import com.example.mytoday.Entity.TodayTasks;
import com.example.mytoday.R;
import com.example.mytoday.Callback.TaskSwipeToDeleteCallback;
import com.example.mytoday.Adapter.TodayListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static com.example.mytoday.Fragment.HomeFragment.PASSED_TODAY;
import static com.example.mytoday.Fragment.TaskPopup.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {

    private TodayListAdapter adapter;
    private TextView todayGoal;
    private FloatingActionButton addFAB;
    private TodayViewModel todayViewModel;

    private HandleUndoDeleteTask handleUndoDeleteTask;
    private TaskPopup taskPopup;
    private TodayTasks currentTodayTask;

    public TodayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);
        setUpViewModelLink();
        setUpConditionals();
        setUpViewData();
    }

    private void setUpViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.today_recyclerview);
        adapter = new TodayListAdapter(view.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new TaskSwipeToDeleteCallback(requireContext()));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        todayGoal = view.findViewById(R.id.today_goal_text);
        addFAB = view.findViewById(R.id.today_add_fab);
    }

    private void setUpViewModelLink() {
        todayViewModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
    }

    private void setUpConditionals() {
        currentTodayTask = (getArguments() != null) ? ( TodayTasks ) getArguments().get(PASSED_TODAY) : null;
        taskPopup = new TaskPopup(null);

        if (currentTodayTask != null){
            setUpAdapter();
            setUpTodayGoal();

            setUpPopup();
            setUpRecyclerSwipe();
        }else {
            Navigation.findNavController(requireView()).popBackStack();
        }
    }

    private void setUpViewData() {
        addFAB.setOnClickListener( v -> {
            taskPopup.setTask(null);
            taskPopup.show(getParentFragmentManager(), TAG);
        });
    }

    private void setUpAdapter() {
        adapter.setTasks(currentTodayTask.getTasks());
        adapter.setUpdateStatusListener(task -> todayViewModel.updateTasks(task));
        adapter.setLongClickListener(task -> {
            taskPopup.setTask(task);
            taskPopup.show(getParentFragmentManager(), TAG);
        });
    }

    private void setUpTodayGoal() {
        todayGoal.setText(currentTodayTask.getToday().getGoal());
        todayGoal.setOnLongClickListener( v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            View view = requireActivity().getLayoutInflater().inflate(R.layout.popup_goal, null);
            Dialog goalDialog = builder.setView(view).create();

            EditText goal = view.findViewById(R.id.goal_popup_text);
            goal.setText(currentTodayTask.getToday().getGoal());

            Button confirm = view.findViewById(R.id.goal_popup_confirm);
            confirm.setOnClickListener(vw -> {
                currentTodayTask.getToday().setGoal(goal.getText().toString());
                todayViewModel.updateToday(currentTodayTask.getToday());
                todayGoal.setText(goal.getText().toString());

                goalDialog.dismiss();
            });

            goalDialog.show();
            return true;
        });
    }

    private void setUpPopup() {
        taskPopup.setTaskHandler(task -> {
            if(currentTodayTask.getTasks().stream().anyMatch(t -> t.equals(task))){
                adapter.updateTaskAtPosition(task);
                todayViewModel.updateTasks(task);
            }else {
                task.setCreated(currentTodayTask.getToday().getDate());
                adapter.addTask(task);
                todayViewModel.insert(task);
            }
        });
    }

    private void setUpRecyclerSwipe() {
        TaskSwipeToDeleteCallback.setSwipeDelete(pos -> {
            Task toDelete = adapter.getTask(pos);

            adapter.deleteTask(pos);
            todayViewModel.deleteTasks(toDelete);
            showUndoSnackbar(toDelete);

            handleUndoDeleteTask = task -> {
                adapter.addTask(task, pos);
                todayViewModel.insert(task);
            };
        });
    }

    private void showUndoSnackbar(Task task) {
        View view = requireActivity().findViewById(R.id.main_coordinator);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_undo_message,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> handleUndoDeleteTask.undoHandler(task));
        snackbar.show();
    }

    interface HandleUndoDeleteTask {
        void undoHandler(Task task);
    }
}
