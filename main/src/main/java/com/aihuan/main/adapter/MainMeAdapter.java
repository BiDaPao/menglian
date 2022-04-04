package com.aihuan.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.bean.UserItemBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/28.
 */

public class MainMeAdapter extends RecyclerView.Adapter {

    private static final int HEAD = -1;
    private static final int NORMAL = 0;
    private static final int RADIO = 1;
    private static final int RADIO_TEXT = 2;

    private Context mContext;
    private List<UserItemBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnRadioBtnClickListener;
    private ActionListener mActionListener;
    private View mHeadView;
    private Drawable mRadioCheckDrawable;
    private Drawable mRadioUnCheckDrawable;
    private String mPriceSuffix;

    public MainMeAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mHeadView = mInflater.inflate(R.layout.item_main_me_head, null);
        mHeadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                UserItemBean bean = mList.get(position - 1);
                if (mActionListener != null) {
                    mActionListener.onItemClick(bean);
                }
            }
        };
        mOnRadioBtnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                UserItemBean bean = mList.get(position - 1);
                bean.toggleRadioBtn();
                notifyItemChanged(position, Constants.PAYLOAD);
                if (mActionListener != null) {
                    mActionListener.onRadioBtnChanged(bean);
                }
            }
        };
        mRadioCheckDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_btn_radio_1);
        mRadioUnCheckDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_btn_radio_0);
        mPriceSuffix = String.format(WordUtil.getString(R.string.main_price), CommonAppConfig.getInstance().getCoinName());
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        UserItemBean bean = mList.get(position - 1);
        int id = bean.getId();
        if (id == Constants.MAIN_ME_DISTURB) {
            return RADIO;
        } else if (id == Constants.MAIN_ME_VIDEO || id == Constants.MAIN_ME_VOICE) {
            return RADIO_TEXT;
        }
        return NORMAL;
    }


    public void setList(List<UserItemBean> list) {
        if (list == null) {
            return;
        }
        boolean changed = false;
        if (mList.size() != list.size()) {
            changed = true;
        } else {
            for (int i = 0, size = mList.size(); i < size; i++) {
                if (!mList.get(i).equals(list.get(i))) {
                    changed = true;
                    break;
                }
            }
        }
        if (changed) {
            mList = list;
            notifyDataSetChanged();
        }
    }

    public void updateVideoPrice(String videoPrice) {
        for (int i = 0, size = mList.size(); i < size; i++) {
            UserItemBean bean = mList.get(i);
            if (bean.getId() == Constants.MAIN_ME_VIDEO) {
                bean.setPriceText(videoPrice);
                notifyItemChanged(i + 1);
                break;
            }
        }
    }

    public void updateVoicePrice(String voicePrice) {
        for (int i = 0, size = mList.size(); i < size; i++) {
            UserItemBean bean = mList.get(i);
            if (bean.getId() == Constants.MAIN_ME_VOICE) {
                bean.setPriceText(voicePrice);
                notifyItemChanged(i + 1);
                break;
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            ViewParent viewParent = mHeadView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mHeadView);
            }
            HeadVh headVh = new HeadVh(mHeadView);
            headVh.setIsRecyclable(false);
            return headVh;
        } else if (viewType == RADIO) {
            return new RadioButtonVh(mInflater.inflate(R.layout.item_main_me_1, parent, false));
        } else if (viewType == RADIO_TEXT) {
            return new RadioButtonTextVh(mInflater.inflate(R.layout.item_main_me_2, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_main_me_0, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position - 1), position, payload);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    class HeadVh extends RecyclerView.ViewHolder {
        public HeadVh(View itemView) {
            super(itemView);
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(UserItemBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
                mName.setText(bean.getName());
            }
        }
    }

    class RadioButtonVh extends Vh {

        ImageView mBtnRadio;

        public RadioButtonVh(View itemView) {
            super(itemView);
            mBtnRadio = itemView.findViewById(R.id.btn_radio);
            mBtnRadio.setOnClickListener(mOnRadioBtnClickListener);
        }

        @Override
        void setData(UserItemBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mBtnRadio.setTag(position);
            }
            mBtnRadio.setImageDrawable(bean.isRadioBtnChecked() ? mRadioCheckDrawable : mRadioUnCheckDrawable);
        }
    }

    class RadioButtonTextVh extends RadioButtonVh {

        TextView mText;

        public RadioButtonTextVh(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.text);
        }

        @Override
        void setData(UserItemBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            mText.setText(StringUtil.contact(bean.getPriceText(), mPriceSuffix));
        }
    }


    public View getHeadView() {
        return mHeadView;
    }


    public interface ActionListener {
        void onItemClick(UserItemBean bean);

        void onRadioBtnChanged(UserItemBean bean);
    }


}
