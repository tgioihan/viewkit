package com.hat.app.viewkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nguyenxuan on 11/25/2014.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preOnCreate();
        super.onCreate(savedInstanceState);
        preOnCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        initView();
        afterInitView();
        initData();
    }

    protected abstract void afterInitView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract void preOnCreate();

    /**
     * do some work before create view
     *
     * @param savedInstanceState
     */
    protected abstract void preOnCreate(Bundle savedInstanceState);

    /**
     * `q
     *
     * @return layout resource id , use for setcontentView method
     */
    protected abstract int getLayoutResourceId();

    /**
     * innit field and properties for view
     */
    protected abstract void initView();

    /**
     * init data for view
     */
    protected abstract void initData();

}
