package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import wang.fly.com.yunhealth.R;

import static android.R.attr.bitmap;
import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by 82661 on 2016/11/9.
 */

public class CircleImageView extends ImageView {

    private Paint paint;
    private int backgroundColor = Color.WHITE;
    private int deepBackgroundColor = Color.WHITE;
    private static final String TAG = "CircleImageView";
    private boolean isScaled = false;
    private boolean isPng = false;

    public CircleImageView(Context context) {
        super(context);
        paint = new Paint();

    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        backgroundColor = a.getColor(R.styleable.CircleImageView_circleColor, Color.WHITE);
        isScaled = a.getBoolean(R.styleable.CircleImageView_isScale, false);
        a.recycle();
        paint = new Paint();

    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        backgroundColor = a.getColor(R.styleable.CircleImageView_circleColor, Color.WHITE);
        isScaled = a.getBoolean(R.styleable.CircleImageView_isScale, false);
        a.recycle();
        paint = new Paint();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * 绘制圆形图片
     *
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (isScaled){
                bitmap = Bitmap.createScaledBitmap(bitmap, getWidth() / 3, getWidth() / 3, true);
            }
            Bitmap bitmap1 = setBitmapBack(bitmap);
            Bitmap b = getCircleBitmap(bitmap1);
            final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            Rect rectDest = new Rect(0, 0, getWidth(), getHeight());
            paint.reset();
            paint.setAntiAlias(true);
            canvas.drawBitmap(b, rectSrc, rectDest, paint);
        } else {
            super.onDraw(canvas);
        }
    }


    private Bitmap setBitmapBack(Bitmap bitmap){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        //设置背景色
        paint.setColor(backgroundColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        //实现一个遮罩层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    /**
     * 获取圆形图片方法
     *
     * @param bitmap
     * @return Bitmap
     * @author caizhiming
     */
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        //设置背景色
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        //实现一个遮罩层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         PorterDuff.Mode.CLEAR 清除画布上图像
         PorterDuff.Mode.SRC 显示上层图像
         PorterDuff.Mode.DST 显示下层图像
         PorterDuff.Mode.SRC_OVER上下层图像都显示，下层居上显示
         PorterDuff.Mode.DST_OVER 上下层都显示,下层居上显示
         PorterDuff.Mode.SRC_IN 取两层图像交集部分,只显示上层图像
         PorterDuff.Mode.DST_IN 取两层图像交集部分,只显示下层图像
         PorterDuff.Mode.SRC_OUT 取上层图像非交集部分
         PorterDuff.Mode.DST_OUT 取下层图像非交集部分
         PorterDuff.Mode.SRC_ATOP 取下层图像非交集部分与上层图像交集部分
         PorterDuff.Mode.DST_ATOP 取上层图像非交集部分与下层图像交集部分
         PorterDuff.Mode.XOR 取两层图像的非交集部分
         */
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
