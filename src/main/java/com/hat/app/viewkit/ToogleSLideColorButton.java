package com.hat.app.viewkit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ToogleSLideColorButton extends ViewGroup {


    private static final float MIN_DISTANCE_TO_SCROLL = 10;
    private Drawable selectedControlDrawable;

    private Drawable selectedBgDrawable;

    private Drawable disSelectedBgDrawable;

    private Drawable disSelectedControlDrawable;

    private ImageView selectedControl;

    private ImageView selectedBg;

    private ImageView disSelectedBg;

    private ImageView disSelectedControl;

    private int offsetX;
    private int controlWidth;
    private int bgWidth;
    private float lastX;
    private float initialX;
    private boolean scrolling;

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private boolean checked = false;

    private IToogleSLideControl checkedChangeListener;

    public IToogleSLideControl getCheckedChangeListener() {
        return checkedChangeListener;
    }

    public void setCheckedChangeListener(IToogleSLideControl checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    public boolean isChecked() {
        return checked;
    }


    public ToogleSLideColorButton(Context context) {
        super(context);
    }

    public ToogleSLideColorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToogleSLideColorButton);
        try {
            selectedControlDrawable = a.getDrawable(R.styleable.ToogleSLideColorButton_selected_control);
            selectedBgDrawable = a.getDrawable(R.styleable.ToogleSLideColorButton_selected_bg);
            disSelectedBgDrawable = a.getDrawable(R.styleable.ToogleSLideColorButton_disselected_bg);
            disSelectedControlDrawable = a.getDrawable(R.styleable.ToogleSLideColorButton_disselected_control);

            selectedControl = new ImageView(getContext());
            selectedBg = new ImageView(getContext());
            disSelectedBg = new ImageView(getContext());
            disSelectedControl = new ImageView(getContext());

            selectedControl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            selectedBg.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            disSelectedBg.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            disSelectedControl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            selectedControl.setImageDrawable(selectedControlDrawable);
            selectedBg.setImageDrawable(selectedBgDrawable);
            disSelectedBg.setImageDrawable(disSelectedBgDrawable);
            disSelectedControl.setImageDrawable(disSelectedControlDrawable);

            addView(disSelectedBg);
            addView(selectedBg);
            addView(disSelectedControl);
            addView(selectedControl);


        } finally {
            a.recycle();
        }
    }

    public void destroy() {
        selectedControl.setImageDrawable(null);
        selectedBg.setImageDrawable(null);
        disSelectedBg.setImageDrawable(null);
        disSelectedControl.setImageDrawable(null);
        selectedControlDrawable = null;
        selectedBgDrawable = null;
        disSelectedBgDrawable = null;
        disSelectedControlDrawable = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        calculateOffset();
        disSelectedControl.layout(offsetX, 0, offsetX + controlWidth, controlWidth);
        selectedControl.layout(offsetX, 0, offsetX + controlWidth, controlWidth);
        disSelectedBg.layout(controlWidth / 2, height / 2 - bgWidth / 2, controlWidth / 2 + bgWidth, height / 2 + bgWidth / 2);
        selectedBg.layout(controlWidth / 2, height / 2 - bgWidth / 2, controlWidth / 2 + bgWidth, height / 2 + bgWidth / 2);

        applyAlpha(offsetX / (width - controlWidth) * 255);
    }

    private void applyAlpha(int alpha) {
        selectedBg.setAlpha(alpha);
        disSelectedBg.setAlpha(255 - alpha);
        selectedControl.setAlpha(alpha);
        disSelectedControl.setAlpha(255 - alpha);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);

        measureChild(selectedControl, width, height);
        measureChild(selectedBg, width, height);
        controlWidth = selectedControl.getMeasuredHeight();
        bgWidth = selectedBg.getMeasuredWidth();
        setMeasuredDimension(bgWidth + selectedControl.getMeasuredWidth(), controlWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                initialX = lastX = ev.getX();
                setOffset(initialX - offsetX);
                scrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final float disX = ev.getX() - lastX;
                lastX = ev.getX();

                if (!scrolling && lastX - initialX > MIN_DISTANCE_TO_SCROLL) {
                    scrolling = true;
                } else {
                    setOffset(disX);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (scrolling) {
                    if (selectedControl.getLeft() > getWidth() / 2) {
                        setToogleButtonChecked(true);
                    } else {
                        setToogleButtonChecked(false);
                    }
                } else {
                    setToogleButtonChecked(!checked);
                }

                break;

            case MotionEvent.ACTION_CANCEL:
                if (selectedControl.getLeft() > getWidth() / 2) {
                    setToogleButtonChecked(true);
                } else {
                    setToogleButtonChecked(false);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setToogleButtonChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (checkedChangeListener != null) {
                checkedChangeListener.onCheckedChange(checked);
            }
        }
        calculateOffset();
        reLayoutControl();
    }

    public void toogle(){
        setToogleButtonChecked(!checked);
    }

    private void calculateOffset() {
        if (checked) {
            offsetX = getWidth() - controlWidth;
        } else {
            offsetX = 0;
        }
    }

    private void setOffset(float disX) {
        offsetX += disX;
        if (offsetX < 0) {
            offsetX = 0;
        } else if (offsetX + controlWidth > getWidth()) {
            offsetX = getWidth() - controlWidth;
        }
        reLayoutControl();
    }

    private void reLayoutControl() {
        if (bgWidth == 0)
            return;
        disSelectedControl.layout(offsetX, 0, offsetX + controlWidth, controlWidth);
        selectedControl.layout(offsetX, 0, offsetX + controlWidth, controlWidth);
        applyAlpha(offsetX / (getWidth() - controlWidth) * 255);
    }

    public interface IToogleSLideControl {
        public void onCheckedChange(boolean checked);
    }

}