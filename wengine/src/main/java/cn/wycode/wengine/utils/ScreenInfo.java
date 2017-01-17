package cn.wycode.wengine.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by wy
 * on 2016/12/29.
 */

public class ScreenInfo {

    public static Point center;
    public static int width;
    public static int height;
    public static float density;

    public static RectF screenRect;

    //不能构造此类对象
    private ScreenInfo(){

    }

    public static void init(Context context){
        density = context.getResources().getDisplayMetrics().density;
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        center = new Point(width / 2, height / 2);
        screenRect = new RectF(0,0,width,height);
    }

}