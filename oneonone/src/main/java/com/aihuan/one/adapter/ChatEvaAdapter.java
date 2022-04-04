package com.aihuan.one.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihuan.common.Constants;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.one.R;
import com.aihuan.one.bean.ChatEvaBean;
import com.aihuan.one.custom.ImpressTextView;

import java.util.List;

/**
 * Created by cxf on 2019/4/21.
 */

public class ChatEvaAdapter extends RecyclerView.Adapter {

    private static final byte TITLE = 1;
    private static final byte EVA_TYPE_NONE = 0;//未评价
    private static final byte EVA_TYPE_GOOD = 1;//已经选择了好评
    private static final byte EVA_TYPE_BAD = 2;//已经选择了差评
    private List<ChatEvaBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private byte mEvaType = EVA_TYPE_NONE;//评价类型

    public ChatEvaAdapter(Context context, List<ChatEvaBean> list) {
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
                    ChatEvaBean bean = mList.get(position);
                    if (!bean.isChecked()) {
                        if (bean.isGood()) {
                            if (mEvaType == EVA_TYPE_BAD) {
                                return;
                            }
                            mEvaType = EVA_TYPE_GOOD;
                        } else {
                            if (mEvaType == EVA_TYPE_GOOD) {
                                return;
                            }
                            mEvaType = EVA_TYPE_BAD;
                        }
                        if (getCheckedCount() >= 3) {
                            ToastUtil.show(R.string.chat_eva_add_max);
                            return;
                        }
                        bean.setChecked(true);
                    } else {
                        bean.setChecked(false);
                        if (getCheckedCount() == 0) {
                            mEvaType = EVA_TYPE_NONE;
                        }
                    }
                    notifyItemChanged(position, Constants.PAYLOAD);
                }

            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).isTitle()) {
            return TITLE;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TITLE) {
            return new TitleVh(mInflater.inflate(R.layout.item_chat_eva_title, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_choose_impress, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        if (vh instanceof Vh) {
            Object payload = payloads.size() > 0 ? mList.get(0) : null;
            ((Vh) vh).setData(mList.get(position), position, payload);
        } else if (vh instanceof TitleVh) {
            ((TitleVh) vh).setData(mList.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    private int getCheckedCount() {
        int checkedCount = 0;
        for (ChatEvaBean bean : mList) {
            if (bean.isChecked()) {
                checkedCount++;
                if (checkedCount >= 3) {
                    return 3;
                }
            }
        }
        return checkedCount;
    }

    public String getChooseEvaList() {
        StringBuilder sb = null;
        for (ChatEvaBean bean : mList) {
            if (bean.isChecked()) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                sb.append(bean.getId());
                sb.append(",");
            }
        }
        if (sb != null) {
            String s = sb.toString();
            s = s.substring(0, s.length() - 1);
            return s;
        }
        return null;
    }


    class TitleVh extends RecyclerView.ViewHolder {

        TextView mTextView;

        public TitleVh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        void setData(ChatEvaBean bean) {
            mTextView.setText(bean.getTitleText());
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImpressTextView mImpressTextView;

        public Vh(View itemView) {
            super(itemView);
            mImpressTextView = itemView.findViewById(R.id.impress);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ChatEvaBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mImpressTextView.setBean(bean);
            }
            mImpressTextView.refreshChecked();
        }
    }
}
