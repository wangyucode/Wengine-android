package cn.wycode.game.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import cn.wycode.wengine.Wengine;
import cn.wycode.wengine.animation.MoveAnimation;
import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.utils.ScreenInfo;
import cn.wycode.wengine.utils.Timer;

/**
 * Created by wy
 * on 2017/1/19.
 */

public class AirplaneDemo extends Wengine {

    private BaseSprite airplane;

    private Bitmap bm_space, bg_2x, bm_airplane;

    private Rect draw_bg_rect;
    private int bg_top;
    private Timer bg_timer, bulletTimer;
    private int bg_seed = 100;

    private int planeSize = 100;

    private int firePerSecond = 30;

    private int bulletW = 4;
    private int bulletH = 8;
    private int bulletSpeed = 500;

    @Override
    protected void init() {
        super.init();
        setShowFps(true);
    }

    @Override
    public void load() {
        bm_space = texture.loadTexture("img/space.png");
        bm_airplane = texture.loadTexture("img/airplane.png");

        bg_top = ScreenInfo.height * 3;
        draw_bg_rect = new Rect(0, bg_top, ScreenInfo.width, ScreenInfo.height * 4);

        bg_timer = new Timer();

        bg_2x = Bitmap.createBitmap(ScreenInfo.width, ScreenInfo.height * 4, Bitmap.Config.ARGB_8888);
        Canvas c_bg = new Canvas(bg_2x);
        Rect dst = new Rect(0, 0, ScreenInfo.width, ScreenInfo.height * 2);
        c_bg.drawBitmap(bm_space, null, dst, null);
        dst.set(0, ScreenInfo.height * 2, ScreenInfo.width, ScreenInfo.height * 4);
        c_bg.drawBitmap(bm_space, null, dst, null);

        airplane = new BaseSprite(bm_airplane, ScreenInfo.center.x - ScreenInfo.dp2px(planeSize) / 2, ScreenInfo.height - ScreenInfo.dp2px(planeSize) - 20, planeSize, planeSize);
        airplane.convertToDpSize();
        airplane.setName("airplane");

        addSprite(airplane);

        bulletTimer = new Timer();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bg_2x, draw_bg_rect, ScreenInfo.screenRect, null);
    }

    @Override
    public void update() {
        //滚动背景
        long elapsed = bg_timer.getElapse();
        bg_top -= elapsed / 1000f * bg_seed;
        if (bg_top < ScreenInfo.height) {
            bg_top = ScreenInfo.height * 3;
        }
        draw_bg_rect.set(0, bg_top, ScreenInfo.width, ScreenInfo.height + bg_top);

        //生成子弹
        if (bulletTimer.getElapse() > 1000f / firePerSecond) {
            MoveAnimation animation = new MoveAnimation(-90, bulletSpeed);
            BaseSprite bullet = null;
            float bulletX = airplane.getCenterX() - ScreenInfo.dp2px(bulletW) / 2f;
            float bulletY = airplane.getY() - ScreenInfo.dp2px(bulletH) - 2;
            for (BaseSprite s : spriteRecyclePool) {
                if ("bullet".equals(s.getName())) {
                    s.setAlive(true);
                    s.removeAllAnimation();
                    s.setX(bulletX);
                    s.setY(bulletY);
                    bullet = s;
                    spriteRecyclePool.remove(s);
                }
            }

            if (bullet == null) {
                bullet = new BaseSprite(bulletX, bulletY, bulletW, bulletH);
                bullet.convertToDpSize();
                bullet.setName("bullet");
            }
            bullet.addAnimation(animation);
            addSprite(bullet);
        }
    }

    @Override
    public void collided(BaseSprite a, BaseSprite b) {

    }

    @Override
    public void touch(MotionEvent event) {
        airplane.setX(event.getX() - airplane.getW() / 2);
        airplane.setY(event.getY() - airplane.getH() / 2);
    }
}
