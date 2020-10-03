package com.hat.app.viewkit;

/**
 * Created by tgioihan on 12/29/2014.
 */
public interface ISlide {
    void onStartSlide();
    void onSlide(int offset, int maxDistance);
    void onSlideFinish(boolean isIn);
}
