package com.aihuan.im.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.im.R;
import com.aihuan.im.bean.SystemMessageBean;

/**
 * Created by cxf on 2018/11/24.
 */

public class SystemMessageAdapter extends RefreshAdapter<SystemMessageBean> {

    private Drawable mDrawable;

    public SystemMessageAdapter(Context context) {
        super(context);
        mDrawable = ContextCompat.getDrawable(context,CommonAppConfig.getInstance().getAppIconRes());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_sys_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mContent;
        TextView mTime;
        ImageView mImg;

        public Vh(View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.content);
            mTime = itemView.findViewById(R.id.time);
            mImg = itemView.findViewById(R.id.img);
        }

        void setData(SystemMessageBean bean) {
            mContent.setText(bean.getContent());
            mTime.setText(bean.getAddtime());
            if (!CommonAppConfig.APP_IS_YUNBAO_SELF) {
                mImg.setImageDrawable(mDrawable);
            }
        }
    }
}
