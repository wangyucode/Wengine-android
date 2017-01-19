package cn.wycode.game.demo.sprite;

import android.graphics.Bitmap;

import cn.wycode.wengine.sprite.BaseSprite;

/**
 * Created by wy
 * on 2017/1/19.
 */

public class Pipe extends BaseSprite {
    public boolean isOver;


    public Pipe(float w, float h) {
        super(w, h);
    }

    public Pipe(float x, float y, float w, float h) {
        super(x, y, w, h);
    }

    public Pipe(Bitmap b, float x, float y, float w, float h) {
        super(b, x, y, w, h);
    }
}
