package cn.wycode.wengine.animation;

import cn.wycode.wengine.sprite.BaseSprite;

/**
 * Created by wy
 * on 2017/1/5.
 */

public class JumpAnimation extends BaseAnimation {
    private float g = 300;
    private float v;
    private float groundY;


    public JumpAnimation(float v0, float groundY) {
        super();
        this.v = v0;
        this.groundY = groundY;
    }

    @Override
    public void adjustChanges(BaseSprite sprite) {
        if(isFirstLoop){
            animTimer.reset();
            isFirstLoop = false;
            return;
        }
        float t = animTimer.getElapse() / 1000f;
        v += g * t;
        float y = sprite.getY() + v * t;
        if (y > groundY - sprite.getH()) {
            y = groundY - sprite.getH();
            setDone(true);
        }
        sprite.setY(y);
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public float getG() {
        return g;
    }

    public float getGroundY() {
        return groundY;
    }

    public void setGroundY(float groundY) {
        this.groundY = groundY;
    }
}
