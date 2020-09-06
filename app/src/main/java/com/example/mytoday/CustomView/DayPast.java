package com.example.mytoday.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mytoday.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.mytoday.Architecture.TodayConverters.dateToString;
import static com.example.mytoday.Architecture.TodayConverters.fromString;
import static com.example.mytoday.CustomView.TodoAccordion.spToPx;

public class DayPast extends View {

    private final DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

    private String dayDate;
    private int totalTasks;
    private int completeTasks;

    private String formattedDate;
    private String formattedTotalTasks;
    private String formattedCompleteTasks;

    public static final SimpleDateFormat dayFormatter = new SimpleDateFormat("E dd", Locale.UK);
    private static final String TASKS_STRING = "Tasks";

    private Paint linePaint;
    private TextPaint tasksPaint;
    private TextPaint headingPaint;
    private TextPaint subHeadingPaint;
    private Rect tasksTallyBounds;
    private Rect dateBounds;

    public DayPast(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DayPast,
                0, 0);

        try{
            formattedDate = (a.getString(R.styleable.DayPast_formattedDate) != null) ? a.getString(R.styleable.DayPast_formattedDate) : "2020-02-02";
            totalTasks = a.getInteger(R.styleable.DayPast_totalTasks, 0);
            completeTasks = a.getInteger(R.styleable.DayPast_completeTasks, 0);
        }finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(getResources().getColor(R.color.colorTextPrimary, getContext().getTheme()));
        linePaint.setStrokeWidth(spToPx(2, displayMetrics));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        tasksPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tasksPaint.setColor(getResources().getColor(R.color.colorTextPrimary, getContext().getTheme()));
        tasksPaint.setTextSize(spToPx(50, displayMetrics));

        headingPaint = new TextPaint(tasksPaint);
        headingPaint.setTextSize(spToPx(20, displayMetrics));

        subHeadingPaint = new TextPaint(headingPaint);
        subHeadingPaint.setColor(getResources().getColor(R.color.colorTextSecondary, getContext().getTheme()));
        subHeadingPaint.setTextSize(spToPx(15, displayMetrics));

        tasksTallyBounds = new Rect();
        dateBounds = new Rect();

        dayDate = dayFormatter.format(fromString(formattedDate));
        formattedTotalTasks = formatTasks(totalTasks);
        formattedCompleteTasks = formatTasks(completeTasks);

        tasksPaint.getTextBounds(formattedCompleteTasks, 0, formattedCompleteTasks.length(), tasksTallyBounds);
        headingPaint.getTextBounds(dayDate, 0, dayDate.length(), dateBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minW, widthMeasureSpec, 1);

        int  minH = spToPx(100, displayMetrics);
        int height = resolveSizeAndState(minH, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int padding = 35;
        int paddingMinor = padding >> 1;
        int xDisplacement = 200;
        int lineStart = ( int ) ((getWidth() - xDisplacement) * .65f);

        canvas.drawLine(lineStart, getHeight() - padding, lineStart + xDisplacement, padding, linePaint);
        canvas.drawText(formattedTotalTasks, lineStart + xDisplacement, getHeight() - padding, tasksPaint);
        canvas.drawText(formattedCompleteTasks, lineStart - tasksPaint.measureText(formattedCompleteTasks), padding + tasksTallyBounds.height(), tasksPaint);

        canvas.drawText(TASKS_STRING, lineStart + xDisplacement + paddingMinor + tasksPaint.measureText(formattedTotalTasks), getHeight() - padding, subHeadingPaint);

        canvas.drawText(dayDate, padding, padding + dateBounds.height(), headingPaint);
        canvas.drawText(formattedDate, padding + paddingMinor, padding + (dateBounds.height() * 2) + paddingMinor , subHeadingPaint);

        canvas.save();
        canvas.restore();
    }

    public static String formatTasks(int num) {
        if(num >= 10)
            return Integer.toString(num);
        return "0" + num;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
        dayDate = dayFormatter.format(fromString(formattedDate));
        invalidate();
        requestLayout();
    }

    public void setFormattedDate(Date date) {
        this.formattedDate = dateToString(date);
        dayDate = dayFormatter.format(fromString(formattedDate));
        invalidate();
        requestLayout();
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
        formattedTotalTasks = formatTasks(totalTasks);
        invalidate();
        requestLayout();
    }

    public int getCompleteTasks() {
        return completeTasks;
    }

    public void setCompleteTasks(int completeTasks) {
        this.completeTasks = completeTasks;
        formattedCompleteTasks = formatTasks(completeTasks);
        invalidate();
        requestLayout();
    }
}
