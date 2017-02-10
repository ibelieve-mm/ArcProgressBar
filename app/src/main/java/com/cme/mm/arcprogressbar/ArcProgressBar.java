package com.cme.mm.arcprogressbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Descriptions：
 * <p>
 * Author：ChenME
 * Date：2017/2/8
 * Email：ibelieve1210@163.com
 */
public class ArcProgressBar extends View {

    /**
     * 背景的画笔
     */
    private Paint backgroundPaint;

    /**
     * 文字的画笔
     */
    private Paint textPaintLeft;
    private Paint textPaintRight;
    private Paint textPaintCenter;
    private Paint textPaintCenterSmall;

    /**
     * 进度的画笔
     */
    private Paint shaderPaint;

    /**
     * 控件宽度
     */
    private int width;

    /**
     * 当前扫过的角度
     */
    private float currentSweepAngle = 0;

    /**
     * 当前进度
     */
    private float sendCurrentProgress;

    /**
     * 最大进度
     */
    private float maxProgress = 100;

    /**
     * 两端文字的大小
     */
    private float betweenTextSize;

    /**
     * 中间文字的大小
     */
    private float centerTextSize;

    /**
     * 中间文字的大小
     */
    private float centerSmallTextSize;

    /**
     * 控件左右的边距
     */
    private float canvasMarginSize;

    /**
     * 进度条的高度
     */
    private float arcProgressHeight;

    /**
     * 中间最后一行文字距弧形进度条底边的距离
     */
    private float centerTextBottomMargin;

    /**
     * 中间两行字的间距
     */
    private float centerTextSpace;

    /**
     * 总的扫过的角度
     */
    private float sweepAngle;

    /**
     * 背景的底色
     */
    private int arcBackgroundColor;

    /**
     * 圆弧的宽度
     */
    private float arcWidth;

    /**
     * 边距
     */
    private float padding;

    /**
     * 圆弧结束位置的坐标
     */
    private float endAngel = -55;

    /**
     * 时刻变化的Angel
     */
    private float mAngel;

    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    private int defaultDuration = 2 * 1000;

    // 用于控制动画状态转换
    private Handler mAnimatorHandler;
    //过程动画
    private ValueAnimator mValueAnimator;

    public ArcProgressBar(Context context) {
        super(context);
        initView(context);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        arcBackgroundColor = context.getResources().getColor(R.color.color_arcBackground);
        betweenTextSize = context.getResources().getDimensionPixelOffset(R.dimen.size_betweenText);
        centerTextSize = context.getResources().getDimensionPixelOffset(R.dimen.size_centerText);
        centerSmallTextSize = context.getResources().getDimensionPixelOffset(R.dimen.size_centerSmallText);
        canvasMarginSize = context.getResources().getDimensionPixelOffset(R.dimen.size_canvasMargin);
        arcProgressHeight = context.getResources().getDimensionPixelOffset(R.dimen.size_arcProgressHeight);
        centerTextBottomMargin = context.getResources().getDimensionPixelOffset(R.dimen.size_centerTextBottomMargin);
        centerTextSpace = context.getResources().getDimensionPixelOffset(R.dimen.size_centerTextSpace);
        arcWidth = context.getResources().getDimensionPixelOffset(R.dimen.size_progressWidth);
        padding = context.getResources().getDimensionPixelOffset(R.dimen.size_progressTopPadding);
        sweepAngle = 180 + 2 * endAngel;

        backgroundPaint = new Paint();

        textPaintCenter = new Paint();
        textPaintCenter.setAntiAlias(true);
        textPaintCenter.setTextSize(centerTextSize);
        textPaintCenter.setColor(Color.RED);
        textPaintCenter.setTextAlign(Paint.Align.CENTER);

        textPaintCenterSmall = new Paint();
        textPaintCenterSmall.setAntiAlias(true);
        textPaintCenterSmall.setTextSize(centerSmallTextSize);
        textPaintCenterSmall.setColor(Color.RED);
        textPaintCenterSmall.setTextAlign(Paint.Align.CENTER);

        textPaintLeft = new Paint();
        textPaintLeft.setAntiAlias(true);
        textPaintLeft.setTextSize(betweenTextSize);
        textPaintLeft.setColor(Color.RED);
        textPaintLeft.setTextAlign(Paint.Align.LEFT);

        textPaintRight = new Paint();
        textPaintRight.setAntiAlias(true);
        textPaintRight.setTextSize(betweenTextSize);
        textPaintRight.setColor(Color.RED);
        textPaintRight.setTextAlign(Paint.Align.RIGHT);

        shaderPaint = new Paint();

        //初始化画笔
        backgroundPaint.setColor(arcBackgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE); //设置空心
        backgroundPaint.setStrokeWidth(arcWidth);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);//设置为圆角
        backgroundPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth(); //获取宽度

        RectF rectF = new RectF(
                padding + (arcWidth / 2),//left
                padding + (arcWidth / 2),//top
                width - (padding + (arcWidth / 2)), //right
                width - (padding + (arcWidth / 2)));//bottom


        paintPercentBack(canvas, rectF);
        drawInsideArc(canvas, rectF);
        paintText(canvas);
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void paintText(Canvas canvas) {
        canvas.drawText("0", padding - canvasMarginSize,
                getFontHeight(textPaintRight) + arcProgressHeight, textPaintLeft);

        canvas.drawText(((int) maxProgress) + "", width - (padding - canvasMarginSize),
                getFontHeight(textPaintRight) + arcProgressHeight, textPaintRight);

        canvas.drawText(((int) sendCurrentProgress) + "", width / 2,
                getFontHeight(textPaintRight) + arcProgressHeight - getFontHeight(textPaintCenterSmall) - centerTextBottomMargin - centerTextSpace, textPaintCenter);


        float percent = sendCurrentProgress / maxProgress * 100;
        String textResult = "已完成 " + dropNeedlessZeroOfNum(percent + "") + "%";
        if (percent >= 100) {
            textResult = "当月任务已完成";
        }

        canvas.drawText(textResult, width / 2,
                getFontHeight(textPaintCenterSmall) + arcProgressHeight - centerTextBottomMargin, textPaintCenterSmall);
    }

    /**
     * 删除float字符串中的多余0
     *
     * @param num 将要被格式化的数字字符串
     * @return
     */
    private String dropNeedlessZeroOfNum(String num) {
        if (num.indexOf(".") > 0) {
            //正则表达
            num = num.replaceAll("0+?$", "");//去掉后面无用的零
            num = num.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return num;
    }

    /**
     * 获取两端字体的高度
     *
     * @return
     */
    private int getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) (fm.descent - fm.ascent);
    }

    /**
     * 绘制圆弧的背景
     *
     * @param canvas
     */
    private void paintPercentBack(Canvas canvas, RectF rectF) {
        canvas.drawArc(
                rectF,
                180 - endAngel,//startAngle
                sweepAngle,//sweepAngle
                false,//userCenter
                backgroundPaint);
    }

    /***
     * 绘制进度
     *
     * @param canvas 画布
     */
    private void drawInsideArc(Canvas canvas, RectF rectF) {

        //初始化画笔
        shaderPaint.setStyle(Paint.Style.STROKE);
        shaderPaint.setStrokeWidth(arcWidth);
        shaderPaint.setStrokeCap(Paint.Cap.ROUND);
        shaderPaint.setAntiAlias(true);

        //设置渐变
        int colorSweep[] = {Color.TRANSPARENT, Color.TRANSPARENT, Color.parseColor("#346B43"), Color.parseColor("#39B44A")};
        shaderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));//shaderPaint.setColor(yellowColor);
        SweepGradient sweepGradient = new SweepGradient(width / 2, width / 2, colorSweep, null);
        shaderPaint.setShader(sweepGradient);

        canvas.drawArc(
                rectF,
                180 - endAngel,//startAngle
                mAngel,//sweepAngle
                false,//userCenter
                shaderPaint);
    }

    /**
     * 设置当前的进度值
     *
     * @param currentSweepAngle
     */
    public void setCurrentSweepAngle(float currentSweepAngle) {
        sendCurrentProgress = currentSweepAngle < 0 ? 0 : currentSweepAngle;
        if (currentSweepAngle < 1) {
            currentSweepAngle = 1;
        } else if (currentSweepAngle > maxProgress) {
            currentSweepAngle = maxProgress;
        }
        this.currentSweepAngle = currentSweepAngle * sweepAngle / maxProgress;
        initListener();
        initHandler();
        initAnimator();
        mValueAnimator.start();
    }

    /**
     * 设置最大的进度值
     *
     * @param maxProgress
     */
    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    private void initHandler() {
        mAnimatorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        mValueAnimator.removeAllUpdateListeners();
                        mValueAnimator.removeAllListeners();
                        break;
                    case 1:
                        invalidate();
                        break;
                }
            }
        };
    }


    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngel = (float) animation.getAnimatedValue() * currentSweepAngle;
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // getHandle发消息通知动画状态更新
                mAnimatorHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    private void initAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mValueAnimator.addUpdateListener(mUpdateListener);
//        mValueAnimator.setInterpolator(new AccelerateInterpolator());//开始慢，然后加速
        mValueAnimator.setInterpolator(new DecelerateInterpolator());//先加速，再减速
//        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());//开始和结束时慢，中间加速变化
//        mValueAnimator.setInterpolator(new AnticipateInterpolator());//起点往回移，再加速
//        mValueAnimator.setInterpolator(new OvershootInterpolator());//加速超过结束值后，再返回
//        mValueAnimator.setInterpolator(new AnticipateOvershootInterpolator());//起点先回移，加速超过结束值后，再返回
//        mValueAnimator.setInterpolator(new BounceInterpolator());//在最后以弹球效果显示
        mValueAnimator.addListener(mAnimatorListener);
    }
}