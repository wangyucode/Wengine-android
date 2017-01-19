package cn.wycode.wengine.sprite;

import android.graphics.Canvas;
import android.graphics.Paint;

import cn.wycode.wengine.utils.ScreenInfo;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by wy
 * on 2017/1/19.
 */

public class TextSprite extends BaseSprite {

    private String text;
    private float textSize;
    private Paint textPaint;


    public TextSprite(String text, float x, float y, float textSize, int textColor) {
        super(x, y, textSize, textSize);
        if (text != null) {
            float w = textSize * text.length();
            setW(w);
        }
        this.textSize = textSize;
        this.text = text;
        textPaint = new Paint(ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        setCollidable(false); //默认关闭碰撞
        convertToDpSize(); //默认单位是dp
    }

    @Override
    public void draw(Canvas c) {
        if (text != null)
            c.drawText(text, getX(), getY(), textPaint);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text != null) {
            float w = textSize * text.length();
            setW(w);
        }
        this.text = text;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        if (isDpSize) {
            textSize = ScreenInfo.dp2px(textSize);
        }
        this.textSize = textSize;
    }
}
