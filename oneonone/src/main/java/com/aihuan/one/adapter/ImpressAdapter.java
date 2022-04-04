package com.aihuan.one.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.Constants;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.one.R;
import com.aihuan.one.bean.ImpressBean;
import com.aihuan.one.custom.ImpressTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/4/12.
 */

public class ImpressAdapter extends RecyclerView.Adapter<ImpressAdapter.Vh> {

    private LayoutInflater mInflater;
    private List<ImpressBean> mList;
    private View.OnClickListener mOnClickListener;

    public ImpressAdapter(Context context, List<ImpressBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (position >= 0 && position < mList.size()) {
                    ImpressBean bean = mList.get(position);
                    if (!bean.isChecked() && getCheckedCount() >= 3) {
                        ToastUtil.show(R.string.impress_add_max);
                        return;
                    }
                    bean.setChecked(!bean.isChecked());
                    notifyItemChanged(position, Constants.PAYLOAD);
                }

            }
        };
    }

    private int getCheckedCount() {
        int checkedCount = 0;
        for (ImpressBean bean : mList) {
            if (bean.isChecked()) {
                checkedCount++;
                if (checkedCount >= 3) {
                    return 3;
                }
            }
        }
        return checkedCount;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_choose_impress, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? mList.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImpressTextView mImpressTextView;

        public Vh(View itemView) {
            super(itemView);
            mImpressTextView = itemView.findViewById(R.id.impress);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ImpressBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mImpressTextView.setBean(bean);
            }
            mImpressTextView.refreshChecked();
        }
    }

    public List<ImpressBean> getChooseImpressList() {
        List<ImpressBean> list = null;
        for (ImpressBean bean : mList) {
            if (bean.isChecked()) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(bean);
            }
        }
        return list;
    }
}
