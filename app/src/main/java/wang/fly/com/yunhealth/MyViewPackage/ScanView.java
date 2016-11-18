package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AnimatorRes;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

import wang.fly.com.yunhealth.R;

import static android.R.attr.end;
import static android.R.attr.left;
import static android.R.attr.logoDescription;

/**
 * Created by 82661 on 2016/11/6.
 */

public class ScanView extends View{

    private int defaultColor;
    private int textSize;
    private int textColor;
    private TextPaint drawText;
    private Paint circlePaint;
    private float workLeft, workRight, workTop, workBottom;
    private double workSize;
    private int []colorArray = new int[5];
    private int height;
    private int width;
    private boolean isFirstDrawBackground = true;
    private boolean isScaning = false;
    private String scanTitle = "正在扫描：";
    private int score = 100;
    private int startAngle = 0;
    private int endAngle = 360;
    private int angleSize = 1;

    private static final String TAG = "ScanView";

    public ScanView(Context context) {
        super(context);
        init(null);
    }

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs){
        defaultColor = getResources().getColor(R.color.lightseagreen);
        Arrays.fill(colorArray, defaultColor);
        textColor = Color.WHITE;
        textSize = 20;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScanView);
            colorArray[0] = a.getColor(R.styleable.ScanView_level1Color, defaultColor);
            colorArray[1] = a.getColor(R.styleable.ScanView_level2Color, defaultColor);
            colorArray[2] = a.getColor(R.styleable.ScanView_level3Color, defaultColor);
            colorArray[3] = a.getColor(R.styleable.ScanView_level4Color, defaultColor);
            colorArray[4] = a.getColor(R.styleable.ScanView_level5Color, defaultColor);
            textColor = a.getColor(R.styleable.ScanView_textColor, Color.WHITE);
            textSize = a.getDimensionPixelSize(R.styleable.ScanView_textSize, 100);
            a.recycle();
        }

        drawText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        drawText.setColor(textColor);


        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.WHITE);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isFirstDrawBackground){
            width = getWidth();
            height = getHeight();
            workSize = 2 / 3.00 * (height >  width ? width : height);
            workLeft = (float) ((width - workSize) / 2);
            workTop = (float) ((height - workSize) / 2);
            workRight = (float) (workLeft + workSize);
            workBottom = (float) (workTop + workSize);
            Log.d(TAG, "onDraw: left" + workLeft);
            Log.d(TAG, "onDraw: top" + workTop);
            Log.d(TAG, "onDraw: right" + workRight);
            Log.d(TAG, "onDraw: bottom" + workBottom);
            isFirstDrawBackground = false;
        }

        drawBackground(canvas);
        drawText(canvas);
        if(isScaning){
            Log.d(TAG, "onDraw: drawTitle");
            drawText.setTextAlign(Paint.Align.LEFT);
            drawText.setTextSize(60);
            canvas.drawText(scanTitle, 15, height - 30, drawText);
        }
        invalidate();
    }


    private void drawBackground(Canvas canvas) {
        if(score >= 90){
            canvas.drawColor(colorArray[0]);
        }else if(score >= 80){
            canvas.drawColor(colorArray[1]);
        }else if(score >= 60){
            canvas.drawColor(colorArray[2]);
        }else if(score >= 30){
            canvas.drawColor(colorArray[3]);
        }else{
            canvas.drawColor(colorArray[4]);
        }
        int d = 10;
        RectF rect = new RectF(workLeft + d, workTop + d, workRight - d, workBottom - d);
        circlePaint.setAlpha(255);
        circlePaint.setStrokeWidth(5f);
        canvas.drawArc(rect, 0, 360, false, circlePaint);
        d = -15;
        rect.set(workLeft + d, workTop + d, workRight - d, workBottom - d);
        circlePaint.setStrokeWidth(50f);
        circlePaint.setAlpha(100);
        canvas.drawArc(rect, 0, 360, false, circlePaint);

    }

    private void drawText(Canvas canvas) {
        drawText.setTextSize(textSize);
        drawText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(score + "", width / 2,
                height / 2 + getStringHeight(score + "", drawText)/ 4, drawText);
        RectF rect = new RectF(workLeft, workTop, workRight, workBottom);
        circlePaint.setAlpha(75);
        circlePaint.setStrokeWidth(25f);
        startAngle += angleSize;
        startAngle %= 360;
        endAngle -= angleSize;
        endAngle %= 360;
        canvas.drawArc(rect, startAngle, 150, false, circlePaint);
        canvas.drawArc(rect, endAngle, 150, false, circlePaint);
    }

    public void setScore(int score) {
        this.score = score;
        invalidate();
    }

    public void startScan(){
        angleSize = 5;
        isScaning = true;
        invalidate();
    }

    public void stopScan(){
        isScaning = false;
        angleSize = 1;
        invalidate();
    }

    public void setScanTitle(String scanTitle) {
        this.scanTitle = scanTitle;
        invalidate();
    }

    public int getScore() {
        return score;
    }


    private int getStringHeight(String str, Paint paint) {
        Paint.FontMetrics fr = paint.getFontMetrics();
        return (int) Math.ceil(fr.descent - fr.top) + 2;  //ceil() 函数向上舍入为最接近的整数。
    }
}
