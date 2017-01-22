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
import cn.wycode.wengine.utils.Timer;

/**
 * Created by wy
 * on 2017/1/20.
 */

public class SnakeDemo extends Wengine {


    private Bitmap bm_bg;
    private int lineOffset = 8;

    private Rect rect_bg_src;
    private float rectBgLeft, rectBgTop;

    private float degree;

    private float speed = 50;
    private float touchX, touchY, speedX, speedY;

    private Timer timerBg;


    @Override
    protected void init() {
        super.init();
        setShowFps(true);
    }

    @Override
    public void load() {
        bm_bg = Bitmap.createBitmap(ScreenInfo.width * 3, ScreenInfo.height * 3, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm_bg);
        c.drawColor(Color.parseColor("#eeeeee"));
        Paint linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#cccccc"));
        for (int i = 1; i * lineOffset < bm_bg.getWidth(); i++) {
            c.drawLine(i * lineOffset, 0, i * lineOffset, bm_bg.getHeight(), linePaint);
        }
        for (int i = 1; i * lineOffset < bm_bg.getHeight(); i++) {
            c.drawLine(0, i * lineOffset, bm_bg.getWidth(), i * lineOffset, linePaint);
        }

        rect_bg_src = new Rect(ScreenInfo.width, ScreenInfo.height, ScreenInfo.width * 2, ScreenInfo.height * 2);
        rectBgLeft = rect_bg_src.left;
        rectBgTop = rect_bg_src.top;

        BaseSprite sprite = new BaseSprite(ScreenInfo.center.x, ScreenInfo.center.y, 10, 10);

        addSprite(sprite);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bm_bg, rect_bg_src, ScreenInfo.screenRect, null);
    }

    @Override
    public void update() {
        if (timerBg == null) {
            timerBg = new Timer();
        }
        long elapse = timerBg.getElapse();
        rectBgLeft += speedX * elapse / 1000f;
        rectBgTop += speedY * elapse / 1000f;
        rect_bg_src.set((int) rectBgLeft, (int) rectBgTop, (int) rectBgLeft + ScreenInfo.width, (int) rectBgTop + ScreenInfo.height);
    }

    @Override
    public void collided(BaseSprite a, BaseSprite b) {

    }

    @Override
    public void touch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                break;
            default:
                double angle = Math.atan((event.getY() - touchY) / (event.getX() - touchX));

                speedY = (float) (Math.sin(angle) * speed);
                speedX = (float) (Math.cos(angle) * speed);

                if (event.getX() - touchX < 0) {
                    speedX = -speedX;
                    speedY = -speedY;
                }

        }
    }

    @Override
    public void release() {
        if (bm_bg != null)
            bm_bg.recycle();
    }
}
