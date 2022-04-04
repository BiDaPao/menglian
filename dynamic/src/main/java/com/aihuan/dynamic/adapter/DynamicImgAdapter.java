package com.aihuan.dynamic.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dynamic.R;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.interfaces.OnItemClickListener;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by debug on 2019/7/15.
 * 动态图片
 */
public class DynamicImgAdapter extends RecyclerView.Adapter<DynamicImgAdapter.Vh> {
    private List<String> mList;
    private Context mContext;
    private OnItemClickListener<String> mOnItemClickListener;


    public DynamicImgAdapter( Context context) {
        mContext = context;

    }

    public void setList(List<String> list) {
        if (mList == null) {
            mList = new ArrayList<>();
        }else {
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener<String> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_img2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        holder.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        private ImageView mImg;
        private String mPath;
        private int mPos;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            if (mOnItemClickListener != null) {
                mImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(mPath, mPos);
                    }
                });
            }
        }

        void setData(String path, int pos) {
            mPath = path;
            mPos = pos;
            ImgLoader.display(mContext, path, mImg);
        }
    }
}
