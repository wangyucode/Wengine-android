package cn.wycode.wengine.sprite;

/**
 * Created by wy
 * on 2017/1/4.
 */

public class Collision {

    private BaseSprite a;
    private BaseSprite b;

    public Collision(BaseSprite a,BaseSprite b){
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        Collision sprite = (Collision)obj;
        return (sprite.a.equals(a)&&sprite.b.equals(b))||(sprite.b.equals(a)&&sprite.a.equals(b));
    }

    @Override
    public int hashCode() {
        return a.hashCode()+b.hashCode();
    }
}
