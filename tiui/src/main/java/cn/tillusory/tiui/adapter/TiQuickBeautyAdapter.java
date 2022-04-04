package cn.tillusory.tiui.adapter;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.TiPanelLayout;
import cn.tillusory.tiui.custom.TiSharePreferences;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiQuickBeauty;
import com.hwangjr.rxbus.RxBus;
import java.util.List;

/**
 * Copyright (c) 2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiQuickBeautyAdapter extends RecyclerView.Adapter<TiDesViewHolder> {

    private final List<TiQuickBeauty> list;

    private int selectedPosition;

    public TiQuickBeautyAdapter(List<TiQuickBeauty> list, int selectedPosition) {
        this.list = list;
        this.selectedPosition = selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TiDesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_filter, parent, false);
        return new TiDesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TiDesViewHolder holder, int position) {
        if (position == 0) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            p.setMargins((int) (holder.itemView.getContext().getResources().getDisplayMetrics().density * 16 + 0.5f), 0, 0, 0);
            holder.itemView.requestLayout();
        } else {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            p.setMargins(0, 0, 0, 0);
            holder.itemView.requestLayout();
        }
        holder.tiTextTV.setText(list.get(position).getString(holder.itemView.getContext()));
        holder.tiImageIV.setImageDrawable(list.get(position).getImageDrawable(holder.itemView.getContext()));
        if (TiPanelLayout.isFullRatio) {
            holder.tiShadow.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.tiShadow.setBackgroundColor(Color.WHITE);
        }

        if (selectedPosition == position) {
            holder.tiTextTV.setSelected(true);
            holder.tiCover.setSelected(true);
        } else {
            holder.tiTextTV.setSelected(false);
            holder.tiCover.setSelected(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    selectedPosition = holder.getAdapterPosition();
                    TiSharePreferences.getInstance().putQuickBeautySelectionPosition(selectedPosition);
                }

                RxBus.get().post(RxBusAction.ACTION_QUICK_BEAUTY,
                    list.get(selectedPosition).getQuickBeautyVal());

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
