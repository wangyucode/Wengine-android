package cn.wycode.game.demo;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import cn.wycode.wengine.Wengine;
import cn.wycode.wengine.animation.BaseAnimation;
import cn.wycode.wengine.animation.MoveAnimation;
import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.utils.ScreenInfo;


/**
 * Created by wy
 * on 2016/12/29.
 */

public class BrickDemo extends Wengine {

    private final String TAG = getClass().getSimpleName();

    private BaseSprite ball, bat;

    private float degree = -32;
    private int speed = 150;

    private int lastReach = BaseSprite.BOUND_NULL;

    private int ballSize = 10;
    private float brickHeight = 20;
    private int batWidth = 80;

    private Bitmap bm_ball, bm_brick;

    @Override
    public void init() {
        super.init();
        setShowFps(true);
        setBackgroundColor(Color.parseColor("#4C7C4A"));
        speed = (int) ScreenInfo.dp2px(speed);
    }

    @Override
    public void load() {

        bm_ball = texture.loadTexture("img/ball.png");
        bm_brick = texture.loadTexture("img/brick.png");

        int row = 8;
        int column = 6;
        float gap = 2; //(dp)
        gap = ScreenInfo.dp2px(gap);
        float w = (ScreenInfo.width - ((column + 1) * gap)) / column;
        //根据屏幕宽度修正gap
        gap += (ScreenInfo.width - w * column - gap * (column + 1)) / (column + 1);

        brickHeight = ScreenInfo.dp2px(brickHeight);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                BaseSprite brick = new BaseSprite(bm_brick, gap + (w + gap) * j, gap + (brickHeight + gap) * i, w, brickHeight);
                brick.setName("brick");
                addSprite(brick);
            }
        }

        ball = new BaseSprite(bm_ball, ScreenInfo.center.x - 10, ScreenInfo.height - ScreenInfo.dp2px(40) - ballSize, ballSize, ballSize);
        ball.convertToDpSize();
        ball.setName("ball");
        speed = (int) ScreenInfo.dp2px(speed);
        BaseAnimation a = new MoveAnimation(degree, speed);
        a.setTag("move");
        ball.addAnimation(a);
        ball.setDieWhenOutScreen(false);
        addSprite(ball);

        bat = new BaseSprite(ScreenInfo.center.x - batWidth / 2, ScreenInfo.height - ScreenInfo.dp2px(20), batWidth, 10);
        bat.setBackgroundColor(Color.parseColor("#ff9800"));
        bat.convertToDpSize();
        bat.setName("bat");
        addSprite(bat);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void update() {
        if (lastReach == ball.reachedScreenBound()) {
            return;
        }
        lastReach = ball.reachedScreenBound();
        MoveAnimation a;
        switch (lastReach) {
            case BaseSprite.BOUND_LEFT:
            case BaseSprite.BOUND_RIGHT:
                a = (MoveAnimation) ball.findAnimationByTag("move");
                degree = 180 - degree;
                a.setDegree(degree);
                break;
            case BaseSprite.BOUND_TOP:
                a = (MoveAnimation) ball.findAnimationByTag("move");
                degree = -degree;
                a.setDegree(degree);
                break;
            case BaseSprite.BOUND_BOTTOM:
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

    @Override
    public void collided(BaseSprite a, BaseSprite b) {
        MoveAnimation animation = (MoveAnimation) ball.findAnimationByTag("move");
        if (a.getName().equals("bat") || b.getName().equals("bat")) {
            degree = -degree + (ball.getCenterX() - bat.getCenterX()) / (bat.getW() / 2f) * 30;
            animation.setDegree(degree);
            return;
        }
        if (a.getName().equals("brick") || b.getName().equals("brick")) {
            float xIn = Math.abs(a.getW() - Math.abs(a.getX() - b.getX()));
            float yIn = Math.abs(a.getH() - Math.abs(a.getY() - b.getY()));
            if (xIn > yIn) {
                degree = -degree;
                animation.setDegree(degree);
            } else {
                degree = 180 - degree;
                animation.setDegree(degree);
            }
            if (a.getName().equals("brick")) {
                a.setAlive(false);
            }
            if (b.getName().equals("brick")) {
                b.setAlive(false);
            }
        }

    }

    @Override
    public void touch(MotionEvent event) {
        bat.setX(event.getX() - bat.getW() / 2);
    }

    @Override
    public void release() {
        if (bm_ball != null)
            bm_ball.recycle();
        if (bm_brick != null)
            bm_brick.recycle();
    }
}
