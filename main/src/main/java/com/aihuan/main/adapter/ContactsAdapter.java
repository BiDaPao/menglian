package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.Constants;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.main.R;

/**
 * Created by cxf on 2019/3/5.
 */

public class ContactsAdapter extends RefreshAdapter<UserBean> {

    private View.OnClickListener mClickListener;

    public ContactsAdapter(Context context) {
        super(context);
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((UserBean) tag, 0);
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_contacts, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }


    public void updateItem(String id, int attention) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            UserBean bean = mList.get(i);
            if (bean != null && id.equals(bean.getId())) {
                bean.setAttent2(attention);//-----
                notifyItemChanged(i, Constants.PAYLOAD);
                break;
            }
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mSign;
        View mLine;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mSign = (TextView) itemView.findViewById(R.id.sign);
            mLine = itemView.findViewById(R.id.line);
            itemView.setOnClickListener(mClickListener);
        }

        void setData(UserBean bean, int position) {
            itemView.setTag(bean);
            ImgLoader.displayAvatar(mContext,bean.getAvatarThumb(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mSign.setText(bean.getSignature());
            if (position == getItemCount() - 1) {
                if (mLine.getVisibility() == View.VISIBLE) {
                    mLine.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mLine.getVisibility() != View.VISIBLE) {
                    mLine.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
