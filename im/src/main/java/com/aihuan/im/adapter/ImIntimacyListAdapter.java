package com.aihuan.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aihuan.common.Constants;
import com.aihuan.common.custom.ItemSlideHelper;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.L;
import com.aihuan.im.R;
import com.aihuan.im.bean.ImUserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cxf
 * @date 2018/10/24
 */

public class ImIntimacyListAdapter extends RecyclerView.Adapter implements ItemSlideHelper.Callback {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<ImUserBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnDeleteClickListener;
    private ImListAdapter.ActionListener mActionListener;

    public ImIntimacyListAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    ImUserBean bean = mList.get(position);
                    if (bean.getUnReadCount() != 0) {
                        bean.setUnReadCount(0);
                        notifyItemChanged(position, Constants.PAYLOAD);
                    }
                    if (mActionListener != null) {
                        mActionListener.onItemClick(bean);
                    }
                }
            }
        };
        mOnDeleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    ImUserBean bean = mList.get(position);
                    removeItem(position);
                    if (mActionListener != null) {
                        mActionListener.onItemDelete(bean, mList.size());
                    }
                }
            }
        };
    }

    public void setList(List<ImUserBean> list) {
        if (list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void setFollow(String toUid, int attent) {
        if (!TextUtils.isEmpty(toUid)) {
            for (ImUserBean bean : mList) {
                if (toUid.equals(bean.getId())) {
                    bean.setAttent(attent);
                    break;
                }
            }
        }
    }

    public void setActionListener(ImListAdapter.ActionListener actionListener) {
        mActionListener = actionListener;
    }

    private void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
    }

    public void removeItem(String toUid) {
        if (TextUtils.isEmpty(toUid)) {
            return;
        }
        int position = getPosition(toUid);
        if (position >= 0 && position < mList.size()) {
            ImUserBean bean = mList.get(position);
            removeItem(position);
            if (mActionListener != null) {
                mActionListener.onItemDelete(bean, mList.size());
            }
        }
    }

    public int getPosition(String uid) {
        for (int i = 0; i < mList.size(); i++) {
            ImUserBean userBean = mList.get(i);
            if (userBean.getId().equals(uid)) {
                return i;
            }
        }
        return -1;
    }

    public void updateItem(String lastMessage, String lastTime, int unReadCount, int position) {
        if (position >= 0 && position < mList.size()) {
            ImUserBean bean = mList.get(position);
            if (bean != null) {
                bean.setHasConversation(true);
                bean.setLastMessage(lastMessage);
                bean.setLastTime(lastTime);
                bean.setUnReadCount(unReadCount);
                notifyItemChanged(position, Constants.PAYLOAD);
            }
        }
    }

    public void insertItem(ImUserBean bean) {
        int position = mList.size();
        mList.add(bean);
        notifyItemInserted(position);
    }

    public void resetAllUnReadCount() {
        if (mList != null && mList.size() > 0) {
            for (ImUserBean bean : mList) {
                bean.setUnReadCount(0);
            }
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_im_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position), position, payload);
        } else if (vh instanceof AnchorVh) {
            ((AnchorVh) vh).setData(mList.get(position), position, payload);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    class HeadVh extends RecyclerView.ViewHolder {

        public HeadVh(View itemView) {
            super(itemView);
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mVip;
        TextView mMsg;
        TextView mTime;
        View mBtnDelete;
        TextView mRedPoint;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mVip = (ImageView) itemView.findViewById(R.id.vip);
            mMsg = (TextView) itemView.findViewById(R.id.msg);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mRedPoint = (TextView) itemView.findViewById(R.id.red_point);
            mBtnDelete = itemView.findViewById(R.id.btn_delete);
            itemView.setOnClickListener(mOnClickListener);
            mBtnDelete.setOnClickListener(mOnDeleteClickListener);
        }

        void setData(ImUserBean bean, int position, Object payload) {
            itemView.setTag(position);
            mBtnDelete.setTag(position);
            if (payload == null) {
                ImgLoader.display(mContext, bean.getAvatar(), mAvatar);
                if (TextUtils.isEmpty(bean.getToremark())) {
                    mName.setText(bean.getUserNiceName());
                } else {
                    mName.setText(bean.getToremark());
                }
                if (bean.isVip()) {
                    if (mVip.getVisibility() != View.VISIBLE) {
                        mVip.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mVip.getVisibility() == View.VISIBLE) {
                        mVip.setVisibility(View.INVISIBLE);
                    }
                }
            }
            mMsg.setText(bean.getLastMessage());
            mTime.setText(bean.getLastTime());
            if (bean.getUnReadCount() > 0) {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
                mRedPoint.setText(String.valueOf(bean.getUnReadCount()));
            } else {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    class AnchorVh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mVip;
        TextView mMsg;
        TextView mTime;
        TextView mRedPoint;
        View mBtnPriChat;

        public AnchorVh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mVip = (ImageView) itemView.findViewById(R.id.vip);
            mMsg = (TextView) itemView.findViewById(R.id.msg);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mRedPoint = (TextView) itemView.findViewById(R.id.red_point);
            mBtnPriChat = itemView.findViewById(R.id.btn_pri_chat);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ImUserBean bean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                ImgLoader.display(mContext, bean.getAvatar(), mAvatar);
                if (TextUtils.isEmpty(bean.getToremark())) {
                    mName.setText(bean.getUserNiceName());
                } else {
                    mName.setText(bean.getToremark());
                }
                if (bean.isVip()) {
                    if (mVip.getVisibility() != View.VISIBLE) {
                        mVip.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mVip.getVisibility() == View.VISIBLE) {
                        mVip.setVisibility(View.INVISIBLE);
                    }
                }
            }
            mMsg.setText(bean.getLastMessage());
            if (bean.isHasConversation()) {
                if (mBtnPriChat.getVisibility() == View.VISIBLE) {
                    mBtnPriChat.setVisibility(View.INVISIBLE);
                }
                mTime.setText(bean.getLastTime());
                if (bean.getUnReadCount() > 0) {
                    if (mRedPoint.getVisibility() != View.VISIBLE) {
                        mRedPoint.setVisibility(View.VISIBLE);
                    }
                    mRedPoint.setText(String.valueOf(bean.getUnReadCount()));
                } else {
                    if (mRedPoint.getVisibility() == View.VISIBLE) {
                        mRedPoint.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                mTime.setText("");
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
                if (mBtnPriChat.getVisibility() != View.VISIBLE) {
                    mBtnPriChat.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnItemTouchListener(new ItemSlideHelper(mContext, this));
    }


    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder vh) {
        if (vh instanceof HeadVh || vh instanceof AnchorVh) {
            return 0;
        }
        return DpUtil.dp2px(70);
    }

    @Override
    public RecyclerView.ViewHolder getChildViewHolder(View childView) {
        if (mRecyclerView != null && childView != null) {
            return mRecyclerView.getChildViewHolder(childView);
        }
        return null;
    }

    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }

    @Override
    public boolean useLeftScroll() {
        return true;
    }

    @Override
    public void onLeftScroll(RecyclerView.ViewHolder holder) {

    }
}
