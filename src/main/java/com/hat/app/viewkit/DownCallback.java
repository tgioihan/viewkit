package com.hat.app.viewkit;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;

class DownCallback implements ViewPropertyAnimatorListener {

    @Override
    public void onAnimationStart(View view) {
        ViewCompat.setLayerType(view, ViewCompat.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onAnimationEnd(View view) {
        ViewCompat.setLayerType(view, ViewCompat.LAYER_TYPE_NONE, null);
    }

    @Override
    public void onAnimationCancel(View view) {
        ViewCompat.setLayerType(view, ViewCompat.LAYER_TYPE_NONE, null);
    }
}
