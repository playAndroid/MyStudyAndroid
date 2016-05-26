package com.mystudy.myzoomimageview.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.regex.Matcher;

/**
 * Created by Administrator on 2016/5/25.
 */
public class MyZommImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    private boolean mOnce;

    /**
     * 初始化的缩放值
     */
    private float mInitScale;
    /**
     * 双击放大的缩放值
     */
    private float mMidScale;
    /**
     * 最大的放大值
     */
    private float mMaxScale;

    private Matrix mScaleMatrix;

    public MyZommImageView(Context context) {
        this(context, null);
    }

    public MyZommImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyZommImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        /**
         * 当View附加到窗口上时调用
         */
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /**
         * 当View从窗口上分离的时候调用
         */
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        /**
         * 全局的布局结束时调用这个方法
         */
        //1 获取ImageView加载完成的图片
        if (!mOnce) {
            //获得控件的宽和高
            int width = getWidth();
            int height = getHeight();
            //得到图片以及宽和高
            Drawable d = getDrawable();
            if (d == null) return;
            int dh = d.getIntrinsicHeight();
            int dw = d.getIntrinsicWidth();

            float scale = 1.0f;
            /**
             * 如果屏幕的宽度大于控件宽度,高度小于控件高度,将其缩小
             */
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }
            /**
             * 如果屏幕的高度大于控件高度,宽度小于控件宽度,将其缩小
             */
            if (dh > height && dw < width) {
                scale = height * 1.0f / dh;
            }

            if ((dh > height && dw > width) || (dh < height && dw < width)) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            mInitScale = scale;
            mMidScale = scale * 2;
            mMaxScale = scale * 4;

            //移动图片到控件的中心
            int dx = getWidth() / 2 - dw / 2;
            int dy = getHeight() / 2 - dh / 2;

            mScaleMatrix.postTranslate(dx, dy);//平移
            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);//缩放
            setImageMatrix(mScaleMatrix);//设置矩阵

            mOnce = true;
        }


    }
}
