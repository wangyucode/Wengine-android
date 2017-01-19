package cn.wycode.wengine.animation;

import cn.wycode.wengine.sprite.BaseSprite;

/**
 * Created by wy
 * on 2016/12/30.
 */

public class MoveAnimation extends BaseAnimation {

    private float startX, startY, toX, toY;

    private float speed;

    private float degree;

    private Type type;

    private enum Type {
        TO_XY_SPEED, DEGREE_SPEED
    }

    public MoveAnimation(int toX, int toY, float speed) {
        super();
        this.toX = toX;
        this.toY = toY;
        this.speed = speed;

        this.type = Type.TO_XY_SPEED;
    }

    public MoveAnimation(float degree, float speed) {
        super();
        this.speed = speed;
        this.degree = degree;

        this.type = Type.DEGREE_SPEED;
    }


    @Override
    public void adjustChanges(BaseSprite sprite) {
        if(isFirstLoop){
            animTimer.reset();
            isFirstLoop = false;
            return;
        }
        this.startX = sprite.getX();
        this.startY = sprite.getY();
        double vX, vY, x, y;
        long dt;
        switch (type) {
            case TO_XY_SPEED:
                double s = Math.sqrt(Math.pow(toX - sprite.getX(), 2) + Math.pow(toY - sprite.getY(), 2));
                double t = s / speed;
                vX = (toX - sprite.getX()) / t;
                vY = (toY - sprite.getY()) / t;
                dt = animTimer.getElapse();
                x = sprite.getX() + vX * dt / 1000;
                y = sprite.getY() + vY * dt / 1000;
                if (toX - startX > 0) {
                    if (x > toX) {
                        x = toX;
                        y = toY;
                        setDone(true);
                    }
                } else {
                    if (x < toX) {
                        x = toX;
                        y = toY;
                        setDone(true);
                    }
                }
                sprite.setX((float) x);
                sprite.setY((float) y);
                break;

            case DEGREE_SPEED:
                vX = Math.cos(Math.toRadians(degree)) * speed;
                vY = Math.sin(Math.toRadians(degree)) * speed;
                dt = animTimer.getElapse();

                x = sprite.getX() + vX * dt / 1000;
                y = sprite.getY() + vY * dt / 1000;
                sprite.setX((float) x);
                sprite.setY((float) y);
                break;
        }

    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }
}
