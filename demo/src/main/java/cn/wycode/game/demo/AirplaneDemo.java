package cn.wycode.game.demo;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import cn.wycode.wengine.Wengine;
import cn.wycode.wengine.animation.MoveAnimation;
import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.sprite.TextSprite;
import cn.wycode.wengine.utils.ScreenInfo;
import cn.wycode.wengine.utils.Timer;

/**
 * Created by wy
 * on 2017/1/19.
 */

public class AirplaneDemo extends Wengine {

    private BaseSprite airplane;

    private Bitmap bm_space, bm_2x, bm_airplane, bm_enemy;

    private Rect draw_bg_rect;
    private int bg_top;
    private Timer bg_timer, bulletTimer, enemyTimer;
    private int bg_seed = 100;

    private int planeSize = 100;

    private int firePerSecond = 6;
    private int enemyPerSecond = 3;
    private int enemySpeed = 200;

    private int enemyW;
    private int enemyH;

    private int bulletW = 4;
    private int bulletH = 8;
    private int bulletSpeed = 500;

    private TextSprite scoreSprite;
    private int score;

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

        bm_2x = Bitmap.createBitmap(ScreenInfo.width, ScreenInfo.height * 4, Bitmap.Config.ARGB_8888);
        Canvas c_bg = new Canvas(bm_2x);
        Rect dst = new Rect(0, 0, ScreenInfo.width, ScreenInfo.height * 2);
        c_bg.drawBitmap(bm_space, null, dst, null);
        dst.set(0, ScreenInfo.height * 2, ScreenInfo.width, ScreenInfo.height * 4);
        c_bg.drawBitmap(bm_space, null, dst, null);

        airplane = new BaseSprite(bm_airplane, ScreenInfo.center.x - ScreenInfo.dp2px(planeSize) / 2, ScreenInfo.height - ScreenInfo.dp2px(planeSize) - 20, planeSize, planeSize);
        airplane.convertToDpSize();
        airplane.setName("airplane");

        bm_enemy = texture.rotate(bm_airplane, 180);

        addSprite(airplane);

        bulletTimer = new Timer();
        enemyTimer = new Timer();

        enemyW = enemyH = planeSize / 2;

        scoreSprite = new TextSprite("", ScreenInfo.width - ScreenInfo.dp2px(200), 20, 20, Color.WHITE);
        addSprite(scoreSprite);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bm_2x, draw_bg_rect, ScreenInfo.screenRect, null);
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
        generateBullet();
        //生成敌人
        generateEnemy();

        scoreSprite.setText("Score:" + score);
    }

    private void generateEnemy() {
        if (enemyTimer.getElapseNotReset() > 1000f / enemyPerSecond) {
            MoveAnimation animation = new MoveAnimation(90, enemySpeed);
            BaseSprite enemy = null;
            float enemyX = random.nextFloat() * (ScreenInfo.width - ScreenInfo.dp2px(enemyW));
            float enemyY = 0 - ScreenInfo.dp2px(enemyH) + 2;
            for (BaseSprite s : spriteRecyclePool) {
                if ("enemy".equals(s.getName())) {
                    s.setAlive(true);
                    s.removeAllAnimation();
                    s.setX(enemyX);
                    s.setY(enemyY);
                    enemy = s;
                    spriteRecyclePool.remove(s);
                }
            }

            if (enemy == null) {
                enemy = new BaseSprite(bm_enemy, enemyX, enemyY, enemyW, enemyH);
                enemy.convertToDpSize();
                enemy.setName("enemy");
            }
            enemy.addAnimation(animation);
            addSprite(enemy);
            enemyTimer.reset();
        }
    }

    private void generateBullet() {
        if (bulletTimer.getElapseNotReset() > 1000f / firePerSecond) {
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
                bullet.setBackgroundColor(Color.YELLOW);
                bullet.convertToDpSize();
                bullet.setName("bullet");
            }
            bullet.addAnimation(animation);
            addSprite(bullet);
            bulletTimer.reset();
        }
    }

    @Override
    public void collided(BaseSprite a, BaseSprite b) {
        if ("enemy".equals(a.getName())) {
            if ("bullet".equals(b.getName())) {
                a.setAlive(false);
                b.setAlive(false);
                score += 100;
            } else if ("airplane".equals(b.getName())) {
                gameOver();
            }
        }
        if ("enemy".equals(b.getName())) {
            if ("bullet".equals(a.getName())) {
                b.setAlive(false);
                a.setAlive(false);
                score += 100;
            } else if ("airplane".equals(a.getName())) {
                gameOver();
            }
        }
    }

    @Override
    public void touch(MotionEvent event) {
        airplane.setX(event.getX() - airplane.getW() / 2);
        airplane.setY(event.getY() - airplane.getH() / 2);
    }

    @Override
    public void release() {
        if (bm_space != null)
            bm_space.recycle();
        if (bm_airplane != null)
            bm_airplane.recycle();
        if (bm_2x != null)
            bm_2x.recycle();
        if (bm_enemy != null)
            bm_enemy.recycle();
    }

    private void gameOver() {
        pause();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setMessage("Game Over!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
                dialog.show();
            }
        });
    }
}
