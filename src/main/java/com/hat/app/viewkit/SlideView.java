package com.hat.app.viewkit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;


import java.lang.ref.WeakReference;

/**
 * Created by tgioihan on 12/29/2014.
 */
public class SlideView extends ViewGroup implements ISlide {
    private static final int MAXDURATIONSLIDE = 500;
    protected View content;
    private int childHeight;
    private int childOffset;
    private int childWidth;
    private int alpha;
    private Fillinger fillinger;
    private boolean showAfterLayoutChange;
    private boolean firstLayout;

    public ISlide getSlideChangeListener() {
        return slideListener;
    }

    public void setSlideChangeListener(ISlide slideChangeListener) {
        this.slideListener = slideChangeListener;
    }

    private ISlide slideListener;

    public SlideView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideView);
            try {
                int contentLayoutId = a.getResourceId(R.styleable.SlideView_slideView, 0);
                setContent(contentLayoutId);
            } finally {
                a.recycle();
            }
        }
        fillinger = new Fillinger(context, this);
        setFocusableInTouchMode(true);
        setFocusable(true);
        requestFocus();
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int height = b - t;
        if(!firstLayout){
            childOffset = height;
        }


        content.layout(0, childOffset, childWidth, childOffset + childHeight);
        if (showAfterLayoutChange) {
            show();
            showAfterLayoutChange = false;
        }
        firstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        measureChild(content, widthMeasureSpec, height);
        childHeight = content.getMeasuredHeight();
        childWidth = content.getMeasuredWidth();
        setMeasuredDimension(width, height);
    }

    public void showAfterLayoutChange() {
        showAfterLayoutChange = true;
    }

    public void setContent(int resId) {
        View view = LayoutInflater.from(getContext()).inflate(resId, this, false);
        setContent(view);
    }

    public void setContent(View v) {
        if (content != null)
            removeView(content);
        content = v;
        addView(content);
    }

    public View getContentView() {
        return content;
    }

    private void moveViewByY(int diffY) {
        childOffset += diffY;
        alpha = (int) (Math.abs((getHeight() - childOffset) * 255 / (childHeight)) * 0.5f);
        content.layout(0, childOffset, childWidth, childOffset + childHeight);
        invalidate();
        if (slideListener != null) {
            slideListener.onSlide(childOffset, childHeight);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawARGB(alpha, 0, 0, 0);
        super.dispatchDraw(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isIn() && touchOutSide(event)) {
            toogle();
            return true;
        }
        return false;
    }

    private boolean touchOutSide(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        return x < content.getLeft() || x > content.getRight() || y < content.getTop() || y > content.getBottom();
    }

    public interface IAction {
        void action();
    }

    public IAction getAction() {
        return action;
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    private IAction action;

    public void hide() {
        hide(null);
    }

    public void hide(IAction action) {
        if (isIn()) {
            if (action != null) {
                this.action = action;
            }
            fillinger.startScroll(content.getTop(), getHeight(), childHeight, MAXDURATIONSLIDE);
        }
    }

    public void show() {
        show(null);
    }

    public void show(IAction action) {
        if (!isIn()) {
            if (action != null) {
                this.action = action;
            }
            fillinger.startScroll(content.getTop(), getHeight() - childHeight, childHeight, MAXDURATIONSLIDE);
        }
    }

    public void toogle() {
        fillinger.cancleAnimation();

        if (isIn()) {
            hide();
        } else {
            show();
        }
    }

    public boolean isIn() {
        return content.getTop() < getHeight();
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
                    if (isIn()) {
                        toogle();
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
            if (isIn()) {
                toogle();
                return true;
            }

        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onStartSlide() {
        if (slideListener != null) {
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_HARDWARE, null);
            slideListener.onStartSlide();
        }
    }

    @Override
    public void onSlide(int offset, int maxDistance) {
        if (slideListener != null) {
            slideListener.onSlide(offset, maxDistance);
        }
    }

    @Override
    public void onSlideFinish(boolean isIn) {
        if(isIn){
            requestFocus();
        }
        if (slideListener != null) {
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_HARDWARE, null);
            slideListener.onSlideFinish(isIn);
        }
    }

    private static class Fillinger implements Runnable {
        private Scroller mScroller;
        private int lastY;
        private boolean more;
        private int currentY;
        private int diffY;
        private WeakReference<SlideView> reference;

        Fillinger(Context context, SlideView slideView) {
            mScroller = new Scroller(context);
            reference = new WeakReference<>(slideView);
        }

        void startScroll(float startY, float endY, float maxDistance, int maxDurationForFling) {
            int duration = (int) Math.min(Math.abs((endY - startY)) / maxDistance * maxDurationForFling, maxDurationForFling);
            lastY = (int) startY;
            if (reference.get() != null) {
                reference.get().onStartSlide();
            }
            mScroller.startScroll(0, (int) startY, 0, -(int) (endY - startY), duration);
            if (reference.get() != null) {
                reference.get().setDrawingCacheEnabled(true);
                reference.get().post(this);
            }


        }

        void cancleAnimation() {
            if (reference.get() != null) {
                reference.get().removeCallbacks(this);
            }

        }

        @Override
        public void run() {
            more = mScroller.computeScrollOffset();
            currentY = mScroller.getCurrY();
            diffY = lastY - currentY;
            if (reference.get() != null) {
                reference.get().moveViewByY(diffY);
            }

            lastY = currentY;
            if (more) {
                if (reference.get() != null) {
                    reference.get().post(this);
                }

            } else {
                if (reference.get() != null) {
                    reference.get().setDrawingCacheEnabled(false);
                    reference.get().onSlideFinish(reference.get().isIn());
                    if (reference.get().getAction() != null) {
                        reference.get().getAction().action();
                        reference.get().setAction(null);
                    }
                }

            }
        }
    }
}