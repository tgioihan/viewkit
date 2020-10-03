package com.hat.app.viewkit.collectionviewadapter;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public interface AnimatorProvider {
    ViewPropertyAnimatorCompat getAnimator(View view, int position);

    public static final class ScaleAnimationProvider implements AnimatorProvider {

        @Override
        public ViewPropertyAnimatorCompat getAnimator(View view, int position) {
            //                ViewCompat.setX(view,-view.getWidth());
            ViewCompat.setAlpha(view, 0);
            ViewCompat.setScaleY(view, 0);
            ViewCompat.setScaleX(view, 0);
            return ViewCompat.animate(view)
//                        .translationX(0)
                    .scaleX(1)
                    .scaleY(1)
                    .setDuration(500)
                    .setInterpolator(new DecelerateInterpolator(4))
                    .alpha(1);
        }
    }
}
