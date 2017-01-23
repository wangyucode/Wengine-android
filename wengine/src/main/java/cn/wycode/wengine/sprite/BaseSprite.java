package cn.wycode.wengine.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.concurrent.CopyOnWriteArrayList;

import cn.wycode.wengine.animation.BaseAnimation;
import cn.wycode.wengine.utils.ScreenInfo;


/**
 * Created by wy
 * on 2016/12/30.
 */

public class BaseSprite implements SpriteAction {

    private final String TAG = getClass().getSimpleName();

    private String name;
    private boolean isAlive;
    private boolean collidable = true;
    private Bitmap image;

    private RectF boundRect, collisionRect;
    private float x, y, w, h, centerX, centerY;

    private int backgroundColor = Color.GREEN;
    protected Paint bgPaint;

    private CopyOnWriteArrayList<BaseAnimation> animations;
    private boolean dieWhenOutScreen = true;
    protected boolean isDpSize;

    public BaseSprite(float w, float h) {
        this(null, 0, 0, w, h);
    }

    public BaseSprite(float x, float y, float w, float h) {
        this(null, x, y, w, h);
    }

    public BaseSprite(Bitmap b, float x, float y, float w, float h) {
        this.image = b;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.boundRect = new RectF(x, y, x + w, y + h);
        this.collisionRect = boundRect;

        this.bgPaint = new Paint();
        this.isAlive = true;
        bgPaint.setColor(backgroundColor);
        bgPaint.setStyle(Paint.Style.FILL);

        animations = new CopyOnWriteArrayList<>();
    }

    /**
     * 适配手机屏幕
     * 将精灵的大小改为以dp为单位
     * 坐标位置不变
     */
    public void convertToDpSize() {
        isDpSize = true;
        w = ScreenInfo.dp2px(w);
        h = ScreenInfo.dp2px(h);
        boundRect.set(x, y, x + w, y + h);
    }


    @Override
    public void draw(Canvas c) {
        if (image != null) {
            c.drawBitmap(image, null, boundRect, bgPaint);
        } else {
            c.drawRect(boundRect, bgPaint);
        }
    }

    @Override
    public void animation() {
        for (BaseAnimation a : animations) {
            if (!a.isDone()) {
                a.adjustChanges(this);
            }

        }
    }

    public void addAnimation(BaseAnimation a) {
        a.setDone(false); //call start
        animations.add(a);
    }

    public void removeAllAnimation() {
        animations.clear();
    }

    public BaseAnimation findAnimationByTag(Object tag) {
        for (BaseAnimation a : animations) {
            if (tag.equals(a.getTag())) {
                return a;
            }
        }
        return null;
    }

    public static final int BOUND_LEFT = 1;
    public static final int BOUND_TOP = 2;
    public static final int BOUND_RIGHT = 3;
    public static final int BOUND_BOTTOM = 4;
    public static final int BOUND_NULL = 0;

    public int reachedScreenBound() {
        int bound;

        if (boundRect.left < ScreenInfo.screenRect.left) {
            bound = BOUND_LEFT;
        } else if (boundRect.top < ScreenInfo.screenRect.top) {
            bound = BOUND_TOP;
        } else if (boundRect.right > ScreenInfo.screenRect.right) {
            bound = BOUND_RIGHT;
        } else if (boundRect.bottom > ScreenInfo.screenRect.bottom) {
            bound = BOUND_BOTTOM;
        } else {
            bound = BOUND_NULL;
        }
        return bound;
    }

    public boolean isOutSideScreen() {
        return !RectF.intersects(ScreenInfo.screenRect, boundRect);
    }


    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public float getW() {
        return boundRect.width();
    }

    public void setW(float w) {
        if(isDpSize){
            w = ScreenInfo.dp2px(w);
        }
        boundRect.right = boundRect.left + w;
        this.w = w;
    }

    public float getX() {
        return boundRect.left;
    }

    public void setX(float x) {
        boundRect.left = x;
        boundRect.right = x + w;
    }

    public float getY() {
        return boundRect.top;
    }

    public void setY(float y) {
        boundRect.top = y;
        boundRect.bottom = y + h;
    }

    public float getH() {
        return boundRect.height();
    }

    public void setH(float h) {
        if(isDpSize){
            h = ScreenInfo.dp2px(h);
        }
        boundRect.bottom = boundRect.top + h;
        this.h = h;
    }

    public float getCenterX() {
        return boundRect.centerX();
    }

    public float getCenterY() {
        return boundRect.centerY();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.bgPaint.setColor(backgroundColor);
    }

    public RectF getBoundRect() {
        return boundRect;
    }

    public RectF getCollisionRect() {
        return collisionRect;
    }

    public void setCollisionRect(RectF collisionRect) {
        this.collisionRect = collisionRect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDieWhenOutScreen() {
        return dieWhenOutScreen;
    }

    public void setDieWhenOutScreen(boolean dieWhenOutScreen) {
        this.dieWhenOutScreen = dieWhenOutScreen;
    }
}
