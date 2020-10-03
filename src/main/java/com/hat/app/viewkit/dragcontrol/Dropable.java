package com.hat.app.viewkit.dragcontrol;

/**
 * Created by tuannx on 8/9/2017.
 */

public interface Dropable {

    void onDrop();
    void onDragOver();
    void onDragExit();
    boolean acceptDrop();
}
