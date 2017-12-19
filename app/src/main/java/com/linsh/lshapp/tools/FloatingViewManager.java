package com.linsh.lshapp.tools;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.linsh.utilseverywhere.ContextUtils;


public class FloatingViewManager {
    private static final WindowManager.LayoutParams LAYOUT_PARAMS;
    private View mFloatingView;
    private final WindowManager mWindowManager;

    static {
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.x = 0;
        localLayoutParams.y = 0;
        localLayoutParams.width = -2;
        localLayoutParams.height = -2;
        localLayoutParams.gravity = 51;
        localLayoutParams.type = LayoutParams.TYPE_PHONE;
        localLayoutParams.format = 1;
        localLayoutParams.flags = 40;
        LAYOUT_PARAMS = localLayoutParams;
    }

    public FloatingViewManager() {
        mWindowManager = ((WindowManager) ContextUtils.get().getSystemService(Context.WINDOW_SERVICE));
    }

    public void setView(View floatingView) {
        if (floatingView == null) {
            removeView();
            return;
        }
        if (mFloatingView != null) {
            if (mFloatingView != floatingView) {
                mWindowManager.removeView(mFloatingView);
            } else {
                return;
            }
        }
        floatingView.setLayoutParams(LAYOUT_PARAMS);
        setOnTouchListener(floatingView);
        mFloatingView = floatingView;
        mWindowManager.addView(mFloatingView, LAYOUT_PARAMS);
    }

    public void addView(View floatingView) {
        if (floatingView != null) {
            floatingView.setLayoutParams(LAYOUT_PARAMS);
            setOnTouchListener(floatingView);
            mFloatingView = floatingView;
            mWindowManager.addView(mFloatingView, LAYOUT_PARAMS);
        }
    }

    public void removeView(View floatingView) {
        if (floatingView != null) {
            mWindowManager.removeView(floatingView);
        }
    }

    public void removeView() {
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
            mFloatingView = null;
        }
    }

    private void setOnTouchListener(final View floatingView) {
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            Point preP;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        this.preP = new Point((int) event.getRawX(), (int) event.getRawY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Point curP = new Point((int) event.getRawX(), (int) event.getRawY());
                        LayoutParams layoutParams = (LayoutParams) floatingView.getLayoutParams();
                        if (preP != null) {
                            layoutParams.x += curP.x - preP.x;
                            layoutParams.y += curP.y - preP.y;
                            mWindowManager.updateViewLayout(floatingView, layoutParams);
                        }
                        this.preP = curP;
                        break;
                    case MotionEvent.ACTION_UP:
                        preP = null;
                        break;

                }
                return true;
            }
        });
    }
}