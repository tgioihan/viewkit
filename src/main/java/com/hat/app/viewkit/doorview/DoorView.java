package com.hat.app.viewkit.doorview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.hat.app.viewkit.R;


public class DoorView<T extends View & DoorView.IBottomDoorView> extends ViewGroup {
    private static final float Filling_HINT = 1200;
    private static final float DRAG_HINT = 20;
    protected FrameLayout viewAbove;
    protected FrameLayout viewBottom;
    protected View viewAboveContent;
    protected T viewBottomContent;
    protected State state = State.Collapsed;
    protected int offset;
    private float lastY;
    private float initialY;
    private boolean dragging;
    private boolean bottomTouch;
    private boolean aboveTouch;
    private VelocityTracker mVelocityTracker;
    FillingRunnable fillingRunnable;
    Callback callback;
    private int aboveHeight;

    public DoorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fillingRunnable = new FillingRunnable();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DoorView);
            try {
                int contentLayoutId = a.getResourceId(R.styleable.DoorView_contentView, 0);
                LayoutInflater.from(context).inflate(contentLayoutId, this, true);
                int viewAboveLayoutId = a.getResourceId(R.styleable.DoorView_viewAboveId, 0);
                int viewBottomLayoutId = a.getResourceId(R.styleable.DoorView_viewBottomId, 0);
                viewAbove = (FrameLayout) findViewById(viewAboveLayoutId);
                viewBottom = (FrameLayout) findViewById(viewBottomLayoutId);
            } finally {
                a.recycle();
            }
        }
        setFocusableInTouchMode(true);
    }

    public void setViewAbove(View view) {
        if (viewAboveContent == view) {
            return;
        }
        viewAbove.removeView(viewAboveContent);
        this.viewAboveContent = view;
        viewAbove.addView(viewAboveContent);
    }

    public void setViewBottom(T view) {
        if (viewBottomContent == view) {
            return;
        }
        viewBottom.removeView(viewBottomContent);
        this.viewBottomContent = view;
        viewBottom.addView(viewBottomContent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);

        aboveHeight = height * 2 /5;
        measureChild(viewAbove, widthMeasureSpec,  MeasureSpec.makeMeasureSpec(aboveHeight, MeasureSpec.AT_MOST));

        int bottomHeight = height - aboveHeight;
        measureChild(viewBottom, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(bottomHeight, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int height = b - t;
        offset = getMinOffset();
        viewAbove.layout(0, -aboveHeight, viewAbove.getMeasuredWidth(), 0);
        viewBottom.layout(0, height - offset, viewBottom.getMeasuredWidth(), height - offset + viewBottom.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialY = lastY = event.getY();
                dragging = false;
                bottomTouch = isBottomTouched(event);

                break;
            case MotionEvent.ACTION_MOVE:
                if(bottomTouch){
                    if (!dragging) {
                        if (Math.abs(event.getY() - initialY) > DRAG_HINT) {
                            dragging = true;
                        }
                    }
                    lastY = event.getY();
                }

                break;
            default:
                break;
        }
        return dragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialY = lastY = event.getY();
                dragging = false;
                bottomTouch = isBottomTouched(event);

                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                setDrawingCacheEnabled(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if(bottomTouch){
                    if (!dragging) {
                        if (Math.abs(event.getY() - initialY) > DRAG_HINT) {
                            dragging = true;
                            setDrawingCacheEnabled(true);
                        }
                    } else {
                        float div = event.getY() - lastY;
                        moveViewBottomComponentBy(div);
                    }

                    lastY = event.getY();
                    if(mVelocityTracker!=null){
                        mVelocityTracker.addMovement(event);
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(bottomTouch){
                    if(mVelocityTracker!=null){
                        mVelocityTracker.addMovement(event);
                        mVelocityTracker.computeCurrentVelocity(1000);
                        float velocityY = mVelocityTracker.getYVelocity();
                        touchUp(velocityY, event);
                    }else{
                        touchUp(0, event);
                    }
                }


                break;
            default:
                break;
        }
        return bottomTouch || aboveTouch;
    }

    public void setState(State state) {
        if(this.state == state){
            return;
        }
        this.state = state;
        int dy = 0;
        boolean up = state == State.Exapanded;
        if (up) {
            dy = getHeight() - getMaxOffset() - (int) viewBottom.getY();
        } else {
            dy = (getHeight() - getMinOffset()) - (int) viewBottom.getY();
        }
        int duration = Math.abs(dy) * 1600 / viewBottom.getHeight();
        invalidateOffset();
        fillingRunnable.scroll((int) viewBottom.getY(), dy, duration);
        requestFocus();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Let the focused view and/or our descendants get the key first
        return super.dispatchKeyEvent(event) || executeKeyEvent(event);
    }

    /**
     * You can call this function yourself to have the scroll view perform
     * scrolling from a key event, just as if the event had been dispatched to
     * it by the view hierarchy.
     *
     * @param event The key event to execute.
     * @return Return true if the event was handled, else false.
     */
    public boolean executeKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (state == State.Exapanded) {
                        setState(State.Collapsed);
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (state == State.Exapanded) {
                setState(State.Collapsed);
                return true;
            }

        }
        return super.onKeyUp(keyCode, event);
    }

    private void touchUp(float velocityY, MotionEvent event) {
        if (Math.abs(velocityY) > Filling_HINT) {
            int dy = 0;
            if (velocityY > 0) {
                state = State.Collapsed;
                dy = (getHeight() - getMinOffset()) - (int) viewBottom.getY();
            } else {
                state = State.Exapanded;

                dy = getHeight() - getMaxOffset() - (int) viewBottom.getY();
            }
            int duration = Math.abs(dy) * 1600 / viewBottom.getHeight();
            invalidateOffset();
            fillingRunnable.scroll((int) viewBottom.getY(), dy, duration);
        } else {
            boolean up = getHeight() - viewBottom.getY() > viewBottom.getHeight() / 2;
            if(!dragging && isBottomTouched(event) ){
                up = !up;
            }
            int dy = 0;

            if (up) {
                state = State.Exapanded;
                dy = getHeight() - getMaxOffset() - (int) viewBottom.getY();
            } else {
                state = State.Collapsed;
                dy = (getHeight() - getMinOffset()) - (int) viewBottom.getY();
            }
            int duration = Math.abs(dy) * 1600 / viewBottom.getHeight();
            invalidateOffset();
            fillingRunnable.scroll((int) viewBottom.getY(), dy, duration);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }

        removeCallbacks(fillingRunnable);
    }

    public FrameLayout getViewBottom() {
        return viewBottom;
    }

    private void moveViewBottomComponentBy(float div) {
        ViewCompat.setTranslationY(viewBottom, viewBottom.getTranslationY() + div);
        offset = caculateBottomOffset();
        float percent = offset / ((float) viewBottom.getMeasuredHeight() - getMinOffset());
        ViewCompat.setTranslationY(viewAbove, -percent * viewAbove.getMeasuredHeight());

        onOffsetChange(percent);
    }

    private void moveViewAboveComponentBy(float div) {
        ViewCompat.setTranslationY(viewAbove, viewAbove.getTranslationY() + div);
        ViewCompat.setTranslationY(viewBottom, viewBottom.getTranslationY() - div);
        offset = caculateBottomOffset();
        float percent = offset / ((float) viewBottom.getMeasuredHeight() - getMinOffset());
        onOffsetChange(percent);
    }

    private int caculateBottomOffset() {
        return (int) viewBottom.getTranslationY();
    }

    private boolean isBottomTouched(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        if (viewBottom.getX() < x && viewBottom.getX() + viewBottom.getWidth() > x && viewBottom.getY() < y && viewBottom.getY() + getMinOffset() > y) {
            return true;
        }
        return false;
    }

    private boolean isAboveTouched(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        if (viewAbove.getX() < x && viewAbove.getX() + viewAbove.getWidth() > x && viewAbove.getY() < y && viewAbove.getY() + viewAbove.getHeight() > y) {
            return true;
        }
        return false;
    }

    private int invalidateOffset() {
        if (state == State.Collapsed) {
            if (viewBottomContent != null) {
                return viewBottomContent.getOffsetOnCollapsed();
            }
        } else {
            return viewBottom.getMeasuredHeight();
        }
        return 0;
    }

    public int getMinOffset() {
        return viewBottomContent.getOffsetOnCollapsed();
    }

    public int getMaxOffset() {
        return viewBottom.getMeasuredHeight();
    }

    protected FrameLayout getViewAbove() {
        return viewAbove;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public class FillingRunnable implements Runnable {

        private OverScroller overScroller;
        private int lastY;

        public FillingRunnable() {
            overScroller = new OverScroller(getContext());
        }

        public void start(int velocity, int startY, int minY, int maxY) {
            lastY = startY;
            overScroller.fling(0, startY, 0, velocity, 0, 0, minY, maxY);
            animationStart();
            postOnAnimation(this);
        }

        public void scroll(int startY, int dy, int duration) {
            lastY = startY;
            overScroller.startScroll(0, startY, 0, dy, duration);
            animationStart();
            postOnAnimation(this);
        }

        @Override
        public void run() {
            boolean more = overScroller.computeScrollOffset();
            int y = overScroller.getCurrY();
            float div = y - lastY;
            lastY = y;
            moveViewBottomComponentBy(div);
            if (more) {
                postOnAnimation(this);
            } else {
                animationEnd();
            }
        }
    }

    private void validateState() {
        offset = caculateBottomOffset();
        if (offset == 0) {
            state = State.Collapsed;
        } else {
            state = State.Exapanded;
            requestFocus();
        }

    }

    public State getState() {
        return state;
    }

    private void animationEnd() {
        ViewCompat.setLayerType(viewAbove, ViewCompat.LAYER_TYPE_NONE, null);
        ViewCompat.setLayerType(viewBottom, ViewCompat.LAYER_TYPE_NONE, null);
        validateState();
        onOffsetStateChange(state, false);
        setDrawingCacheEnabled(false);
    }

    protected void onOffsetChange(float percent) {
        if (callback != null)
            callback.onOffsetChange(percent);
    }

    protected void onOffsetStateChange(State state, boolean scrolling) {
        if (callback != null)
            callback.onOffsetStateChange(state, scrolling);
    }

    private void animationStart() {
        ViewCompat.setLayerType(viewAbove, ViewCompat.LAYER_TYPE_HARDWARE, null);
        ViewCompat.setLayerType(viewBottom, ViewCompat.LAYER_TYPE_HARDWARE, null);
        onOffsetStateChange(state, true);
    }


    public enum State {
        Collapsed, Exapanded
    }

    public interface IBottomDoorView {
        int getOffsetOnCollapsed();
    }

    public interface Callback {

        void onOffsetChange(float percent);

        void onOffsetStateChange(State state, boolean scrolling);
    }
}
