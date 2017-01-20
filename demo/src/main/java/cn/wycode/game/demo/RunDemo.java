package cn.wycode.game.demo;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import cn.wycode.wengine.Wengine;
import cn.wycode.wengine.animation.BaseAnimation;
import cn.wycode.wengine.animation.FrameAnimation;
import cn.wycode.wengine.animation.JumpAnimation;
import cn.wycode.wengine.animation.MoveAnimation;
import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.sprite.TextSprite;
import cn.wycode.wengine.utils.ScreenInfo;
import cn.wycode.wengine.utils.Timer;

/**
 * Created by wy
 * on 2017/1/4.
 */

public class RunDemo extends Wengine implements BaseAnimation.AnimationStateListener {

    private final String TAG = getClass().getSimpleName();

    private BaseSprite man;
    private Bitmap bm_bg, bm_ground, bm_bg_2x, bm_ground_2x, bm_man;
    private Bitmap[] bm_runFrames;
    private Rect bg_rect, draw_ground_rect, ground_rect, draw_bg_rect;
    private int bg_left, ground_left;
    private Timer bg_timer, obs_timer, coin_timer;
    private int bg_seed = 80;
    private int ground_seed = bg_seed * 2;
    private int jumpSpeed = -200;

    private int manHeight = 120;
    private int manWidth = 40;

    float obsSize = 40;

    private JumpAnimation jumpAnimation;
    private MoveAnimation moveAnimation;

    private int jumpCount;

    private int metre;

    private TextSprite scoreSprite;

    @Override
    public void init() {
        super.init();
        setShowFps(true);
        jumpSpeed = (int) ScreenInfo.dp2px(jumpSpeed);
        bg_seed = (int) ScreenInfo.dp2px(bg_seed);
        ground_seed = (int) ScreenInfo.dp2px(ground_seed);
    }

    @Override
    public void load() {
        bm_bg = texture.loadTexture("img/run_bg.png");
        bm_ground = texture.loadTexture("img/ground.png");
        bm_man = texture.loadTexture("img/man1.png");

        bg_rect = new Rect(0, 0, ScreenInfo.width, ScreenInfo.height * 2 / 3);
        draw_bg_rect = new Rect(bg_left, 0, ScreenInfo.width, ScreenInfo.height * 2 / 3);
        ground_rect = new Rect(0, ScreenInfo.height * 2 / 3, ScreenInfo.width, ScreenInfo.height);
        draw_ground_rect = new Rect(ground_left, 0, ScreenInfo.width, ScreenInfo.height / 3);

        bm_bg_2x = Bitmap.createBitmap(ScreenInfo.width * 4, ScreenInfo.height * 2 / 3, Bitmap.Config.ARGB_8888);
        Canvas c_bg = new Canvas(bm_bg_2x);
        Rect dst = new Rect(0, 0, ScreenInfo.width * 2, ScreenInfo.height * 2 / 3);
        c_bg.drawBitmap(bm_bg, null, dst, null);
        dst.set(ScreenInfo.width * 2, 0, ScreenInfo.width * 4, ScreenInfo.height * 2 / 3);
        c_bg.drawBitmap(bm_bg, null, dst, null);

        bm_ground_2x = Bitmap.createBitmap(ScreenInfo.width * 2, ScreenInfo.height / 3, Bitmap.Config.ARGB_8888);
        Canvas c_ground = new Canvas(bm_ground_2x);
        dst.set(0, 0, ScreenInfo.width, ScreenInfo.height / 3);
        c_ground.drawBitmap(bm_ground, null, dst, null);
        dst.set(ScreenInfo.width, 0, ScreenInfo.width * 2, ScreenInfo.height / 3);
        c_ground.drawBitmap(bm_ground, null, dst, null);

        man = new BaseSprite(bm_man, ScreenInfo.center.x - manWidth, ScreenInfo.height * 2 / 3 - ScreenInfo.dp2px(manHeight), manWidth, manHeight);
        man.convertToDpSize();
        man.setName("man");

        jumpAnimation = new JumpAnimation(jumpSpeed, ScreenInfo.height * 2 / 3);
        jumpAnimation.setTag("jump");
        jumpAnimation.setStateListener(this);

        bm_runFrames = new Bitmap[]{texture.loadTexture("img/man1.png"), texture.loadTexture("img/man2.png")};
        FrameAnimation frameAnimation = new FrameAnimation(bm_runFrames, 200);
        man.addAnimation(frameAnimation);
        addSprite(man);

        obs_timer = new Timer();

        scoreSprite = new TextSprite(metre + "m", ScreenInfo.center.x, 16, 16, Color.WHITE);
        addSprite(scoreSprite);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bm_bg_2x, draw_bg_rect, bg_rect, null);
        canvas.drawBitmap(bm_ground_2x, draw_ground_rect, ground_rect, null);
    }

    @Override
    public void update() {
        if (bg_timer == null) {
            bg_timer = new Timer();
            return;
        }
        long elapsed = bg_timer.getElapse();
        bg_left += elapsed * bg_seed / 1000;
        ground_left += elapsed * ground_seed / 1000;
        if (bg_left > ScreenInfo.width * 3) {
            bg_left = ScreenInfo.width + bg_left % ScreenInfo.width;
        }
        if (ground_left > ScreenInfo.width) {
            ground_left = ground_left % ScreenInfo.width;
        }
        draw_bg_rect.set(bg_left, draw_bg_rect.top, ScreenInfo.width + bg_left, draw_bg_rect.bottom);
        draw_ground_rect.set(ground_left, draw_ground_rect.top, ScreenInfo.width + ground_left, draw_ground_rect.bottom);

        metre = (int) (bg_timer.getElapseNotReset() / 1000f * 2);
        scoreSprite.setText(metre + "m");

        if (obs_timer.getElapseNotReset() > 4000 + random.nextInt(4000)) {
            moveAnimation = new MoveAnimation(180, ground_seed);
            BaseSprite obstacle = null;
            for (BaseSprite s : spriteRecyclePool) {
                if ("obstacle".equals(s.getName())) {
                    s.setAlive(true);
                    s.removeAllAnimation();
                    obstacle = s;
                    obstacle.setX(ScreenInfo.width - 1);
                    spriteRecyclePool.remove(s);
                    break;
                }
            }

            if (obstacle == null) {
                obstacle = new BaseSprite(ScreenInfo.width - 1, ScreenInfo.height * 2 / 3 - ScreenInfo.dp2px(obsSize), obsSize, obsSize);
                obstacle.convertToDpSize();
                obstacle.setName("obstacle");
            }
            obstacle.addAnimation(moveAnimation);
            addSprite(obstacle);
            obs_timer.reset();
        }

    }

    @Override
    public void collided(BaseSprite a, BaseSprite b) {
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

    @Override
    public void touch(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (man.findAnimationByTag("jump") != null) {
                    if (jumpCount < 2) {
                        jumpAnimation.setDone(false);
                        jumpAnimation.setV(jumpSpeed);
                    }
                } else {
                    man.addAnimation(jumpAnimation);
                }
                break;
        }
    }

    @Override
    public void release() {
        if (bm_bg != null)
            bm_bg.recycle();
        if (bm_bg_2x != null)
            bm_bg_2x.recycle();
        if (bm_ground != null)
            bm_ground.recycle();
        if (bm_ground_2x != null)
            bm_ground_2x.recycle();
        if (bm_man != null)
            bm_man.recycle();
        if (bm_bg != null) {
            for (Bitmap b : bm_runFrames) {
                if (b != null)
                    b.recycle();
            }
            bm_runFrames = null;
        }
    }

    @Override
    public void start() {
        jumpCount++;
    }

    @Override
    public void done() {
        jumpCount = 0;
    }
}

