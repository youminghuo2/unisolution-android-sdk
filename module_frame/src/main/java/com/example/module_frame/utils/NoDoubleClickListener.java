package com.example.module_frame.utils;

import android.icu.util.Calendar;
import android.view.View;

public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1500;

    private long lastClickTime = 0;

    protected abstract void onNoDoubleClick(View v);

    @Override
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {

            lastClickTime = currentTime;

            onNoDoubleClick(view);
        }
    }
}
