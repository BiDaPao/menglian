package com.aihuan.one.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.aihuan.one.R;
import com.aihuan.one.bean.ImpressBean;

/**
 * Created by cxf on 2018/10/15.
 */

public class ImpressTextView extends AppCompatTextView {

    private Paint mPaint;
    private int mRadius;
    private boolean mChecked;
    private int mColor;
    private RectF mRectF;
    private float mScale;
    private int mStrokeWidth;
    private ImpressBean mBean;

    public ImpressTextView(Context context) {
        this(context, null);
    }

    public ImpressTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImpressTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScale = context.getResources().getDisplayMetrics().density;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImpressTextView);
        mRadius = (int) ta.getDimension(R.styleable.ImpressTextView_mt_radius, 0);
        mChecked = ta.getBoolean(R.styleable.ImpressTextView_mt_checked, false);
        mColor = ta.getColor(R.styleable.ImpressTextView_mt_color, 0);
        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mStrokeWidth = dp2px(1);
        mPaint.setStrokeWidth(mStrokeWidth);
        if (mColor != 0) {
            mPaint.setColor(mColor);
        }
        mRectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRectF.top = mStrokeWidth;
        mRectF.left = mStrokeWidth;
        mRectF.right = w - mStrokeWidth;
        mRectF.bottom = h - mStrokeWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPaint != null) {
            if (mChecked) {
                mPaint.setStyle(Paint.Style.FILL);
            } else {
                mPaint.setStyle(Paint.Style.STROKE);
            }
        }
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
        super.onDraw(canvas);
    }

    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }


    public void toggleChecked() {
        setChecked(!mChecked);
    }

    public void setChecked(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        if (mBean != null) {
            mBean.setChecked(checked);
        }
        if (checked) {
            setTextColor(0xffffffff);
        } else {
            setTextColor(mColor);
        }
    }

    public ImpressBean getBean() {
        return mBean;
    }


    public void setBean(ImpressBean bean) {
        mBean = bean;
        mColor = Color.parseColor(bean.getColor());
        mPaint.setColor(mColor);
        mChecked = mBean.isChecked();
        if (mChecked) {
            setTextColor(0xffffffff);
        } else {
            setTextColor(mColor);
        }
        setText(bean.getName());
    }


    public void refreshChecked() {
        if (mBean != null) {
            mChecked = mBean.isChecked();
            if (mChecked) {
                setTextColor(0xffffffff);
            } else {
                setTextColor(mColor);
            }
        }
    }


    public boolean isChecked() {
        return mChecked;
    }


    public void setRadius(int radius) {
        mRadius = radius;
    }
}
