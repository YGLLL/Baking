package com.example.ygl.baking.Util;

import android.content.Context;

public class Util{
    //转换dip为px
    public static int convertDipOrPx(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip*scale + 0.5f*(dip>=0?1:-1));
    }
}