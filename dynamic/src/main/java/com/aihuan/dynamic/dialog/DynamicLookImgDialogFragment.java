package com.aihuan.dynamic.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.dynamic.R;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.dialog.AbsDialogFragment;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.activity.DynamicPublishActivity;
import com.aihuan.dynamic.custorm.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debug on 2019/7/22.
 *
 */

public class DynamicLookImgDialogFragment extends AbsDialogFragment implements View.OnClickListener {
    private CommonCallback<Boolean> mCommonCallback;
    private TextView mTvTitle;
    private ViewPager mViewPager;
    private View mBtnDel;
    private ViewPagerAdapter mViewPagerAdapter;
    private int mSum;
    private int mCurPos;


    public void setCommonCallback(CommonCallback<Boolean> commonCallback) {
        mCommonCallback = commonCallback;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_dynamic_imgs2;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog3;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTvTitle = (TextView) findViewById(R.id.titleView);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mBtnDel = findViewById(R.id.btn_del);
        findViewById(R.id.btn_close).setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        loadData();
    }


    private void loadData() {
        Bundle bundle = getArguments();
        assert bundle != null;
        String dynamicUid = bundle.getString(Constants.DYNAMIC_UID);
        if (dynamicUid!=null){
            if (dynamicUid.equals(CommonAppConfig.getInstance().getUid())) {
                mBtnDel.setVisibility(View.VISIBLE);
            }
        }
        List<String> list = bundle.getStringArrayList(Constants.DYNAMIC_IMG_LIST);
        mCurPos = bundle.getInt(Constants.DYNAMIC_IMG_CUR_POS, 0);
        if (list == null) {
            return;
        }
        mSum = list.size();
        List<ImageView> viewList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ImageView imgView = new ImageView(mContext);
            imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ImgLoader.display(mContext, list.get(i), imgView);
            viewList.add(imgView);
        }
        mViewPagerAdapter = new ViewPagerAdapter(viewList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTvTitle.setText((mCurPos + 1) + "/" + mSum);
        mViewPager.setCurrentItem(mCurPos);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurPos = position;
                L.e("---mCurPos-mCurPos--" + mCurPos);
                mTvTitle.setText((mCurPos + 1) + "/" + mSum);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void del() {
        DialogUitl.showTitleStringArrayDialog(mContext, new Integer[]{
                R.string.delete
        }, false, WordUtil.getString(R.string.dynamic_del_tip), new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                L.e("---mCurPos---" + mCurPos);
                if (mViewPagerAdapter != null) {
                    mSum = mViewPagerAdapter.delItem(mCurPos, mViewPager);
                }
                if (mSum > 0) {
                    mTvTitle.setText((mCurPos + 1) + "/" + mSum);
                } else {
                    dismiss();
                }
                ((DynamicPublishActivity) mContext).delUploadImgs(mCurPos);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_del) {
            del();
        } else if (i == R.id.btn_close) {
            dismiss();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mCommonCallback != null) {
            mCommonCallback.callback(true);
        }
        super.onDismiss(dialog);
    }


}
