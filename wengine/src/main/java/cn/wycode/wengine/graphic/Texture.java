package cn.wycode.wengine.graphic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wy
 * on 2017/1/4.
 */

public class Texture {

    private Context mContext;
    public BitmapFactory.Options options;

    public Texture(Context context) {
        mContext = context;
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }


    public Bitmap loadTexture(String file) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = mContext.getAssets().open(file);
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return bitmap;
    }

    public Bitmap loadTexture(String file,int x,int y,int w,int h) {
        Bitmap temp = loadTexture(file);
        return Bitmap.createBitmap(temp,x,y,w,h);
    }

    public Bitmap rotate(Bitmap src,float angle){
        Matrix m = new Matrix();
        m.setRotate(angle);
        return Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),m,true);
    }
}
