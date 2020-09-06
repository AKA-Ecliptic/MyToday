package com.example.mytoday.Callback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytoday.R;

import static com.example.mytoday.CustomView.TodoAccordion.spToPx;

public class TaskSwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private static TaskSwipeDelete swipeDelete;
    private DisplayMetrics displayMetrics;

    private Drawable icon;
    private ColorDrawable background;

    public TaskSwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT);
        displayMetrics = context.getResources().getDisplayMetrics();

        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        background = new ColorDrawable(Color.argb(.2f, .0f, .0f, .0f));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        swipeDelete.onSwipeDelete(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
        @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
        boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        int margin = spToPx(2, displayMetrics);
        int yOffset = spToPx(7, displayMetrics);
        int inset = margin >> 1;

        int iconTop = itemView.getTop() + margin;
        int iconBottom = itemView.getBottom() - margin;

        if (dX < 0) {
            int iconLeft = itemView.getRight() - itemView.getHeight() + margin - yOffset;
            int iconRight = itemView.getRight() - yOffset;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX),
                    itemView.getTop() + inset, itemView.getRight(), itemView.getBottom() - inset);
        } else {
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }

    public static void setSwipeDelete(TaskSwipeDelete taskSwipeDelete) {
        swipeDelete = taskSwipeDelete;
    }

    public interface TaskSwipeDelete {
        void onSwipeDelete(int pos);
    }
}
