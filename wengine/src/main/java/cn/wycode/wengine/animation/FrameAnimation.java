package cn.wycode.wengine.animation;

import android.graphics.Bitmap;

import cn.wycode.wengine.sprite.BaseSprite;

/**
 * Created by wy
 * on 2017/1/5.
 */

public class FrameAnimation extends BaseAnimation {

    private Bitmap[] frames;
    private int duration;
    int i = 0;

    public FrameAnimation(Bitmap[] frames, int duration) {
        this.frames = frames;
        this.duration = duration;
    }

    @Override
    public void adjustChanges(BaseSprite sprite) {
        if (animTimer.elapse(duration)) {
            sprite.setImage(frames[i % frames.length]);
        }
        i++;
    }
}
