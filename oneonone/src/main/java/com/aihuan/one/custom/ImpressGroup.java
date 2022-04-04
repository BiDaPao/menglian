package com.aihuan.one.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aihuan.one.R;
import com.aihuan.one.bean.ImpressBean;

import java.util.List;

/**
 * Created by cxf on 2019/4/12.
 */

public class ImpressGroup extends LinearLayout {

    private static final int COUNT = 3;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private Context mContext;
    private ImpressTextView[] mImpressTextViews;
    private ImpressBean[] mImpressBeans;
    private List<ImpressBean> mImpressBeanList;
    private int mImpressHeight;
    private int mRadius;
    private int mPadding;
    private int mGravity;

    public ImpressGroup(Context context) {
        this(context, null);
    }

    public ImpressGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImpressGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImpressGroup);
        mImpressHeight = (int) ta.getDimension(R.styleable.ImpressGroup_ig_im_height, 0);
        mRadius = (int) ta.getDimension(R.styleable.ImpressGroup_ig_im_radius, 0);
        mPadding = (int) ta.getDimension(R.styleable.ImpressGroup_ig_im_padding, 0);
        mGravity = ta.getInt(R.styleable.ImpressGroup_ig_im_gravity, LEFT);
        ta.recycle();
        mImpressTextViews = new ImpressTextView[COUNT];
        mImpressBeans = new ImpressBean[COUNT];
        init();
    }

    private void init() {
        if (mGravity == LEFT) {
            setGravity(Gravity.CENTER_VERTICAL);
        } else {
            setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        }
        ImpressTextView v0 = new ImpressTextView(mContext);
        v0.setRadius(mRadius);
        v0.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mImpressHeight));
        v0.setPadding(mPadding, 0, mPadding, 0);
        v0.setGravity(Gravity.CENTER);
        v0.setTextSize(12);
        v0.setVisibility(INVISIBLE);
        addView(v0);
        mImpressTextViews[0] = v0;

        ImpressTextView v1 = new ImpressTextView(mContext);
        v1.setRadius(mRadius);
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mImpressHeight);
        params1.leftMargin = mPadding;
        v1.setLayoutParams(params1);
        v1.setPadding(mPadding, 0, mPadding, 0);
        v1.setGravity(Gravity.CENTER);
        v1.setTextSize(12);
        v1.setVisibility(INVISIBLE);
        addView(v1);
        mImpressTextViews[1] = v1;

        ImpressTextView v2 = new ImpressTextView(mContext);
        v2.setRadius(mRadius);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mImpressHeight);
        params2.leftMargin = mPadding;
        v2.setLayoutParams(params2);
        v2.setPadding(mPadding, 0, mPadding, 0);
        v2.setGravity(Gravity.CENTER);
        v2.setTextSize(12);
        v2.setVisibility(INVISIBLE);
        addView(v2);
        mImpressTextViews[2] = v2;
    }

    public void showData(List<ImpressBean> list) {
        mImpressBeanList = list;
        for (int i = 0; i < COUNT; i++) {
            mImpressBeans[i] = null;
        }
        if (list != null && list.size() > 0) {
            if (mGravity == LEFT) {
                for (int i = 0, size = list.size(); i < size; i++) {
                    mImpressBeans[i] = list.get(i);
                }
            } else {
                int size = list.size();
                int diff = COUNT - size;
                for (int i = 0; i < COUNT; i++) {
                    if (diff <= i) {
                        mImpressBeans[i] = list.get(i - diff);
                    }
                }
            }
        }
        for (int i = 0; i < COUNT; i++) {
            ImpressBean bean = mImpressBeans[i];
            if (bean != null) {
                bean.setChecked(true);
                if (mImpressTextViews[i] != null && mImpressTextViews[i].getVisibility() != VISIBLE) {
                    mImpressTextViews[i].setVisibility(VISIBLE);
                }
                mImpressTextViews[i].setBean(bean);
            } else {
                if (mImpressTextViews[i] != null && mImpressTextViews[i].getVisibility() == VISIBLE) {
                    mImpressTextViews[i].setVisibility(INVISIBLE);
                }
            }
        }

    }

    public List<ImpressBean> getImpressBeanList() {
        return mImpressBeanList;
    }

}
