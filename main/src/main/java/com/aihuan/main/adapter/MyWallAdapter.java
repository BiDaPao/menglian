package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.main.R;
import com.aihuan.main.bean.WallBean;

/**
 * Created by cxf on 2019/5/13.
 */

public class MyWallAdapter extends RefreshAdapter<WallBean> {

    private static final int ADD = -1;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnAddClickListener;
    private ActionListener mActionListener;

    public MyWallAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onItemClick((WallBean) tag);
                }
            }
        };
        mOnAddClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onAddClick();
                }
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).isAdd()) {
            return ADD;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ADD) {
            return new AddVh(mInflater.inflate(R.layout.item_my_wall_add, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_my_wall, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if(vh instanceof Vh){
            ((Vh) vh).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public int getImageCount() {
        int count = 0;
        for (WallBean bean : mList) {
            if (!bean.isAdd()) {
                count++;
            }
        }
        return count;
    }

    public boolean hasVideo() {
        for (WallBean bean : mList) {
            if (bean.isVideo()) {
                return true;
            }
        }
        return false;
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        View mPlay;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mPlay = itemView.findViewById(R.id.icon_play);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(WallBean bean) {
            itemView.setTag(bean);
            ImgLoader.display(mContext, bean.getThumb(), mImg);
            if (bean.isVideo()) {
                if (mPlay.getVisibility() != View.VISIBLE) {
                    mPlay.setVisibility(View.VISIBLE);
                }
            } else {
                if (mPlay.getVisibility() == View.VISIBLE) {
                    mPlay.setVisibility(View.INVISIBLE);
                }
            }

        }

    }


    class AddVh extends RecyclerView.ViewHolder {
        public AddVh(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mOnAddClickListener);
        }

    }

    public interface ActionListener {
        void onItemClick(WallBean bean);

        void onAddClick();
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
