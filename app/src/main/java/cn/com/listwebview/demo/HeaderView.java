package cn.com.listwebview.demo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import cn.com.listwebview.demo.utils.DensityUtil;

/**
 * Created by LiuZhen on 2017/3/28.
 */
public class HeaderView extends View {

    private Paint mPath;
    ValueAnimator animator1, animator2;
    private float r;
    float fraction1;
    float fraction2;
    boolean animating = false;
    private int num = 5;
    private int cir_x = 0;

    public HeaderView(Context context) {
        this(context, null, 0);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        r = DensityUtil.dp2px(getContext(), 6);

        mPath = new Paint();
        mPath.setAntiAlias(true);
        mPath.setColor(Color.rgb(114, 114, 114));

        animator1 = ValueAnimator.ofFloat(1f, 1.2f, 1f, 0.8f);//从左到右过渡
        animator1.setDuration(800);
        animator1.setInterpolator(new DecelerateInterpolator());//DecelerateInterpolator减速插补器
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction1 = (float) animation.getAnimatedValue();//监听动画运动值
                invalidate();
            }
        });
        animator1.setRepeatCount(ValueAnimator.INFINITE);//设置重复次数为无限次
        animator1.setRepeatMode(ValueAnimator.REVERSE);//RESTART是直接重新播放

        animator2 = ValueAnimator.ofFloat(1f, 0.8f, 1f, 1.2f);
        animator2.setDuration(800);
        animator2.setInterpolator(new DecelerateInterpolator());
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction2 = (float) animation.getAnimatedValue();
            }
        });
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.REVERSE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getMeasuredWidth() / num - 10;
        for (int i = 0; i < num; i++) {
            if (animating) {
                switch (i) {
                    case 0:
                        mPath.setAlpha(105);
                        mPath.setColor(getResources().getColor(R.color.Yellow));
                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 2 - 2 * w / 3 * 2, getMeasuredHeight() / 2, r * fraction2, mPath);
                        break;
                    case 1:
                        mPath.setAlpha(145);
                        mPath.setColor(getResources().getColor(R.color.Green));
                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 1 - w / 3 * 2, getMeasuredHeight() / 2, r * fraction2, mPath);
                        break;
                    case 2:
                        mPath.setAlpha(255);
                        mPath.setColor(getResources().getColor(R.color.Blue));
                        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r * fraction1, mPath);
                        break;
                    case 3:
                        mPath.setAlpha(145);
                        mPath.setColor(getResources().getColor(R.color.Orange));
                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 1 + w / 3 * 2, getMeasuredHeight() / 2, r * fraction2, mPath);
                        break;
                    case 4:
                        mPath.setAlpha(105);
                        mPath.setColor(getResources().getColor(R.color.Yellow));
                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 2 + 2 * w / 3 * 2, getMeasuredHeight() / 2, r * fraction2, mPath);
                        break;
                }
            } else {
                switch (i) {
                    case 0:
                        mPath.setAlpha(105);
                        mPath.setColor(getResources().getColor(R.color.Yellow));
                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 2 - 2 * w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                    case 1:
                        mPath.setAlpha(145);
                        mPath.setColor(getResources().getColor(R.color.Green));
                        canvas.drawCircle(getMeasuredWidth() / 2 - cir_x * 1 - w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                    case 2:
                        mPath.setAlpha(255);
                        mPath.setColor(getResources().getColor(R.color.Blue));
                        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                    case 3:
                        mPath.setAlpha(145);
                        mPath.setColor(getResources().getColor(R.color.Orange));
                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 1 + w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                    case 4:
                        mPath.setAlpha(105);
                        mPath.setColor(getResources().getColor(R.color.Yellow));
                        canvas.drawCircle(getMeasuredWidth() / 2 + cir_x * 2 + 2 * w / 3 * 2, getMeasuredHeight() / 2, r, mPath);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator1 != null) animator1.cancel();
        if (animator2 != null) animator2.cancel();
    }

    public View getView() {
        return this;
    }

    public void onPullingDown(float fraction) {
        setScaleX(1 + fraction / 2);
        setScaleY(1 + fraction / 2);
        animating = false;
        if (animator1.isRunning()) {
            animator1.cancel();
            invalidate();
        }
        if (animator2.isRunning()) animator2.cancel();
    }
    public void startAnim() {
        animating = true;
        if (!animator1.isRunning())
            animator1.start();
        if (!animator2.isRunning())
            animator2.start();
    }

    public void reset() {
        animating = false;
        if (animator1.isRunning()) animator1.cancel();
        if (animator2.isRunning()) animator2.cancel();
        invalidate();
    }
}
