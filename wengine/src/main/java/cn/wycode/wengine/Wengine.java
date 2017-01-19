package cn.wycode.wengine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.wycode.wengine.graphic.Texture;
import cn.wycode.wengine.sprite.BaseSprite;
import cn.wycode.wengine.sprite.Collision;
import cn.wycode.wengine.utils.ScreenInfo;
import cn.wycode.wengine.utils.Timer;


/**
 * 游戏引擎
 *
 * @author wycode
 * @date 2016年12月29日18:18:24
 * <p>
 * 继承此类，复写相应方法
 */
public abstract class Wengine extends Activity implements Runnable, View.OnTouchListener, SurfaceHolder.Callback {

    private final String TAG = "Engine";

    protected Context mContext;
    private SurfaceView mSurfaceView;
    private Canvas mCanvas;
    private static final int LOADING_DELAY = 2000;
    private boolean isGaming;
    private boolean isPause, isUserPause;
    private int backgroundColor = Color.DKGRAY;
    private int logoBgColor = Color.parseColor("#34495E");
    private int logoTextColor = Color.parseColor("#1ABC9C");
    private Paint logoPaint;

    private boolean isShowFps;
    private int maxFps = 60;
    protected Paint redPaint;

    protected Texture texture;

    private HashSet<Collision> collisions;

    protected Random random;
    /**
     * 精灵对象池
     */
    protected CopyOnWriteArrayList<BaseSprite> spritePool;
    /**
     * 可回收精灵对象池
     */
    protected CopyOnWriteArrayList<BaseSprite> spriteRecyclePool;


    /**
     * 在setContentView之后调用
     * 主要加载资源
     */
    public abstract void load();

    /**
     * 在绘制精灵之前调用
     * 主要用于刷新游戏背景
     *
     * @param canvas 游戏画布
     */
    public abstract void draw(Canvas canvas);

    /**
     * 每帧循环时调用
     * 用于刷新精灵属性
     */
    public abstract void update();

    /**
     * 每帧循环时调用
     * 碰撞回调
     */
    public abstract void collided(BaseSprite a, BaseSprite b);

    /**
     * Touch事件回调
     *
     * @param event MotionEvent
     */
    public abstract void touch(MotionEvent event);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();

        mSurfaceView = new SurfaceView(this);
        setContentView(mSurfaceView);

        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.setOnTouchListener(this);
        mSurfaceView.setKeepScreenOn(true);

        load();
    }

    protected void init() {
        mContext = this;
        ScreenInfo.init(this);
        texture = new Texture(this);
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        logoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        logoPaint.setColor(logoTextColor);
        logoPaint.setTextSize(ScreenInfo.dp2px(20));
        spritePool = new CopyOnWriteArrayList<>();
        spriteRecyclePool = new CopyOnWriteArrayList<>();
        collisions = new HashSet<>();
        random = new Random();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        touch(motionEvent);
        return true;
    }

    @Override
    public void run() {
        int frameCount = 0;
        int fps = 0;
        Timer frameTimer = new Timer();

        while (isGaming) {
            if (isPause) continue;
            if (isUserPause) continue;

            long frameStart = System.currentTimeMillis();

            frameCount++;
            if (frameTimer.elapse(1000)) {
                fps = frameCount;
                frameCount = 0;
            }

            update();
            //碰撞检测
            for (int i = 0; i < spritePool.size() - 1; i++) {
                BaseSprite s1 = spritePool.get(i);
                for (int j = i + 1; j < spritePool.size(); j++) {
                    BaseSprite s2 = spritePool.get(j);
                    if (!s1.isCollidable() || !s2.isCollidable()) continue;
                    if (!s1.isAlive() || !s2.isAlive()) {
                        collisions.remove(new Collision(s1, s2));
                        continue;
                    }
                    if (RectF.intersects(s1.getCollisionRect(), s2.getCollisionRect())) {
                        if (collisions.add(new Collision(s1, s2)))
                            collided(s1, s2);
                        break;
                    } else {
                        collisions.remove(new Collision(s1, s2));
                    }
                }
            }
            //刷新界面
            if (beginDraw()) {
                mCanvas.drawColor(backgroundColor);
                draw(mCanvas);

                for (BaseSprite sprite : spritePool) {
                    if (sprite.isDieWhenOutScreen() && sprite.isOutSideScreen()) {
                        sprite.setAlive(false);
                    }
                    if (sprite.isAlive()) {
                        sprite.animation();
//                        Log.v(TAG,"draw sprite:x="+sprite.getX()+",y="+sprite.getY());
                        sprite.draw(mCanvas);
                    } else {
                        spritePool.remove(sprite);
                        sprite.removeAllAnimation();
                        spriteRecyclePool.add(sprite);
                    }
                }

                if (isShowFps) {
                    mCanvas.drawText("FPS:" + fps, 0, redPaint.getTextSize(), redPaint);
                }
                endDraw();
            }

            //锁定最高帧数
            long frameEnd = System.currentTimeMillis();
            long frameTime = frameEnd - frameStart;
            if (frameTime > 1000 / maxFps) {
                try {
                    Thread.sleep(frameTime - 1000 / maxFps);
                } catch (InterruptedException e) {
                    //ignore this
                }
            }
        }
        finish();
    }

    public void pause() {
        isUserPause = true;
    }

    public void resume() {
        isUserPause = false;
    }

    private boolean beginDraw() {
        if (mSurfaceView.getHolder().getSurface().isValid()) {
            mCanvas = mSurfaceView.getHolder().lockCanvas();
            return true;
        }
        return false;
    }

    private void endDraw() {
        mSurfaceView.getHolder().unlockCanvasAndPost(mCanvas);
    }

    public void setShowFps(boolean showFps) {
        isShowFps = showFps;
    }

    public void addSprite(BaseSprite sprite) {
        spritePool.add(sprite);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        Log.d(TAG, "surfaceDestroyed");
        beginDraw();
        mCanvas.drawColor(logoBgColor);
        mCanvas.drawText("Powered by wycode.cn", 10, ScreenInfo.height - 10, logoPaint);
        endDraw();
        final Thread gameThread = new Thread(this);
        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                isGaming = true;
                gameThread.start();
            }
        }, LOADING_DELAY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        Log.d(TAG, "surfaceChanged:width=" + width + ",height=" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.d(TAG, "surfaceDestroyed");
        finish();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
