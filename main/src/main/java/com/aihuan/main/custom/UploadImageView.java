package com.aihuan.main.custom;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.upload.UploadBean;
import com.aihuan.main.R;

import java.io.File;

/**
 * Created by cxf on 2019/4/9.
 */

public class UploadImageView extends FrameLayout {

    private Context mContext;
    private float mScale;
    private ImageView mImageView;
    private View mBtnDel;
    private int mImageRes;
    private int mAddIconRes;
    private int mDelIconRes;
    private int mBgColor;
    private int mAddSize;
    private int mDelSize;
    private ActionListener mActionListener;
    private boolean mHasImage;

    public UploadImageView(@NonNull Context context) {
        this(context, null);
    }

    public UploadImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UploadImageView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UploadImageView);
        mImageRes = ta.getResourceId(R.styleable.UploadImageView_uiv_src, 0);
        mAddIconRes = ta.getResourceId(R.styleable.UploadImageView_uiv_add_icon, 0);
        mDelIconRes = ta.getResourceId(R.styleable.UploadImageView_uiv_del_icon, 0);
        mBgColor = ta.getColor(R.styleable.UploadImageView_uiv_bg_color, 0);
        mAddSize = (int) ta.getDimension(R.styleable.UploadImageView_uiv_add_size, 0);
        mDelSize = (int) ta.getDimension(R.styleable.UploadImageView_uiv_del_size, 0);
        ta.recycle();
        init();
    }

    private void init() {
        setBackgroundColor(mBgColor);
        ImageView addBtn = new ImageView(mContext);
        LayoutParams params1 = new LayoutParams(mAddSize, mAddSize);
        params1.gravity = Gravity.CENTER;
        addBtn.setLayoutParams(params1);
        addBtn.setImageResource(mAddIconRes);
        addView(addBtn);

        mImageView = new ImageView(mContext);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params2);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(mImageRes);
        addView(mImageView);

        ImageView delBtn = new ImageView(mContext);
        mBtnDel = delBtn;
        LayoutParams params3 = new LayoutParams(mDelSize, mDelSize);
        params3.gravity = Gravity.RIGHT;
        int dp5 = dp2px(5);
        delBtn.setPadding(dp5, dp5, dp5, dp5);
        delBtn.setLayoutParams(params3);
        delBtn.setImageResource(mDelIconRes);
        delBtn.setVisibility(INVISIBLE);
        addView(delBtn);

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    if (v == UploadImageView.this) {
                        if (!mHasImage) {
                            mActionListener.onAddClick(UploadImageView.this);
                        }
                    } else if (v == mBtnDel) {
                        mActionListener.onDelClick(UploadImageView.this);
                    }
                }
            }
        };
        setOnClickListener(clickListener);
        delBtn.setOnClickListener(clickListener);
    }

    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }


    public void showImageData(UploadBean uploadBean) {
        File file = uploadBean.getOriginFile();
        if (file != null) {
            if (mImageView != null) {
                ImgLoader.display(mContext, file, mImageView);
                mHasImage = true;
            }
            if (mBtnDel != null && mBtnDel.getVisibility() != VISIBLE) {
                mBtnDel.setVisibility(VISIBLE);
            }
        } else {
            String url = uploadBean.getRemoteAccessUrl();
            if (!TextUtils.isEmpty(url)) {
                if (mImageView != null) {
                    ImgLoader.display(mContext, url, mImageView);
                    mHasImage = true;
                }
                if (mBtnDel != null && mBtnDel.getVisibility() != VISIBLE) {
                    mBtnDel.setVisibility(VISIBLE);
                }
            } else {
                if (mImageView != null) {
                    mImageView.setImageDrawable(null);
                    mHasImage = false;
                }
                if (mBtnDel != null && mBtnDel.getVisibility() == VISIBLE) {
                    mBtnDel.setVisibility(INVISIBLE);
                }
            }
        }
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onAddClick(UploadImageView uploadImageView);

        void onDelClick(UploadImageView uploadImageView);
    }

}
