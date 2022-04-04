package cn.tillusory.tiui.adapter;

/**
 * @ClassName TiLipGlossAdapter
 * @Description TODO
 * @Author Spica2 7
 * @Date 2021/9/7 9:04
 */

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.TiPanelLayout;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiMakeupConfig;
import cn.tillusory.tiui.model.TiMakeupText;
import cn.tillusory.tiui.model.TiSelectedPosition;
import com.bumptech.glide.Glide;
import com.hwangjr.rxbus.RxBus;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 唇彩的适配器
 */

/**
 * Created by Anko on 2019-09-08.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiLipGlossAdapter extends RecyclerView.Adapter<TiMakeupItemViewHolder> {

  private int selectedPosition = TiSelectedPosition.POSITION_LIP_GLOSS;

  private List<TiMakeupConfig.TiMakeup> list;
  private List<TiMakeupText> textList;

  private Handler handler = new Handler();

  private Map<String, String> downloadingMakeups = new ConcurrentHashMap<>();

  public TiLipGlossAdapter(List<TiMakeupConfig.TiMakeup> list, List<TiMakeupText> textList) {
    this.list = list;
    this.textList = textList;

    DownloadDispatcher.setMaxParallelRunningCount(5);
  }

  public void setSelectedPosition(int selectedPosition) {
    this.selectedPosition = selectedPosition;
    TiSelectedPosition.POSITION_LIP_GLOSS = selectedPosition;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public TiMakeupItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_makeup, parent, false);
    return new TiMakeupItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final TiMakeupItemViewHolder holder, int position) {
    final TiMakeupConfig.TiMakeup tiMakeup = list.get(holder.getAdapterPosition());


    if (selectedPosition == position) {
      holder.itemView.setSelected(true);
    } else {
      holder.itemView.setSelected(false);
    }

    //显示封面
    if (tiMakeup == TiMakeupConfig.TiMakeup.NO_MAKEUP) {
      holder.thumbIV.setImageResource(R.drawable.ic_ti_none);
    } else {
      Glide.with(holder.itemView.getContext())
          .load(list.get(position).getThumb())
          .into(holder.thumbIV);
    }


    holder.nameTV.setText(textList.get(position).getString(holder.itemView.getContext()));
    if (TiPanelLayout.isFullRatio) {
      holder.nameTV.setTextColor(holder.itemView.getContext().getResources().getColorStateList(R.color.color_ti_selector_full));
    } else {
      holder.nameTV.setTextColor(holder.itemView.getContext().getResources().getColorStateList(R.color.color_ti_selector_not_full));
    }

    //唇彩无需下载
    holder.downloadIV.setVisibility(View.GONE);
    holder.loadingIV.setVisibility(View.GONE);
    holder.stopLoadingAnimation();



    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
          //切换选中背景
          int lastPosition = selectedPosition;
          selectedPosition = holder.getAdapterPosition();
          TiSelectedPosition.POSITION_LIP_GLOSS = selectedPosition;
          notifyItemChanged(selectedPosition);
          notifyItemChanged(lastPosition);
        }

        //如果已经下载了，则让美妆生效
        RxBus.get().post(RxBusAction.ACTION_LIP_GLOSS, list.get(selectedPosition).getName());
      }
    });
  }

  @Override
  public int getItemCount() {
    return list == null ? 0 : list.size();
  }
}
