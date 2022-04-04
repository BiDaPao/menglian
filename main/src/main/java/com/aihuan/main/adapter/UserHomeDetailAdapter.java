package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.main.R;
import com.aihuan.main.bean.UserEvaBean;
import com.aihuan.one.custom.ImpressGroup;

/**
 * Created by cxf on 2019/4/8.
 */

public class UserHomeDetailAdapter extends RefreshAdapter<UserEvaBean> {

    private static final int HEAD = -1;
    private View mHeadView;

    public UserHomeDetailAdapter(Context context) {
        super(context);
        mHeadView = mInflater.inflate(R.layout.item_user_home_head, null, false);
        mHeadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            ViewParent viewParent = mHeadView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mHeadView);
            }
            HeadVh headVh = new HeadVh(mHeadView);
            headVh.setIsRecyclable(false);
            return headVh;
        }
        return new Vh(mInflater.inflate(R.layout.item_user_home_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }


    class HeadVh extends RecyclerView.ViewHolder {

        public HeadVh(View itemView) {
            super(itemView);
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImpressGroup mImpressGroup;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mImpressGroup = itemView.findViewById(R.id.impress_group);
        }

        void setData(UserEvaBean bean) {
            ImgLoader.display(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mImpressGroup.showData(bean.getEvaList());
        }
    }

    public View getHeadView() {
        return mHeadView;
    }


}
