package com.example.mytoday.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytoday.CustomView.TodoAccordion;
import com.example.mytoday.Entity.Task;
import com.example.mytoday.R;

import java.util.List;

public class TodayListAdapter extends RecyclerView.Adapter<TodayListAdapter.TaskViewHolder> {

    private static UpdateStatusListener statusListener;
    private static TaskLongClickListener longClickListener;

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnLongClickListener {
        private final TodoAccordion todoAccordion;

        private TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            itemView.setOnLongClickListener(this);
            todoAccordion = itemView.findViewById(R.id.todo_accordion);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            view.performClick();
            if(event.getActionMasked() == MotionEvent.ACTION_UP){
                if(this.todoAccordion.pointInCheckBox(( int ) event.getX(), ( int ) event.getY())){
                    this.todoAccordion.cycleStatus();
                    getTask(getAdapterPosition()).setStatus(this.todoAccordion.getStatusValue());
                    statusListener.onStatusUpdate(getTask(getAdapterPosition()));
                    return false;
                }
                this.todoAccordion.setExpanded(!this.todoAccordion.isExpanded());
                return false;
            }
            return false;
        }

        @Override
        public boolean onLongClick(View view) {
            longClickListener.onLongClick(getTask(getAdapterPosition()));
            return false;
        }
    }

    private final LayoutInflater inflater;
    private List<Task> tasks;

    public TodayListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_today_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task current = tasks.get(position);
        holder.todoAccordion.setTitle(current.getTitle());
        holder.todoAccordion.setDescription(current.getDescription());
        holder.todoAccordion.setStatus(current.getStatus());
    }

    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        notifyItemInserted(this.tasks.indexOf(task));
    }

    public void addTask(Task task, int pos) {
        this.tasks.add(pos, task);
        notifyItemInserted(this.tasks.indexOf(task));
    }

    public void updateTaskAtPosition(Task task) {
        notifyItemChanged(this.tasks.indexOf(task));
    }

    public void deleteTask(int pos) {
        this.tasks.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public int getItemCount() {
        if (tasks != null)
            return tasks.size();
        else return 0;
    }

    public Task getTask(int position) {
        return tasks.get(position);
    }

    public void setUpdateStatusListener(UpdateStatusListener updateStatusListener) {
        statusListener = updateStatusListener;
    }

    public void setLongClickListener(TaskLongClickListener taskLongClickListener) {
        longClickListener = taskLongClickListener;
    }

    public interface UpdateStatusListener {
        void onStatusUpdate(Task task);
    }

    public interface TaskLongClickListener {
        void onLongClick(Task task);
    }
}
