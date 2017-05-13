package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import wang.fly.com.yunhealth.R;

/**
 * Created by 82661 on 2016/10/29.
 */

public class HeartWavesView extends View {


    private int mTableLineColor = Color.RED;
    private int mWavesLineColor = Color.BLACK;
    private int mTitleColor = Color.BLACK;
    private int mTitleSize = 30;
    private int mXYTextSize = 20;

    private Context context;
    private static final String TAG = "HeartWavesView";

    private Paint paintWavesLine;
    private Paint paintTableLine;
    private TextPaint paintTitle;
    private TextPaint paintXYText;

    private boolean isFirstDrawPoint = true;
    private boolean isFirstDrawBackground = true;
    private int height;
    private int width;
    private int leftPadding;
    private int rightPadding;
    private int topPadding;
    private int bottomPadding;

    private int maxY = 4100;
    private int minY = 0;
    private int maxX = 120;
    private int x_num = 25;
    private int y_num;
    private int grid_width;

    //x轴每个小格子对应的秒
    //y轴每个小格子对应的指数
    private int grid_second = 5;
    private float grid_num;
    private int zeroCurY;
    private int yStartNum;


    private int workWidth;
    private int workHeight;

    //几秒钟一次数据，默认为1秒1次
    private float dataHz = 1;

    private List<PointXY> pointList;

//    private String title = "2分钟心电图";

    private float averageData;
    private float maxData;

    onDataChangedlistener onDataChangedlistener = null;

    //定义接口
    public interface onDataChangedlistener {
        void getMaxData(float max);

        void getAverage(float average);
    }

    public void setOnDataChangedlistener(HeartWavesView.onDataChangedlistener onDataChangedlistener) {
        this.onDataChangedlistener = onDataChangedlistener;
    }

    /**
     * 在代码中动态生成的时候用
     *
     * @param context
     */
    public HeartWavesView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    /**
     * 在布局中使用了自定义属性的时候使用
     *
     * @param context
     * @param attrs
     */
    public HeartWavesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        resolveAttrs(attrs);
        initView();
    }

    private void resolveAttrs(AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.HeartWavesView);
        mTableLineColor = typeArray.getColor(R.styleable.HeartWavesView_tableLineColor, Color.RED);
        mTitleColor = typeArray.getColor(R.styleable.HeartWavesView_titleColor, Color.BLACK);
        mWavesLineColor = typeArray.getColor(R.styleable.HeartWavesView_wavesLineColor, Color.BLACK);
        mTitleSize = typeArray.getDimensionPixelSize(R.styleable.HeartWavesView_titleSize, 30);
        mXYTextSize = typeArray.getDimensionPixelSize(R.styleable.HeartWavesView_xyTextSize, 20);
        typeArray.recycle();
    }

    /**
     * 在使用了自定义style集的时候使用
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public HeartWavesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        resolveAttrs(attrs);
        initView();
    }

    private void initView() {
        //生成抗锯齿的画笔
        pointList = new ArrayList<>();
        paintWavesLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置画笔粗细
        paintWavesLine.setStrokeWidth(2.5f);
        //设置画笔颜色
        paintWavesLine.setColor(mWavesLineColor);

        paintTableLine = new Paint();
        paintTableLine.setColor(mTableLineColor);
        paintTableLine.setAntiAlias(true);
        paintWavesLine.setStrokeWidth(4);

        paintTitle = new TextPaint();
        paintTitle.setTextSize(mTitleSize);
        paintTitle.setColor(mTitleColor);

        paintXYText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paintXYText.setColor(mTitleColor);
        paintXYText.setTextSize(mXYTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFirstDrawBackground) {
            height = getHeight();
            width = getWidth();
            leftPadding = rightPadding = 100;
            topPadding = bottomPadding = 50;
        }
        drawBackground(canvas);
        drawWaves(canvas);
    }

    private void drawWaves(Canvas canvas) {
        PointXY start;
        PointXY end;
        for (int i = 0; i < pointList.size() - 1; i++) {
            start = pointList.get(i);
            end = pointList.get(i + 1);
            canvas.drawLine(start.getX(), start.getY(), end.getX(), end.getY(), paintWavesLine);
        }
    }

    private void drawBackground(Canvas canvas) {
        if (isFirstDrawBackground) {
            isFirstDrawBackground = false;
            x_num = maxX / grid_second;
            x_num = x_num % 5 == 0 ? x_num : (x_num % 5 > 3 ? (x_num / 5 + 1) * 5 : x_num / 5 * 5);
            grid_width = (width - leftPadding - rightPadding) / x_num;
            y_num = (height - topPadding - rightPadding) / grid_width;
            y_num = y_num % 5 == 0 ? y_num : (y_num % 5 > 3 ? (y_num / 5 + 1) * 5 : y_num / 5 * 5);
            //获取工作区的宽和高
            workWidth = grid_width * x_num;
            workHeight = grid_width * y_num;
            //获取xy轴比例尺
            //获得y轴0标识位的位置
            if (maxY > 0 && minY >= 0) {
                yStartNum = maxY;
                grid_num = maxY / y_num;
                zeroCurY = y_num;
            } else if (maxY <= 0 && minY < 0) {
                yStartNum = 0;
                grid_num = -minY / y_num;
                zeroCurY = 0;
            } else {
                zeroCurY = y_num / 2;
                zeroCurY = zeroCurY % 5 == 0 ? zeroCurY :
                        (zeroCurY % 5 > 3) ? (zeroCurY / 5 + 1) * 5 : (zeroCurY / 5 * 5);
                grid_num = Math.max(maxY, minY) / Math.min(y_num - zeroCurY, zeroCurY);
                yStartNum = (int) (zeroCurY * grid_num);
            }
        }
        for (int i = 0; i <= x_num; i++) {
            paintTableLine.setStrokeWidth(1f);
            if (i % 5 == 0) {
                paintTableLine.setStrokeWidth(3f);
                String label = grid_second * i + "";
                canvas.drawText(label,
                        leftPadding + i * grid_width - mXYTextSize / 2,
                        workHeight + bottomPadding / 2 + topPadding + 10,
                        paintXYText);
            }
            canvas.drawLine(leftPadding + i * grid_width, topPadding,
                    leftPadding + i * grid_width, topPadding + workHeight, paintTableLine);
        }
        for (int i = 0; i <= y_num; i++) {
            paintTableLine.setStrokeWidth(1f);
            if (i % 5 == 0) {
                paintTableLine.setStrokeWidth(3f);
                String label = yStartNum - i * grid_num + "";
                canvas.drawText(label, leftPadding / 5, topPadding + i * grid_width, paintXYText);
            }
            canvas.drawLine(leftPadding, topPadding + i * grid_width,
                    leftPadding + workWidth, topPadding + i * grid_width, paintTableLine);
        }
//        canvas.drawText(title, width / 2 - mTitleSize * title.length() / 2,
//                topPadding / 2, paintTitle);
    }

    public void drawNextPoint(float y) {
        //获取数据的平均值和最大值
        if (y >= maxData) {
            maxData = y;
            if (this.onDataChangedlistener != null) {
                this.onDataChangedlistener.getMaxData(maxData);
            }
        }
        averageData = (pointList.size() * averageData + y) / (pointList.size() + 1);

        if (!isFirstDrawBackground) {
            if (isFirstDrawPoint) {
                isFirstDrawPoint = false;
                PointXY point = new PointXY();
                point.setX(leftPadding);
                point.setY(zeroCurY * grid_width + topPadding);
                pointList.add(point);
            }
            PointXY lastPoint = pointList.get(pointList.size() - 1);
            Log.d(TAG, "drawNextPoint: size" + pointList.size());
            if (pointList.size() == maxX - 1) {
                if (this.onDataChangedlistener != null) {
                    this.onDataChangedlistener.getAverage(averageData);
                }
                pointList.clear();
                lastPoint.setX(leftPadding);
            }
            PointXY nowPoint = new PointXY();
            nowPoint.setX(dataHz / grid_second * grid_width + lastPoint.getX());
            nowPoint.setY((yStartNum - y) / grid_num * grid_width + topPadding);
            pointList.add(nowPoint);
            invalidate();

        }

    }

    public Paint getPaintWavesLine() {
        return paintWavesLine;
    }

    public void setPaintWavesLine(Paint paintWavesLine) {
        this.paintWavesLine = paintWavesLine;
    }

    public Paint getPaintTableLine() {
        return paintTableLine;
    }

    public void setPaintTableLine(Paint paintTableLine) {
        this.paintTableLine = paintTableLine;
    }

    public TextPaint getPaintTitle() {
        return paintTitle;
    }

    public void setPaintTitle(TextPaint paintTitle) {
        this.paintTitle = paintTitle;
    }

    public TextPaint getPaintXYText() {
        return paintXYText;
    }

    public void setPaintXYText(TextPaint paintXYText) {
        this.paintXYText = paintXYText;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }


    public class PointXY {
        private float x;
        private float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
