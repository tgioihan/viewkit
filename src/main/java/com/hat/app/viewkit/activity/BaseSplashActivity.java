package com.hat.app.viewkit.activity;

import android.os.Bundle;

import com.hat.app.viewkit.R;
import com.hat.app.viewkit.SplashView;

/**
 * Created by tuannx on 8/11/2017.
 */

public class BaseSplashActivity extends BaseActivity {
    private boolean finishSplash;
    protected SplashView  splashView;

    @Override
    protected void afterInitView() {

    }

    @Override
    protected void preOnCreate() {

    }

    @Override
    protected void preOnCreate(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.splash_activity;
    }

    @Override
    protected void initView() {
        splashView = (SplashView) findViewById(R.id.splashView);
        onSetUp(splashView);
        splashView.startBehavior(new SplashView.AnimationCallback() {
            @Override
            public void onEnd(SplashView splashView) {
                onSplash(splashView);
            }
        });
    }

    protected void onSetUp(SplashView splashView) {

    }

    protected void onSplash(SplashView splashView) {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        if(finishSplash){
            super.onBackPressed();
        }
    }
}
