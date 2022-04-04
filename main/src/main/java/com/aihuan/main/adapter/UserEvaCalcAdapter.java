package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.main.R;
import com.aihuan.one.bean.ImpressBean;
import com.aihuan.one.custom.ImpressTextView;

/**
 * Created by cxf on 2019/4/21.
 */

public class UserEvaCalcAdapter extends RefreshAdapter<ImpressBean> {

    public UserEvaCalcAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_impress_calc, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {

        ImpressTextView mTextView;

        public Vh(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text);
        }

        void setData(ImpressBean bean) {
            mTextView.setBean(bean);
        }
    }

}
