package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;

import java.util.List;

import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureData;
import wang.fly.com.yunhealth.R;

/**
 * Created by 82661 on 2016/11/13.
 */

public class FoldLineView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private static final String TAG = "FoldLineView";
    /** surface持有者 */
    private SurfaceHolder mHolder;
    /** 当前画布 */
    private Canvas mCanvas;
    /** 是否开始绘画 */
    private boolean mIsDrawing = false;
    /** 最后一次点击的x坐标 */
    private int lastX;
    /** 偏移量，用来实现平滑移动 */
    private int mOffset = 0;
    /** 移动速度，用来实现速度递减 */
    private int mSpeed = 0;
    /** 是否触摸屏幕 */
    private boolean mIsTouch = false;
    /** 时间计数器，用来快速滚动时候减速 */
    private int time = 0;
    /** 移动时候X方向上的速度 */
    private double xVelocity = 0;
    /** 是否可以滚动，当不在范围时候不可以滚动 */
    private boolean isScroll = true;
    /** 每个x轴刻度的宽度 */
    private int mXScaleWidth;
    /** 每个y轴刻度的宽度 */
    private int mYScaleWidth;
    /** 外部的list，用来存放折线上的值 */
    private List<MeasureData> mLines;
    /** x轴上的格子数量 */
    private int mXScaleNum = 6;
    /** y轴上的格子数量 */
    private int mYScaleNum = 9;
    /**
     * 左、上、右、下四个内边距
     */
    private Thread drawThread;
    private int offsetMax;
    private int offsetMin = 0;
    private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom;
    private int mWidth;
    private int mHeight;
    private int xSize;
    private float yStart, yEnd;
    private float gridX, gridY;
    private Paint backPaint;
    private Paint linePaint;
    private TextPaint textPaint;
    private int backLineColor = Color.LTGRAY;
    private int lineColor = Color.BLACK;
    private int labelColor = Color.LTGRAY;
    private int labelTextSize = 30;
    private int maxColor = Color.BLACK;
    private int minColor = Color.BLACK;
    private int averageColor = Color.BLACK;
    public onScrollChartListener onScrollChartListener;
    public interface onScrollChartListener{
        public void onScroll(float average, float max, float min, int start, int end);
    }

    public FoldLineView.onScrollChartListener getOnScrollChartListener() {
        return onScrollChartListener;
    }

    public void setOnScrollChartListener(FoldLineView.onScrollChartListener onScrollChartListener) {
        this.onScrollChartListener = onScrollChartListener;
    }

    /**
     * 构造方法区
     * @param context
     */
    public FoldLineView(Context context) {
        super(context);
        init();
    }

    public FoldLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(attrs);
        init();
    }

    private void resolveAttrs(AttributeSet attrs) {
        TypedArray type = getContext().obtainStyledAttributes(attrs, R.styleable.FoldLineView);
        backLineColor = type.getColor(R.styleable.FoldLineView_backLineColor, Color.LTGRAY);
        lineColor = type.getColor(R.styleable.FoldLineView_lineColor, Color.BLACK);
        labelColor = type.getColor(R.styleable.FoldLineView_labelColor, Color.LTGRAY);
        labelTextSize = type.getDimensionPixelSize(R.styleable.FoldLineView_labelSize, 30);
        maxColor = type.getColor(R.styleable.FoldLineView_maxColor, Color.BLACK);
        minColor = type.getColor(R.styleable.FoldLineView_minColor, Color.BLACK);
        averageColor = type.getColor(R.styleable.FoldLineView_averageColor, Color.BLACK);
        type.recycle();
    }

    public FoldLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(attrs);
        init();
    }

    /**
     * 对view进行初始化
     */
    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setColor(backLineColor);
        linePaint.setColor(lineColor);
        textPaint.setColor(labelColor);
        textPaint.setTextSize(labelTextSize);
        this.setxSize(90);
        this.setYRange(-300, 300);
    }

    /**
     * 在surface被创建时
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new Thread(this);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获取大小
         */
        getMeasure();
    }


    public void getMeasure(){
        mWidth = getWidth();
        mHeight = getHeight();
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        mPaddingRight = getPaddingRight();
        mXScaleWidth = (mWidth - mPaddingLeft - mPaddingRight) / mXScaleNum;
        mYScaleWidth = (mHeight - mPaddingTop - mPaddingBottom) / mYScaleNum;
    }

    @Override
    public void run() {
        while (mIsDrawing){
            //进行画图
            // 绘制方法
            getMeasure();
            setSpeedCut();
            setOffsetRange();
            drawing();
        }
    }

    public boolean startDrawing(){
        if (mLines == null || mLines.size() == 0){
            return false;
        }
        mIsDrawing = true;
        if (drawThread == null){
            drawThread = new Thread(this);
        }
        return true;
    }

    public void stopDrawing(){
        mIsDrawing = false;
    }

    private void drawing() {
        try {
            long start = System.currentTimeMillis();
            // 获取并锁定画布
            mCanvas = mHolder.lockCanvas();
            // 绘制坐标轴
            drawAxis();
//            // 绘制曲线
            drawLine();
            long end = System.currentTimeMillis();
            if (end - start < 50) {
                Thread.sleep(50 - (end - start));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                // 保证每次都将绘制的内容提交到服务器
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    private void drawLine() {
        //判定每一条线
        MeasureData now;
        float number = 0;
        int max = 0;
        int min = 0;
        float sum = 0;
        int start = 0;
        int end = 0;
        if (mLines.size() > 0){
            boolean []flag = {false, false, false};
            max = 0;
            min = 0;
            sum = 0;
            number = 0;
            start = end = 0;
            for (int i = mLines.size() - 1; i >= 0; i--) {
                now = mLines.get(i);
                int temp = computeX(i);
                if (checkIsInChart(temp)){
                    //该点在折线内
                    //计算平均值等
                    number ++;
                    sum += now.getAverageData();
                    if (!flag[2]){
                        end = i;
                        flag[2] = true;
                    }
                    start = i;
                    if (!flag[0]){
                        max = i;
                        flag[0] = true;
                    }else if (mLines.get(max).getAverageData() < now.getAverageData()){
                        max = i;
                    }
                    if (!flag[1]){
                        min = i;
                        flag[1] = true;
                    }else if (mLines.get(min).getAverageData() > now.getAverageData()){
                        min = i;
                    }
                    mCanvas.drawLine(temp, mPaddingTop,
                            temp, mHeight - mPaddingBottom,
                            backPaint);
                    mCanvas.drawText(now.getDate(), temp, mHeight - mPaddingBottom/ 4, textPaint);
                    backPaint.setColor(averageColor);
                    backPaint.setStrokeWidth(3f);
                    mCanvas.drawCircle(temp, computeY((int) now.getAverageData()), 5, backPaint);
                    /**
                     * 尝试添加最大值，最小值的曲线
                     */
                    backPaint.setColor(maxColor);
                    mCanvas.drawCircle(temp, computeY((int) now.getMaxData()), 5, backPaint);
                    backPaint.setColor(minColor);
                    mCanvas.drawCircle(temp, computeY((int) now.getMinData()), 5, backPaint);
                    backPaint.setColor(backLineColor);
                    backPaint.setStrokeWidth(1f);
                    linePaint.setStrokeWidth(2f);
                    if (i != 0 && checkIsInChart(computeX(i - 1))){
                        //上一个点在折线图内,且存在上一个点
                        linePaint.setColor(averageColor);
                        mCanvas.drawLine(computeX(i - 1),
                                computeY((int) mLines.get(i - 1).getAverageData()),
                                temp,
                                computeY((int) now.getAverageData()),
                                linePaint
                        );
                        linePaint.setColor(maxColor);
                        mCanvas.drawLine(computeX(i - 1),
                                computeY((int) mLines.get(i - 1).getMaxData()),
                                temp,
                                computeY((int) now.getMaxData()),
                                linePaint
                        );
                        linePaint.setColor(minColor);
                        mCanvas.drawLine(computeX(i - 1),
                                computeY((int) mLines.get(i - 1).getMinData()),
                                temp,
                                computeY((int) now.getMinData()),
                                linePaint
                        );
                        linePaint.setColor(lineColor);
                    }
                }
            }
        }
        //进行值传递
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStrokeWidth(4f);
//        paint.setColor(maxColor);
//        mCanvas.drawCircle(computeX(max), computeY((int) mLines.get(max).getAverageData()), 5, paint);
//        paint.setColor(minColor);
//        mCanvas.drawCircle(computeX(min), computeY((int) mLines.get(min).getAverageData()), 5, paint);
//        paint.setColor(averageColor);
//        mCanvas.drawLine(computeX(start), computeY((int) (sum / number)),
//                computeX(end), computeY((int) (sum / number)),
//                paint);
        onScrollChartListener.onScroll((sum / number),
                mLines.get(max).getAverageData(),
                mLines.get(min).getAverageData(),
                start, end);
    }

    private int computeY(int data){
        return (int) (mHeight - mPaddingBottom - mYScaleWidth * ((data - yStart) / gridY));
    }
    /**
     * 计算每一个x的坐标
     * @param i
     * @return
     */
    private int computeX(int i){

        return (i - mLines.size()) * mXScaleWidth + mOffset + mWidth - mPaddingRight;

    }

    /**
     * 判断当前坐标点是否在折线图内
     * @param x
     * @return
     */
    private boolean checkIsInChart(int x){
        if (x > mPaddingLeft && x < mWidth - mPaddingRight){
            return true;
        }
        return false;
    }

    private void drawAxis() {
        // 设置画布背景为白色
        mCanvas.drawColor(Color.WHITE);
        //开始画边界
        backPaint.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 1));
        backPaint.setStrokeWidth(1f);
        mCanvas.drawLine(mPaddingLeft, mHeight - mPaddingBottom,
                mWidth - mPaddingRight, mHeight - mPaddingBottom, backPaint);
        mCanvas.drawLine(mPaddingLeft, mPaddingTop, mPaddingLeft,
                mHeight - mPaddingBottom, backPaint);
        for (int i = 1; i < mYScaleNum; i++) {
            mCanvas.drawLine(mPaddingLeft, mPaddingTop + i * mYScaleWidth,
                    mWidth - mPaddingRight, mPaddingTop + i * mYScaleWidth, backPaint);
        }
        textPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 1; i <= mYScaleNum; i++) {
            mCanvas.drawText("" + (int)(yEnd - (i - 1) * gridY),
                    mPaddingLeft / 2, mPaddingTop + i * mYScaleWidth, textPaint);
        }
    }

    /**
     * 设置快速滚动时，末尾的减速
     */
    private void setSpeedCut() {
        if (!mIsTouch && isScroll) {
            // 通过当前速度计算所对应的偏移量
            mOffset = mOffset + mSpeed;
        }
        // 每次偏移量的计算
        if (mSpeed != 0) {
            time++;
            mSpeed = (int) (xVelocity + time * time * (xVelocity / 1600.0) - (xVelocity / 20.0) * time);
        } else {
            time = 0;
            mSpeed = 0;
        }

    }
    /** 对偏移量进行边界值判定 */
    private void setOffsetRange() {

        offsetMax = mXScaleWidth * (mLines.size() - 5);
        if (mOffset >= offsetMax) {
            isScroll = false;
            mOffset = offsetMax;
        } else if (mOffset < offsetMin) {// 如果划出最大值范围
            isScroll = false;
            mOffset = offsetMin;
        } else {
            isScroll = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 计算当前速度
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);
        // 计算速度的单位时间
        velocityTracker.computeCurrentVelocity(50);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录触摸点坐标
                lastX = (int) event.getX();
                mIsTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算偏移量
                int offsetX = (int) (event.getX() - lastX);
                // 在当前偏移量的基础上增加偏移量
                mOffset = mOffset + offsetX;
                setOffsetRange();
                // 偏移量修改后下次重绘会有变化
                lastX = (int) event.getX();
                // 获取X方向上的速度
                xVelocity = velocityTracker.getXVelocity();
                mSpeed = (int) xVelocity;
                break;
            case MotionEvent.ACTION_UP:
                mIsTouch = false;
                break;
        }
        // 计算完成后回收内存
        velocityTracker.clear();
        velocityTracker.recycle();
        return true;
    }

    public int getxSize() {
        return xSize;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public float getyStart() {
        return yStart;
    }

    public void setYRange(float yStart, float yEnd) {
        this.yStart = yStart;
        this.yEnd = yEnd;
        this.gridY = (yEnd - yStart) / (mYScaleNum - 1);
    }

    public float getyEnd() {
        return yEnd;
    }

    public List<MeasureData> getLines() {
        return mLines;
    }

    public void setLines(List<MeasureData> lines) {
        mLines = lines;
    }

    /** dp转化为px工具 */
    private int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
