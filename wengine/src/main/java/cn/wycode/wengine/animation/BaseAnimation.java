package cn.wycode.wengine.animation;


import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.utils.Timer;

/**
 * Created by wy
 * on 2016/12/30.
 */

public abstract class BaseAnimation {
    protected final String TAG = getClass().getSimpleName();

    protected Timer animTimer;
    protected BaseSprite sprite;
    private boolean isDone;
    protected boolean isLoop;

    private Object tag;

    private AnimationStateListener listener;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    BaseAnimation() {
        animTimer = new Timer();
    }

    public abstract void adjustChanges(BaseSprite sprite);

    public void resetTimer(){
        animTimer.reset();
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
        if (listener != null) {
            if (isDone) {
                listener.done();
            } else {
                listener.start();
            }
        }
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public void setStateListener(AnimationStateListener listener) {
        this.listener = listener;
    }

    public interface AnimationStateListener {
        void start();

        void done();
    }
}
