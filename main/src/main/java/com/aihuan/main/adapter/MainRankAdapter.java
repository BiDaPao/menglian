package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aihuan.common.glide.ImgLoader;
import com.aihuan.main.R;
import com.aihuan.main.bean.ListBean;

import java.util.List;

/**
 * Created by debug on 2019/7/29.
 */

public class MainRankAdapter extends RecyclerView.Adapter<MainRankAdapter.Vh> {
    private List<ListBean> mList;
    private Context mContext;

    public MainRankAdapter(Context context, List<ListBean> list) {
        mContext = context;
        mList = list;

    }

    public void setList(List<ListBean> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.item_main_rank, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        holder.setData(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        private ImageView mAvatar;
        private ImageView mBg;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mBg = itemView.findViewById(R.id.bg);
        }

        void setData(ListBean bean,int pos) {
            ImgLoader.display(mContext, bean.getAvatarThumb(), mAvatar);
            if (pos==0){
                mBg.setImageResource(R.mipmap.head_1);
            }else if (pos==1){
                mBg.setImageResource(R.mipmap.head_2);
            }else if (pos==2){
                mBg.setImageResource(R.mipmap.head_3);
            }
        }
    }
}
