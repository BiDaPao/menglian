package cn.tillusory.tiui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwangjr.rxbus.RxBus;

import java.util.List;

import cn.tillusory.tiui.R;
import cn.tillusory.tiui.TiPanelLayout;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiBeauty;
import cn.tillusory.tiui.model.TiSelectedPosition;

/**
 * Created by Anko on 2018/11/22.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiBeautyAdapter extends RecyclerView.Adapter<TiDesViewHolder> {
    private final List<TiBeauty> list;

    private int selectedPosition = TiSelectedPosition.POSITION_BEAUTY;

    public TiBeautyAdapter(List<TiBeauty> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TiDesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_label, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_des, parent, false);
        }
        return new TiDesViewHolder(itemView);
    }

    @Override public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull final TiDesViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            holder.tiTextTV.setText(list.get(position).getString(holder.itemView.getContext()));
            if (TiPanelLayout.isFullRatio) {
                holder.tiTextTV.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            } else {
                holder.tiTextTV.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.ti_unselected));
            }
            return;
        }

        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (position == 0) {
            p.setMargins((int) (holder.itemView.getContext().getResources().getDisplayMetrics().density * 13 + 0.5f), 0, 0, 0);
        } else {
            p.setMargins(0, 0, 0, 0);
        }
        holder.itemView.requestLayout();

        holder.tiTextTV.setText(list.get(position).getString(holder.itemView.getContext()));
        if (TiPanelLayout.isFullRatio) {
            holder.tiTextTV.setTextColor(holder.itemView.getContext().getResources().getColorStateList(R.color.color_ti_selector_full));
            holder.tiImageIV.setImageDrawable(list.get(position).getFullImageDrawable(holder.itemView.getContext()));
        } else {
            holder.tiTextTV.setTextColor(holder.itemView.getContext().getResources().getColorStateList(R.color.color_ti_selector_not_full));
            holder.tiImageIV.setImageDrawable(list.get(position).getImageDrawable(holder.itemView.getContext()));

        }

        if (selectedPosition == position) {
            holder.tiTextTV.setSelected(true);
            holder.tiImageIV.setSelected(true);
        } else {
            holder.tiTextTV.setSelected(false);
            holder.tiImageIV.setSelected(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    selectedPosition = holder.getAdapterPosition();
                    TiSelectedPosition.POSITION_BEAUTY = selectedPosition;
                }

                switch (list.get(selectedPosition)) {
                    case WHITENING:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_WHITENING);
                        break;
                    case BLEMISH_REMOVAL:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_BLEMISH_REMOVAL);
                        break;
                    case LABEL_PRECISE_BEAUTY:
                        break;
                    case PRECISE_BEAUTY:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_PRECISE_BEAUTY);
                        break;
                    case TENDERNESS:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_TENDERNESS);
                        break;
                    case BRIGHTNESS:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_BRIGHTNESS);
                        break;
                    case SHARPNESS:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_SHARPNESS);
                        break;
                    case PRECISE_TENDERNESS:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_PRECISE_TENDERNESS);
                        break;
                    case DARK_CIRCLE:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_DARK_CIRCLE);
                        break;
                    case CROWS_FEET:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_CROWS_FEET);
                        break;
                    case NASOLABIAL_FOLD:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_NASOLABIAL_FOLD);
                        break;
                    case HIGH_LIGHT:
                        RxBus.get().post(RxBusAction.ACTION_SKIN_HIGH_LIGHT);
                        break;
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}