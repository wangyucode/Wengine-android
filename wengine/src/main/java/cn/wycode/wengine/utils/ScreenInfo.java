package cn.wycode.wengine.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.DisplayMetrics;

/**
 * Created by wy
 * on 2016/12/29.
 */

public class ScreenInfo {

    public static Point center;
    public static int width;
    public static int height;
    public static float density;
    public static DisplayMetrics metrics;

    public static RectF screenRect;

    //不能构造此类对象
    private ScreenInfo(){

    }

    public static void init(Context context){
        metrics = context.getResources().getDisplayMetrics();
        density = metrics.density;
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        center = new Point(width / 2, height / 2);
        screenRect = new RectF(0,0,width,height);
    }

    public static float px2dp(float px){
        return px/density;
    }

    public static float dp2px(float dp){
        return dp*density;
    }

}
