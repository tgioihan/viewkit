package com.hat.app.viewkit.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

/**
 * Created by tuannx on 5/13/2016.
 */
public class OffsetAppbarLayout extends AppBarLayout implements AppBarLayout.OnOffsetChangedListener {

    private int verticalOffset;

    public OffsetAppbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    public int getVerticalOffset() {
        return verticalOffset;
    }
}
