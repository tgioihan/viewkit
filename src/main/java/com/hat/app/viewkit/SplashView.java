package com.hat.app.viewkit;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SplashView extends RelativeLayout {
    private final ViewGroup splashRoot;
    private TextView logoText;
    private ImageView icon;
    private ImageView textBottom;
    private AnimationCallback callback;

    public SplashView(Context context, AttributeSet attrs) {
        super(context, attrs);

        splashRoot = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.splashview, this, false);
        initComponent(splashRoot);

        addView(splashRoot);
    }

    public void setUp(int iconId,int textId){
        icon.setImageResource(iconId);
        logoText.setText(textId);
    }

    private void initComponent(ViewGroup view) {
        logoText = (TextView)view.findViewById(R.id.logoText);
        icon = (ImageView) view.findViewById(R.id.icon);
        textBottom = (ImageView) view.findViewById(R.id.textBottom);
    }

    public void startBehavior(AnimationCallback callback) {
        this.callback = callback;
        animateView(icon, new AnimationCallback() {
            @Override
            public void onEnd(SplashView splashView) {
                secondStep();
            }
        });

    }

    private void secondStep() {
        animateView(logoText, new AnimationCallback() {
            @Override
            public void onEnd(SplashView splashView) {
                callback.onEnd(SplashView.this);
            }
        });
        animateView(textBottom, new AnimationCallback() {
            @Override
            public void onEnd(SplashView splashView) {

            }
        });
    }

    private void animateView(View view, final AnimationCallback callback) {
        view.setScaleX(0);
        view.setScaleY(0);
        ViewCompat.setAlpha(view, 0);
        ViewCompat.animate(view).scaleX(1).scaleY(1).alpha(1).setDuration(600).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                view.setVisibility(VISIBLE);
                ViewCompat.setLayerType(view, ViewCompat.LAYER_TYPE_HARDWARE, null);
            }

            @Override
            public void onAnimationEnd(View view) {
                ViewCompat.setLayerType(view, ViewCompat.LAYER_TYPE_NONE, null);
                callback.onEnd(SplashView.this);
            }

            @Override
            public void onAnimationCancel(View view) {
                ViewCompat.setLayerType(view, ViewCompat.LAYER_TYPE_NONE, null);
            }
        });
    }

    public interface AnimationCallback {
        void onEnd(SplashView splashView);
    }
}
