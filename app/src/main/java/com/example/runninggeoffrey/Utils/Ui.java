package com.example.runninggeoffrey.Utils;

import android.content.Context;
import android.util.TypedValue;

public class Ui {
    public static int dp2px(Context mContext, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                mContext.getResources().getDisplayMetrics());
    }
}
