package com.example.mytoday.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mytoday.Entity.Task;
import com.example.mytoday.R;
import com.example.mytoday.Enum.TaskStatus;

import java.util.Date;

public class TaskPopup extends DialogFragment {

    static final String TAG = "TASK_POPUP";
    private static TaskHandler taskHandler;
    private Task task;

    TaskPopup(@Nullable Task task) {
        this.task = task;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_task, null);

        Dialog dialog = builder.setView(view).create();

        EditText title = view.findViewById(R.id.task_popup_title);
        Spinner status = view.findViewById(R.id.task_popup_spinner);
        EditText description = view.findViewById(R.id.task_popup_description);

        status.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.spinner_list_item, TaskStatus.values()));

        if(task != null){
            title.setText(task.getTitle());
            status.setSelection(task.getStatus());
            description.setText(task.getDescription());
        }

        Button confirm = view.findViewById(R.id.task_popup_confirm);
        confirm.setOnClickListener(v -> {
            if(task == null){
                taskHandler.onTaskPassed(
                    new Task(
                            new Date(),
                            title.getText().toString(),
                            description.getText().toString(),
                            ((TaskStatus)status.getSelectedItem()).getValue()
                    )
                );
            }else {
                task.setTitle(title.getText().toString());
                task.setDescription(description.getText().toString());
                task.setStatus(((TaskStatus)status.getSelectedItem()).getValue());

                taskHandler.onTaskPassed(task);
            }
            dialog.dismiss();
        });

        return dialog;
    }

    void setTask(Task task) {
        this.task = task;
    }

    void setTaskHandler(TaskHandler handler) {
        taskHandler = handler;
    }

    interface TaskHandler{
        void onTaskPassed(Task task);
    }
}
