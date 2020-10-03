package com.hat.app.viewkit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tgioihan.imageloader.util.DebugLog;
import com.tgioihan.imageloader.view.RecyclingViewDrawableHelper;

/**
 * Created by tuannx on 10/22/2016.@43ae6e30
 */
public class RecycleDrawableRelativeView extends RelativeLayout {

    private RecyclingViewDrawableHelper helper;

    public RecycleDrawableRelativeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        helper = new RecyclingViewDrawableHelper(this);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DebugLog.d("");
        helper.onDetachedFromWindow();
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        helper.setBackgroundDrawable(background);
        super.setBackgroundDrawable(background);
    }
}
