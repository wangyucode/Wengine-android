package cn.wycode.game.demo;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;

import cn.wycode.wengine.Wengine;
import cn.wycode.wengine.animation.BaseAnimation;
import cn.wycode.wengine.animation.FrameAnimation;
import cn.wycode.wengine.animation.JumpAnimation;
import cn.wycode.wengine.animation.MoveAnimation;
import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.utils.ScreenInfo;
import cn.wycode.wengine.utils.Timer;

/**
 * Created by wy
 * on 2017/1/4.
 */

public class BirdDemo extends Wengine implements BaseAnimation.AnimationStateListener {

    private final String TAG = getClass().getSimpleName();

    private BaseSprite bird;
    private Bitmap bm_sky, bm_bird;
    private Rect draw_bg_rect, rect_over, rect_over_dst;
    private int bg_left;
    private Timer bg_timer, pipe_timer;
    private int bg_seed = 100;
    private int jumpSpeed = -200;

    private int pipeSpeed = 100;

    private int birdHeight = 120;

    private JumpAnimation jumpAnimation;
    private MoveAnimation moveAnimation;

    private int score;


    @Override
    public void init() {
        super.init();
        setShowFps(true);
    }

    @Override
    public void load() {
        bm_sky = texture.loadTexture("img/sky.png");

        draw_bg_rect = new Rect(bg_left, 0, bm_sky.getWidth() / 2, bm_sky.getHeight());
        rect_over = new Rect(0, 0, 0, bm_sky.getHeight());
        rect_over_dst = new Rect(0, 0, 0, ScreenInfo.height);

        bird = new BaseSprite(bm_bird, ScreenInfo.center.x, ScreenInfo.center.y, birdHeight, birdHeight);
        bird.setName("bird");

        jumpAnimation = new JumpAnimation(jumpSpeed, ScreenInfo.height);
        jumpAnimation.setTag("jump");
        jumpAnimation.setStateListener(this);

        Bitmap[] runFrames = new Bitmap[]{texture.loadTexture("img/man1.png"), texture.loadTexture("img/man2.png")};
        FrameAnimation frameAnimation = new FrameAnimation(runFrames, 500);
        bird.addAnimation(frameAnimation);
        addSprite(bird);

        bg_timer = new Timer();
        pipe_timer = new Timer();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bm_sky, draw_bg_rect, ScreenInfo.screenRect, null);
        if (!rect_over_dst.isEmpty()) {
            canvas.drawBitmap(bm_sky, rect_over, rect_over_dst, null);
        }

        canvas.drawText("score:" + score, ScreenInfo.center.x, redPaint.getTextSize(), redPaint);
    }

    @Override
    public void update() {
        long elapsed = bg_timer.getElapse();
        bg_left += elapsed / 1000f * bg_seed;
        if (bg_left > ScreenInfo.width * 2) {
            bg_left = 0;
        }
        int bm_left = (int) (bm_sky.getWidth() / 2f / ScreenInfo.width * bg_left);

        rect_over.set(0, 0, bm_left-bm_sky.getWidth()/2, draw_bg_rect.bottom);
        rect_over_dst.set(ScreenInfo.width - (bg_left-ScreenInfo.width), 0, ScreenInfo.width, ScreenInfo.height);

        draw_bg_rect.set(bm_left, draw_bg_rect.top, bm_sky.getWidth() / 2 + bm_left, draw_bg_rect.bottom);


        moveAnimation = new MoveAnimation(180, pipeSpeed);
//        if (pipe_timer.getElapseNotReset() > 2000 + random.nextInt(1000)) {
//            BaseSprite obstacle = null;
//            for (BaseSprite s : spriteRecyclePool) {
//                if ("obstacle".equals(s.getName())) {
//                    s.setAlive(true);
//                    s.removeAllAnimation();
//                    obstacle = s;
//                    obstacle.setX(ScreenInfo.width - 1);
//                    spriteRecyclePool.remove(s);
//                    break;
//                }
//            }
//            if (obstacle == null) {
//                obstacle = new BaseSprite(ScreenInfo.width - 1, ScreenInfo.height * 2 / 3 - 40, 40, 40);
//                obstacle.setName("obstacle");
//            }
//
//            obstacle.addAnimation(moveAnimation);
//            addSprite(obstacle);
//            pipe_timer.reset();
//        }

    }

    @Override
    public void collided(BaseSprite a, BaseSprite b) {
        gameOver();
    }

    @Override
    public void touch(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (bird.findAnimationByTag("jump") != null) {
                    jumpAnimation.setDone(false);
                    jumpAnimation.setV(jumpSpeed);
                } else {
                    bird.addAnimation(jumpAnimation);
                }
                break;
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void done() {
        gameOver();
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
