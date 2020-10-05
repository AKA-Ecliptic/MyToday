package com.example.mytoday.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytoday.CustomView.DayPast;
import com.example.mytoday.Entity.TodayTasks;
import com.example.mytoday.R;
import com.example.mytoday.Enum.TaskStatus;

import java.util.Date;
import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.TodayViewHolder> {

    private static RecyclerViewItemClickListener onItemClick;
    private static RecyclerViewItemClickListener onItemLongClick;
    private boolean hasDivider = false;

    static class TodayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final DayPast dayPast;

        private TodayViewHolder(View itemView) {
            super(itemView);
            dayPast = itemView.findViewById(R.id.day_past);
            dayPast.setOnClickListener(this);
            dayPast.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClick.onRecyclerViewItemClicked(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            onItemLongClick.onRecyclerViewItemClicked(view, getAdapterPosition());
            return true;
        }
    }

    private final LayoutInflater inflater;
    private List<TodayTasks> todayTasks;

    public HomeListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TodayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.recyclerview_home_item, parent, false);

        if(viewType == 1 && !hasDivider) {
            hasDivider = true;
            ViewGroup divider = (ViewGroup) inflater.inflate(R.layout.recycler_home_divider, itemView, false);
            itemView.addView(divider, 0);
        }

        return new TodayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayViewHolder holder, int position) {
        TodayTasks current = todayTasks.get(position);
        holder.dayPast.setFormattedDate(current.getToday().getDate());
        holder.dayPast.setTotalTasks(current.getTasks().size());
        holder.dayPast.setCompleteTasks(( int )
                current.getTasks()
                        .stream()
                        .filter(t -> t.getStatus() == TaskStatus.COMPLETE.getValue())
                        .count());
    }

    public void setTodayTasks(List<TodayTasks> todayTasks){
        this.todayTasks = todayTasks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (todayTasks != null)
            return todayTasks.size();
        else return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return todayTasks.get(position).getToday().getDate().after(new Date()) ? 1 : 0;
    }

    public TodayTasks getItem(int position) {
        return todayTasks.get(position);
    }

    public void setOnItemLongClick(RecyclerViewItemClickListener recyclerViewItemLongClickListener) {
        onItemLongClick = recyclerViewItemLongClickListener;
    }

    public void setOnItemClick(RecyclerViewItemClickListener recyclerViewItemClickListener) {
        onItemClick = recyclerViewItemClickListener;
    }

    public interface RecyclerViewItemClickListener {
        void onRecyclerViewItemClicked(View v, int pos);
    }
}
