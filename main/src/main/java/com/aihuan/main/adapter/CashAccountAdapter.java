package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aihuan.common.Constants;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.main.R;
import com.aihuan.main.bean.CashAccountBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/23.
 */

public class CashAccountAdapter extends RecyclerView.Adapter<CashAccountAdapter.Vh> {

    private List<CashAccountBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mDeleteClickListener;
    private ActionListener mActionListener;
    private String mCashAccountId;

    public CashAccountAdapter(Context context, String cashAccountId) {
        mList = new ArrayList<>();
        mCashAccountId = cashAccountId;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    int position = (int) tag;
                    CashAccountBean bean = mList.get(position);
                    if (bean.getDel_status() == 0) {
                        ToastUtil.show("删除账户申请正在审核中...");
                        return;
                    }
                    mActionListener.onItemClick(mList.get(position), position);
                }
            }
        };
        mDeleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    int position = (int) tag;
                    CashAccountBean bean = mList.get(position);
                    if (bean.getDel_status() == 0) {
                        ToastUtil.show("删除账户申请正在审核中...");
                        return;
                    }
                    mActionListener.onItemDelete(mList.get(position), position);
                }
            }
        };
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_cash_account, parent, false));
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


    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
    }

    public void insertItem(CashAccountBean bean) {
        int position = mList.size();
        mList.add(bean);
        notifyItemInserted(position);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void setList(List<CashAccountBean> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }


    class Vh extends RecyclerView.ViewHolder {

        RadioButton mRadioButton;
        ImageView mIcon;
        TextView mAccount;
        View mBtnDelete;
        TextView tvDeleteReason;

        public Vh(View itemView) {
            super(itemView);
            mRadioButton = (RadioButton) itemView.findViewById(R.id.radioButton);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mAccount = itemView.findViewById(R.id.account);
            mBtnDelete = itemView.findViewById(R.id.btn_delete);
            tvDeleteReason = itemView.findViewById(R.id.tv_delete_reason);
            itemView.setOnClickListener(mOnClickListener);
            mBtnDelete.setOnClickListener(mDeleteClickListener);
        }

        void setData(CashAccountBean bean, int position, Object payload) {
            itemView.setTag(position);
            mBtnDelete.setTag(position);
            if (payload == null) {
                ImgLoader.displayAvatar(mIcon.getContext(), bean.getAlipay_avatar(), mIcon);
                mAccount.setText(bean.getAlipay_nickname());
            }
            mRadioButton.setChecked(bean.getId().equals(mCashAccountId));
            if (bean.getDel_status() == 2) {
                tvDeleteReason.setText(bean.getDel_remark());
                tvDeleteReason.setVisibility(View.VISIBLE);
            } else {
                tvDeleteReason.setVisibility(View.GONE);
            }
        }
    }

    public interface ActionListener {
        void onItemClick(CashAccountBean bean, int position);

        void onItemDelete(CashAccountBean bean, int position);
    }

}
