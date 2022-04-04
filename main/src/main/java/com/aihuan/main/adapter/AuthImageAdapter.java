package com.aihuan.main.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.Constants;
import com.aihuan.common.upload.UploadBean;
import com.aihuan.main.R;
import com.aihuan.main.activity.AuthActivity;
import com.aihuan.main.custom.UploadImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/4/9.
 */

public class AuthImageAdapter extends RecyclerView.Adapter<AuthImageAdapter.Vh> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<UploadBean> mList;
    private UploadImageView.ActionListener mActionListener;
    private RecyclerView mRecyclerView;
    private Handler mHandler;

    public AuthImageAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mList.add(new UploadBean());
        mInflater = LayoutInflater.from(context);
        mActionListener = new UploadImageView.ActionListener() {
            @Override
            public void onAddClick(UploadImageView uploadImageView) {
                Object tag = uploadImageView.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (position >= 0 && position < mList.size()) {
                    ((AuthActivity) mContext).chooseImage(position);
                }
            }

            @Override
            public void onDelClick(UploadImageView uploadImageView) {
                Object tag = uploadImageView.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (position >= 0 && position < mList.size()) {
                    if (position == Constants.AUTH_IMAGE_MAX_SIZE - 1) {
                        mList.get(position).setEmpty();
                        notifyItemChanged(position);
                    } else {
                        mList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
                        int size = mList.size();
                        if (size == Constants.AUTH_IMAGE_MAX_SIZE - 1 && !mList.get(size - 1).isEmpty()) {
                            mList.add(new UploadBean());
                            notifyItemInserted(size + 1);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(0, 300);
                            }
                        }
                    }
                }
            }
        };
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mRecyclerView != null) {
                    mRecyclerView.smoothScrollToPosition(mList.size() - 1);
                }
            }
        };
    }


    public void setList(List<UploadBean> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        mList.clear();
        mList.addAll(list);
        if (mList.size() < Constants.AUTH_IMAGE_MAX_SIZE) {
            mList.add(new UploadBean());
        }
        notifyDataSetChanged();
    }

    public void insertItem(List<String> imagePathList) {
        if (mList == null || imagePathList == null || imagePathList.size() == 0) {
            return;
        }
        int size = Math.min(Constants.AUTH_IMAGE_MAX_SIZE - mList.size() + 1, imagePathList.size());
        if (size == 0) {
            return;
        }
        List<UploadBean> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            File file = new File(imagePathList.get(i));
            if (file.exists()) {
                UploadBean bean = new UploadBean(file);
                list.add(bean);
            }
        }
        if (list.size() == 0) {
            return;
        }
        mList.addAll(mList.size() - 1, list);
        if (mList.size() > Constants.AUTH_IMAGE_MAX_SIZE) {
            mList = mList.subList(0, Constants.AUTH_IMAGE_MAX_SIZE);
        }
        notifyDataSetChanged();
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, 300);
        }
    }

    public void updateItem(int position, File file) {
        int size = mList.size();
        if (position >= 0 && position < size) {
            UploadBean bean = mList.get(position);
            if (bean != null) {
                bean.setOriginFile(file);
            }
            notifyItemChanged(position);
            if (position == size - 1 && size < Constants.AUTH_IMAGE_MAX_SIZE) {
                mList.add(new UploadBean());
                notifyItemInserted(size + 1);
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 300);
                }
            }
        }
    }

    public List<UploadBean> getList() {
        return mList;
    }

    public boolean isEmpty() {
        for (UploadBean bean : mList) {
            if (!bean.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_auth_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        mContext = null;
    }

    class Vh extends RecyclerView.ViewHolder {

        UploadImageView mImageView;

        public Vh(View itemView) {
            super(itemView);
            mImageView = (UploadImageView) itemView;
            mImageView.setActionListener(mActionListener);
        }

        void setData(UploadBean bean, int positon, Object payload) {
            mImageView.setTag(positon);
            if (payload == null) {
                mImageView.showImageData(bean);
            }
        }
    }

}
