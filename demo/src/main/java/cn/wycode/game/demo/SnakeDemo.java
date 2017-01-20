package cn.wycode.game.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import cn.wycode.wengine.Wengine;
import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.utils.ScreenInfo;

/**
 * Created by wy
 * on 2017/1/20.
 */

public class SnakeDemo extends Wengine {


    private Bitmap bm_bg;
    private int lineOffset = 3;

    private Rect rect_bg_src;

    @Override
    protected void init() {
        super.init();
        setShowFps(true);
    }

    @Override
    public void load() {
        bm_bg = Bitmap.createBitmap(ScreenInfo.width * 3, ScreenInfo.height * 3, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm_bg);
        c.drawColor(Color.parseColor("#dddddd"));
        Paint linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#bbbbbb"));
        int i = 1;
        do {
            c.drawLine(i * lineOffset, 0, i * lineOffset, c.getHeight(), linePaint);
            i++;
        } while (i * lineOffset > c.getWidth());

        rect_bg_src = new Rect(ScreenInfo.width, ScreenInfo.height, ScreenInfo.width * 2, ScreenInfo.height * 2);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bm_bg, rect_bg_src, ScreenInfo.screenRect, null);
    }

    @Override
    public void update() {

    }

    @Override
    public void collided(BaseSprite a, BaseSprite b) {

    }

    @Override
    public void touch(MotionEvent event) {

    }

    @Override
    public void release() {

    }
}
