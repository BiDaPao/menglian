package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.main.R;
import com.aihuan.main.bean.UserEvaBean;
import com.aihuan.one.custom.ImpressGroup;

/**
 * Created by cxf on 2019/4/21.
 */

public class UserEvaAdapter extends RefreshAdapter<UserEvaBean> {

    public UserEvaAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_user_home_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
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

}
