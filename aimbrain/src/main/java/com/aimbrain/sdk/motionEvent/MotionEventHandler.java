package com.aimbrain.sdk.motionEvent;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.aimbrain.sdk.Manager;
import com.aimbrain.sdk.collectors.MotionEventCollector;
import com.aimbrain.sdk.collectors.SensorEventCollector;


public class MotionEventHandler {

    private static MotionEventHandler motionEventHandler;

    public static MotionEventHandler getInstance(){
        if(motionEventHandler == null){
            motionEventHandler = new MotionEventHandler();
        }
        return motionEventHandler;
    }

    private MotionEventHandler() {
    }

    public void touchCaptured(MotionEvent motionEvent, long timestamp, Window window) {
        Log.i("Handler", "Touch captured:" + motionEvent.getAction());

        View view = findViewBelowMotionEvent(motionEvent, window);

        if(!Manager.getInstance().isViewIgnored(view)) {
            SensorEventCollector.getInstance().startCollectingData(500);
        }

        MotionEventCollector.getInstance().processMotionEvent(motionEvent, view, timestamp);
    }

    private View findViewBelowMotionEvent(MotionEvent event, Window window) {
        ViewGroup rootView = (ViewGroup)window.getDecorView();
        float x = event.getRawX();
        float y = event.getRawY();
        return findViewAtWindowPoint(rootView, x, y);
    }

    private View findViewAtWindowPoint(ViewGroup viewGroup, float x, float y) {
        for (int i = viewGroup.getChildCount()-1; i >= 0; i--) {
            final View child = viewGroup.getChildAt(i);
            if(isPointInView(x, y, child)) {
                if (child instanceof ViewGroup) {
                    return findViewAtWindowPoint((ViewGroup) child, x, y);
                }
                return child;
            }
        }
        return viewGroup;
    }

    private boolean isPointInView(float x, float y, View view){
        Rect outRect = new Rect();
        int[] viewStartLocation = new int[2];
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(viewStartLocation);
        outRect.offset(viewStartLocation[0], viewStartLocation[1]);
        return outRect.contains((int)x, (int)y);
    }
}
