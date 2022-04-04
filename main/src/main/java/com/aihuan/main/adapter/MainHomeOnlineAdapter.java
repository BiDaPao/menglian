package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.CommonIconUtil;
import com.aihuan.main.R;

/**
 * Created by Sky.L on 2019/10/24
 */
public class MainHomeOnlineAdapter extends RefreshAdapter<UserBean> {
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnInviteClickListener;
    private ItemPartClickListener mItemPartClickListener;
    private boolean mIsAuth;
    public MainHomeOnlineAdapter(Context context,boolean isAuth) {
        super(context);
        mIsAuth = isAuth;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mItemPartClickListener != null){
                        mItemPartClickListener.onAvatarClick(mList.get(position));
                    }
                }
            }
        };
        mOnInviteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mItemPartClickListener != null){
                        mItemPartClickListener.onInviteClick(mList.get(position));
                    }
                }
            }
        };

    }

    public void updateIsAuth(boolean isAuth){
        mIsAuth = isAuth;
    }

    public void setItemPartClickListener(ItemPartClickListener itemPartClickListener) {
        mItemPartClickListener = itemPartClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_main_home_online,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((Vh)holder).setData(mList.get(position),position);
    }


    class Vh extends RecyclerView.ViewHolder{
        TextView btn_invite;
        TextView tv_nickname;
        TextView tv_city;
        ImageView iv_sex;
        ImageView iv_avatar;


        public Vh(View itemView) {
            super(itemView);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_city = itemView.findViewById(R.id.tv_city);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            btn_invite = itemView.findViewById(R.id.btn_invite);
            iv_sex = itemView.findViewById(R.id.iv_sex);
            btn_invite.setOnClickListener(mOnInviteClickListener);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(UserBean bean,int position){
            itemView.setTag(position);
            btn_invite.setTag(position);
            if (mIsAuth){
                if (btn_invite.getVisibility() != View.VISIBLE){
                    btn_invite.setVisibility(View.VISIBLE);
                }
            }else {
                if (btn_invite.getVisibility() == View.VISIBLE){
                    btn_invite.setVisibility(View.INVISIBLE);
                }
            }
            String city = bean.getCity();
            if (!TextUtils.isEmpty(city)){
                tv_city.setText(city);
            }
            tv_nickname.setText(bean.getUserNiceName());
            ImgLoader.display(mContext,bean.getAvatarThumb(),iv_avatar);
            iv_sex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
        }
    }


    public interface ItemPartClickListener{
        void onAvatarClick(UserBean bean);

        void onInviteClick(UserBean bean);
    }
}
