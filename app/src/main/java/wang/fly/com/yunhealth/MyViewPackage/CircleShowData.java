package wang.fly.com.yunhealth.MyViewPackage;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import wang.fly.com.yunhealth.R;

/**
 * Created by 82661 on 2016/11/10.
 */

public class CircleShowData extends View {
    private int level = 1;
    public static final int THINER = 0, NORMAL = 1, FAT = 2, FATER = 3, FATEST = 4;
    private static final String[] content = {"过轻", "正常", "过重", "肥胖", "特胖"};
    private static final int[] colorId = {
            R.color.warning,
            R.color.normal,
            R.color.warning,
            R.color.danger,
            R.color.danger
    };
    private static final int[] startColor = {
            R.color.warningStart,
            R.color.normalStart,
            R.color.warningStart,
            R.color.dangerStart,
            R.color.dangerStart
    };
    private Paint backPaint;
    private TextPaint textPaint;
    private int width;
    private int height;
    private boolean isFirstDraw = true;
    private Context context;
    private static final String TAG = "CircleShowData";

    public CircleShowData(Context context) {
        super(context);
        this.context = context;
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    public CircleShowData(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    public CircleShowData(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFirstDraw){
            width = getWidth();
            height = getHeight();
        }
        //画出背景
        backPaint.setStrokeWidth(10f);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeCap(Paint.Cap.ROUND);
        backPaint.setColor(context.getResources().getColor(colorId[level]));

        RectF rectF = new RectF(width / 10, height / 10, width * 9 / 10, height * 9 / 10);
        SweepGradient s = new SweepGradient(width/ 2, height/ 2,
                context.getResources().getColor(startColor[level]),
                context.getResources().getColor(colorId[level]));
        backPaint.setShader(s);
        canvas.drawArc(rectF, -90, 360, false, backPaint);
        backPaint.setAlpha(100);
        int d = 10;
        rectF.set(width / 10 + d, height / 10 + d, width * 9 / 10 - d, height * 9 / 10 - d);
        canvas.drawArc(rectF, -90, 360, false, backPaint);
        textPaint.setColor(context.getResources().getColor(colorId[level]));
        textPaint.setTextSize(width / 5);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Rect textBound = new Rect();
        textPaint.getTextBounds(content[level], 0, content[level].length(), textBound);
        canvas.drawText(content[level], width / 2,
                height/ 2 + (textBound.height() / 2f), textPaint);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
