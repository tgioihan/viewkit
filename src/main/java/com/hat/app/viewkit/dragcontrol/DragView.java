package com.hat.app.viewkit.dragcontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tuannx on 8/11/2017.
 */
public class DragView extends View {

    private Bitmap mBitmap;
    Paint paint;

    public DragView(Context context) {
        super(context);
        paint = new Paint();
    }

    public DragView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    public void setBitmap(Bitmap bitmap){
        this.mBitmap = bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap,0,0,paint);
    }
}
