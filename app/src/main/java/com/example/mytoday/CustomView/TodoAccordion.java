package com.example.mytoday.CustomView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.example.mytoday.R;
import com.example.mytoday.Enum.TaskStatus;

public class TodoAccordion extends View {

    private static final int TITLE_FONT_SIZE = 24;
    private static final int DESCRIPTION_FONT_SIZE = 14;

    private final DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
    private final int padding = 25;

    private int initHeight;
    private int expandHeight;

    private TaskStatus status;
    private String title;
    private boolean expanded;
    private String description;

    private float titleHeight;

    private TextPaint titlePaint;
    private TextPaint descriptionPaint;

    private Paint checkBoxPaint;
    private Paint checkBoxFillPaint;

    private StaticLayout staticLayout;

    private int arrowDip = 35;
    private final int arrowArmLength = 35;
    private Rect checkBox;
    private float[] arrow;

    public TodoAccordion(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TodoAccordion,
                0, 0);

        try {
            status = TaskStatus.fromValue(
                    a.getInteger(
                            R.styleable.TodoAccordion_status,
                            TaskStatus.INCOMPLETE.getValue())
            );
            title = (a.getString(R.styleable.TodoAccordion_title) != null) ? a.getString(R.styleable.TodoAccordion_title) : context.getString(R.string.placeholder);
            expanded = a.getBoolean(R.styleable.TodoAccordion_expanded, false);
            description = (a.getString(R.styleable.TodoAccordion_description) != null) ? a.getString(R.styleable.TodoAccordion_description) : context.getString(R.string.placeholder);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(spToPx(TITLE_FONT_SIZE, displayMetrics));
        titlePaint.setColor(getResources().getColor(R.color.colorTextPrimary, getContext().getTheme()));

        titleHeight = titlePaint.getTextSize();

        descriptionPaint = new TextPaint(titlePaint);
        descriptionPaint.setTextSize(spToPx(DESCRIPTION_FONT_SIZE, displayMetrics));

        checkBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        checkBoxPaint.setColor(getResources().getColor(R.color.colorTextPrimary, getContext().getTheme()));
        checkBoxPaint.setStrokeWidth(spToPx(2, displayMetrics));
        checkBoxPaint.setStyle(Paint.Style.STROKE);
        checkBoxPaint.setStrokeCap(Paint.Cap.SQUARE);

        checkBoxFillPaint = new Paint(checkBoxPaint);
        checkBoxFillPaint.setStrokeWidth(spToPx(7, displayMetrics));
        checkBoxFillPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minW, widthMeasureSpec, 1);

        staticLayout = StaticLayout.Builder.obtain(description, 0, description.length(), descriptionPaint, width - padding * 2)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD)
                .setLineSpacing(0, 1)
                .setIncludePad(false)
                .setMaxLines(7)
                .setEllipsize(TextUtils.TruncateAt.END)
                .build();

        int  minH = initHeight = ( int ) titleHeight + getPaddingBottom() + getPaddingTop() + getSuggestedMinimumHeight() + padding;
        int height = resolveSizeAndState(minH, heightMeasureSpec, 0);
        expandHeight = staticLayout.getHeight() + padding * 2 + initHeight;

        setMeasuredDimension(width, height);
        calculateToggles();
    }

    private void calculateToggles() {
        int yOffset = 5;
        int xOffset = 50;

        checkBox = calculateCheckBoxRect(xOffset, yOffset);
        arrow = calculateArrow(xOffset, yOffset);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        switch (status){
            case INCOMPLETE:
                canvas.drawRect(checkBox, checkBoxPaint);
                titlePaint.setStrikeThruText(false);
                break;
            case IN_PROGRESS:
                canvas.drawRect(checkBox, checkBoxPaint);
                canvas.drawPoint(checkBox.centerX(), checkBox.centerY(), checkBoxFillPaint);
                titlePaint.setStrikeThruText(false);
                break;
            case COMPLETE:
                canvas.drawRect(checkBox, checkBoxFillPaint);
                titlePaint.setStrikeThruText(true);
                break;
        }

        canvas.drawText(title, ( int ) (getWidth() - titlePaint.measureText(title)) >> 1, titleHeight, titlePaint);
        canvas.drawLines(arrow, checkBoxPaint);

        canvas.translate(padding, titleHeight + (padding * 2));
        staticLayout.draw(canvas);

        canvas.restore();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getStatusValue() {
        return status.getValue();
    }

    public void setStatus(int status) {
        this.status = TaskStatus.fromValue(status);
        invalidate();
        requestLayout();
    }

    public void cycleStatus() {
        this.status = TaskStatus.fromValue(this.status.getValue() + 1);
        invalidate();
        requestLayout();
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        invalidate();
        requestLayout();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
        requestLayout();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        initAnimators();
    }

    private void initAnimators() {
        ValueAnimator heightAnimator = ValueAnimator.ofInt(getHeight(), ( expanded ) ?  expandHeight : initHeight);
        heightAnimator.addUpdateListener(valueAnimator -> {
            int currentHeight = ( int ) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = currentHeight;
            setLayoutParams(layoutParams);
        });

        heightAnimator.setDuration(350);
        heightAnimator.start();

        ValueAnimator arrowAnimator = ValueAnimator.ofInt(( expanded ) ? 35 : -35, ( expanded ) ? -35 : 35);
        arrowAnimator.addUpdateListener(valueAnimator -> {
            arrowDip = ( int ) valueAnimator.getAnimatedValue();
        });

        arrowAnimator.setDuration(250);
        arrowAnimator.start();
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        invalidate();
        requestLayout();
    }

    private float[] calculateArrow(int xOffset, int yOffset) {
        int y = ( int ) ((titleHeight + padding + getPaddingTop() + yOffset - arrowDip)) >> 1;
        int x = getMeasuredWidth() - xOffset - (arrowArmLength * 2);
        return new float[] {
                x, y, //Line1 Start
                arrowArmLength + x, arrowDip + y, //Line1 End
                arrowArmLength + x, arrowDip + y, //Line2 Start
                (arrowArmLength * 2) + x, y //Line2 End
        };
    }

    private Rect calculateCheckBoxRect(int xOffset, int yOffset) {
        int left = xOffset + getPaddingLeft();
        int top = 10 + getPaddingTop() + yOffset;
        int bottom = initHeight - top;
        int right = left + bottom - top;

        return new Rect(left, top, right, bottom);
    }

    public boolean pointInCheckBox(int x, int y) {
        return this.checkBox.contains(x, y);
    }

    public static int spToPx(int fontSize, DisplayMetrics displayMetrics) {
        return ( int ) (fontSize * displayMetrics.scaledDensity);
    }

    public static int pxToSp(int pixels, DisplayMetrics displayMetrics) {
        return ( int ) (pixels / displayMetrics.scaledDensity);
    }
}
