package cn.wycode.game.demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.wycode.wengine.Wengine;
import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.utils.ScreenInfo;
import cn.wycode.wengine.utils.Timer;

/**
 * Created by wy
 * on 2017/1/20.
 */

public class SnakeDemo extends Wengine {
    private final String TAG = "SnakeDemo";

    private Bitmap bm_bg, bm_snake;
    private int lineOffset = 8;

    private Rect rect_bg_src;
    private float rectBgLeft, rectBgTop;

    private float degree;

    private float speed = 50;
    private float touchX, touchY, speedX, speedY;

    private Timer timerBg, timerSnake, timerFood;

    private ArrayList<BaseSprite> snakeBody;

    private ArrayList<Food> foods;

    private int snakeSize = 10;
    private int foodSize = 3;

    private boolean isAdd;

    private int[] colors = new int[]{Color.parseColor("#FF4747"), Color.parseColor("#FFEF47"), Color.parseColor("#47FF56"), Color.parseColor("#479AFF"), Color.parseColor("#8A47FF")};

    @Override
    protected void init() {
        super.init();
        setShowFps(true);
    }

    @Override
    public void load() {
        bm_snake = texture.loadTexture("img/ball.png");
        bm_bg = Bitmap.createBitmap(ScreenInfo.width * 3, ScreenInfo.height * 3, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm_bg);
        c.drawColor(Color.parseColor("#eeeeee"));
        Paint linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#cccccc"));
        for (int i = 1; i * lineOffset < bm_bg.getWidth(); i++) {
            c.drawLine(i * lineOffset, 0, i * lineOffset, bm_bg.getHeight(), linePaint);
        }
        for (int i = 1; i * lineOffset < bm_bg.getHeight(); i++) {
            c.drawLine(0, i * lineOffset, bm_bg.getWidth(), i * lineOffset, linePaint);
        }

        rect_bg_src = new Rect(ScreenInfo.width, ScreenInfo.height, ScreenInfo.width * 2, ScreenInfo.height * 2);
        rectBgLeft = rect_bg_src.left;
        rectBgTop = rect_bg_src.top;

        speedX = speed;


        snakeBody = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            BaseSprite sprite = new BaseSprite(bm_snake, ScreenInfo.center.x - snakeSize / 2 * i, ScreenInfo.center.y, snakeSize, snakeSize);
            sprite.setName("snakeBody");
            snakeBody.add(sprite);
            addSprite(sprite);
        }

        foods = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            int x = random.nextInt(bm_bg.getWidth());
            int y = random.nextInt(bm_bg.getHeight());
            int color = colors[random.nextInt(colors.length)];
            foods.add(new Food(x, y, color));
        }

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bm_bg, rect_bg_src, ScreenInfo.screenRect, null);
    }

    @Override
    public void update() {
        if (timerBg == null) {
            timerBg = new Timer();
            timerSnake = new Timer();
            timerFood = new Timer();
            return;
        }
        long elapse = timerBg.getElapse();
        rectBgLeft += speedX * elapse / 1000f;
        rectBgTop += speedY * elapse / 1000f;
        rect_bg_src.set((int) rectBgLeft, (int) rectBgTop, (int) rectBgLeft + ScreenInfo.width, (int) rectBgTop + ScreenInfo.height);

        for (BaseSprite s : snakeBody) {
            s.setX(s.getX() - speedX * elapse / 1000f);
            s.setY(s.getY() - speedY * elapse / 1000f);
        }


        BaseSprite snakeBody = null;
        for (BaseSprite s : spriteRecyclePool) {
            if ("snakeBody".equals(s.getName())) {
                s.setAlive(true);
                s.setX(ScreenInfo.center.x);
                s.setY(ScreenInfo.center.y);
                snakeBody = s;
                spriteRecyclePool.remove(s);
            }
        }
        if (snakeBody == null) {
            snakeBody = new BaseSprite(bm_snake, ScreenInfo.center.x, ScreenInfo.center.y, snakeSize, snakeSize);
            snakeBody.setName("snakeBody");
        }
        this.snakeBody.add(0, snakeBody);
        addSprite(snakeBody);
        if (timerSnake.getElapseNotReset() > 1000) {
            if (!isAdd) {
                BaseSprite last = this.snakeBody.get(this.snakeBody.size() - 1);
                last.setAlive(false);
                this.snakeBody.remove(last);
            }
        }

        for (BaseSprite s : spritePool) {
            if ("food".equals(s.getName())) {
                s.setAlive(false);
                spritePool.remove(s);
                spriteRecyclePool.add(s);
            }
        }

        for (Food f : foods) {
            if (rect_bg_src.contains(f.x, f.y)) {
                BaseSprite food = generateFood(f.x - rect_bg_src.left, f.y - rect_bg_src.top, f.color);
                addSprite(food);
            }
        }

        if (timerFood.elapse(500)) {
            int x = random.nextInt(bm_bg.getWidth());
            int y = random.nextInt(bm_bg.getHeight());
            int color = colors[random.nextInt(colors.length)];
            foods.add(new Food(x, y, color));
        }

    }

    private BaseSprite generateFood(int x, int y, int color) {
        BaseSprite food = null;
        for (BaseSprite s : spriteRecyclePool) {
            if ("food".equals(s.getName())) {
                s.setAlive(true);
                s.setX(x);
                s.setY(y);
                s.setBackgroundColor(color);
                food = s;
                spriteRecyclePool.remove(s);
            }
        }
        if (food == null) {
            food = new BaseSprite(x, y, foodSize, foodSize);
            food.setBackgroundColor(color);
            food.setName("food");
        }
        return food;
    }

    @Override
    public void collided(BaseSprite a, BaseSprite b) {

    }

    @Override
    public void touch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                break;
            default:
                double angle = Math.atan((event.getY() - touchY) / (event.getX() - touchX));

                speedY = (float) (Math.sin(angle) * speed);
                speedX = (float) (Math.cos(angle) * speed);

                if (event.getX() - touchX < 0) {
                    speedX = -speedX;
                    speedY = -speedY;
                }

        }
    }

    @Override
    public void release() {
        if (bm_bg != null)
            bm_bg.recycle();
    }

    class Food {
        int x, y, color;

        public Food(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

}
