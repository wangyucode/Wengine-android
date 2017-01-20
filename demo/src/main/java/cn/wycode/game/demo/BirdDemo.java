package cn.wycode.game.demo;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import cn.wycode.game.demo.sprite.Pipe;
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

public class BirdDemo extends Wengine implements BaseAnimation.AnimationStateListener {

    private final String TAG = getClass().getSimpleName();

    private BaseSprite bird;
    private Bitmap bm_sky, bm_bird;
    private Bitmap[] bm_runFrames;
    private Rect draw_bg_rect, rect_over, rect_over_dst;
    private int bg_left;
    private Timer bg_timer, pipe_timer;
    private int bg_seed = 100;
    private int jumpSpeed = -200;

    private int pipeSpeed = 150;

    private int birdHeight = 20;

    private JumpAnimation jumpAnimation;

    private int score;

    private boolean isJumping;

    private TextSprite scoreSprite;


    @Override
    public void init() {
        super.init();
        setShowFps(true);
        jumpSpeed = (int) ScreenInfo.dp2px(jumpSpeed);
        bg_seed = (int) ScreenInfo.dp2px(bg_seed);
        pipeSpeed = (int) ScreenInfo.dp2px(pipeSpeed);
    }

    @Override
    public void load() {
        bm_sky = texture.loadTexture("img/sky.png");

        draw_bg_rect = new Rect(bg_left, 0, bm_sky.getWidth() / 2, bm_sky.getHeight());
        rect_over = new Rect(0, 0, 0, bm_sky.getHeight());
        rect_over_dst = new Rect(0, 0, 0, ScreenInfo.height);

        bird = new BaseSprite(bm_bird, ScreenInfo.center.x / 3, ScreenInfo.center.y, birdHeight, birdHeight);
        bird.setName("bird");
        bird.convertToDpSize();

        jumpAnimation = new JumpAnimation(jumpSpeed, ScreenInfo.height);
        jumpAnimation.setTag("jump");
        jumpAnimation.setStateListener(this);

        bm_runFrames = new Bitmap[]{texture.loadTexture("img/man1.png"), texture.loadTexture("img/man2.png")};
        FrameAnimation frameAnimation = new FrameAnimation(bm_runFrames, 500);
        bird.addAnimation(frameAnimation);
        addSprite(bird);

        bg_timer = new Timer();


        scoreSprite = new TextSprite("score:" + score, ScreenInfo.center.x, 16, 16, Color.WHITE);
        addSprite(scoreSprite);

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bm_sky, draw_bg_rect, ScreenInfo.screenRect, null);
        if (!rect_over_dst.isEmpty()) {
            canvas.drawBitmap(bm_sky, rect_over, rect_over_dst, null);
        }
    }

    @Override
    public void update() {
        if (pipe_timer == null) {
            pipe_timer = new Timer();
            return;
        }
        long elapsed = bg_timer.getElapse();
        bg_left += elapsed / 1000f * bg_seed;
        if (bg_left > ScreenInfo.width * 2) {
            bg_left = 0;
        }
        int bm_left = (int) (bm_sky.getWidth() / 2f / ScreenInfo.width * bg_left);

        rect_over.set(0, 0, bm_left - bm_sky.getWidth() / 2, draw_bg_rect.bottom);
        rect_over_dst.set(ScreenInfo.width - (bg_left - ScreenInfo.width), 0, ScreenInfo.width, ScreenInfo.height);

        draw_bg_rect.set(bm_left, draw_bg_rect.top, bm_sky.getWidth() / 2 + bm_left, draw_bg_rect.bottom);

        float time = ScreenInfo.width / ScreenInfo.dp2px(pipeSpeed) * 1000;

        if (isJumping) {
            if (pipe_timer.getElapseNotReset() > time + time * random.nextFloat()) {
                Pipe obstacleTop = null;
                BaseSprite obstacleBottom = null;
                for (BaseSprite s : spriteRecyclePool) {
                    if ("obstacleTop".equals(s.getName())) {
                        s.setAlive(true);
                        s.removeAllAnimation();
                        ((Pipe) s).isOver = false; //没有被越过
                        spriteRecyclePool.remove(s);
                        obstacleTop = (Pipe) s;
                        obstacleTop.setX(ScreenInfo.width - 1);
                    } else if ("obstacleBottom".equals(s.getName())) {
                        s.setAlive(true);
                        s.removeAllAnimation();
                        spriteRecyclePool.remove(s);
                        obstacleBottom = s;
                        obstacleBottom.setX(ScreenInfo.width - 1);
                    }
                }
                //无法复活上部管子，new 一个新的上部管子
                if (obstacleTop == null) {
                    obstacleTop = new Pipe(ScreenInfo.width - 1, 0, 40, 80);
                    obstacleTop.convertToDpSize();
                    obstacleTop.setName("obstacleTop");
                }
                //无法复活下部管子，new 一个新的下部管子
                if (obstacleBottom == null) {
                    //下部管子尽量长点,随机位置时就不用管高度了
                    obstacleBottom = new BaseSprite(ScreenInfo.width - 1, 50, 40, ScreenInfo.px2dp(ScreenInfo.height));
                    obstacleBottom.convertToDpSize();
                    obstacleBottom.setName("obstacleBottom");
                }
                //上部管子的高度随机(80~160dp)
                obstacleTop.setH(100 + random.nextInt(100));
                //上下管子的间距，控制了难度
                float gap = ScreenInfo.dp2px(birdHeight * 5);
                float topObsBottom = ScreenInfo.dp2px(obstacleTop.getH());
                //重新设置下部管子的位置(px) = 上部管子最下+间距
                obstacleBottom.setY(topObsBottom + gap);
                //TODO 动画必须每次都new 暂无法重新利用（待优化）
                MoveAnimation moveAnimation1 = new MoveAnimation(180, pipeSpeed);
                MoveAnimation moveAnimation2 = new MoveAnimation(180, pipeSpeed);
                obstacleTop.addAnimation(moveAnimation1);
                obstacleBottom.addAnimation(moveAnimation2);
                addSprite(obstacleTop);
                addSprite(obstacleBottom);
                pipe_timer.reset();
            }
        }
        //分数更新
        for (BaseSprite s : spritePool) {
            if ("obstacleTop".equals(s.getName())) {
                if (!((Pipe) s).isOver && s.getCenterX() < bird.getCenterX()) {
                    score++;
                    ((Pipe) s).isOver = true; //标记为已经过
                }
            }
        }
        scoreSprite.setText("score:" + score);


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
    public void release() {
        if (bm_sky != null)
            bm_sky.recycle();
        if (bm_bird != null)
            bm_bird.recycle();
        if (bm_runFrames != null) {
            for (Bitmap b : bm_runFrames) {
                if (b != null) {
                    b.recycle();
                }
            }
            bm_runFrames = null;
        }
    }

    @Override
    public void start() {
        isJumping = true;
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
