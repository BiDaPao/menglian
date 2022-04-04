package cn.tillusory.tiui.adapter;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cn.tillusory.sdk.TiSDK;
import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.common.TiUtils;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.model.TiSelectedPosition;
import cn.tillusory.tiui.model.TiStickerConfig;
import com.bumptech.glide.Glide;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.listener.DownloadListener2;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anko on 2018/5/24.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiStickerAdapter extends RecyclerView.Adapter<TiStickerViewHolder> {

    private final int ITEM_TYPE_ONE = 1;

    private final int ITEM_TYPE_TWO = 2;

    private int selectedPosition = TiSelectedPosition.POSITION_STICKER;

    private List<TiStickerConfig.TiSticker> stickerList;

    private Handler handler = new Handler();

    private Map<String, String> downloadingStickers = new ConcurrentHashMap<>();

    public TiStickerAdapter(List<TiStickerConfig.TiSticker> stickerList) {
        this.stickerList = stickerList;
        DownloadDispatcher.setMaxParallelRunningCount(5);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_ONE;
        } else {
            return ITEM_TYPE_TWO;
        }
    }

    @NonNull
    @Override
    public TiStickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_TYPE_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_sticker_one, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_sticker, parent, false);
        }
        return new TiStickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TiStickerViewHolder holder, int position) {

        final TiStickerConfig.TiSticker tiSticker = stickerList.get(holder.getAdapterPosition());

        if (selectedPosition == position) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }

        //显示封面
        if (tiSticker == TiStickerConfig.TiSticker.NO_STICKER) {
            holder.thumbIV.setImageResource(R.drawable.ic_ti_none_sticker);
        } else {
            Glide.with(holder.itemView.getContext())
                .load(stickerList.get(position).getThumb())
                .into(holder.thumbIV);
        }

        //判断是否已经下载
        if (tiSticker.isDownloaded()) {
            holder.downloadIV.setVisibility(View.GONE);
            holder.loadingIV.setVisibility(View.GONE);
            holder.stopLoadingAnimation();
        } else {
            //判断是否正在下载，如果正在下载，则显示加载动画
            if (downloadingStickers.containsKey(tiSticker.getName())) {
                holder.downloadIV.setVisibility(View.GONE);
                holder.loadingIV.setVisibility(View.VISIBLE);
                holder.startLoadingAnimation();
            } else {
                holder.downloadIV.setVisibility(View.VISIBLE);
                holder.loadingIV.setVisibility(View.GONE);
                holder.stopLoadingAnimation();
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果没有下载，则开始下载到本地
                if (!tiSticker.isDownloaded()) {

                    //如果已经在下载了，则不操作
                    if (downloadingStickers.containsKey(tiSticker.getName())) {
                        return;
                    }

                    new DownloadTask.Builder(tiSticker.getUrl(), new File(TiSDK.getStickerPath(holder.itemView.getContext())))
                            .setMinIntervalMillisCallbackProcess(30)
                            .setConnectionCount(1)
                            .build()
                            .enqueue(new DownloadListener2() {
                                @Override
                                public void taskStart(@NonNull DownloadTask task) {
                                    downloadingStickers.put(tiSticker.getName(), tiSticker.getUrl());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                        }
                                    });
                                }

                                @Override
                                public void taskEnd(@NonNull final DownloadTask task, @NonNull EndCause cause, @Nullable final Exception realCause) {
                                    downloadingStickers.remove(tiSticker.getName());

                                    if (cause == EndCause.COMPLETED) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                File targetDir = new File(TiSDK.getStickerPath(holder.itemView.getContext()));
                                                File file = task.getFile();
                                                try {
                                                    //解压到贴纸目录
                                                    TiUtils.unzip(file, targetDir);
                                                    if (file != null) {
                                                        file.delete();
                                                    }

                                                    //修改内存与文件
                                                    tiSticker.setDownloaded(true);
                                                    tiSticker.downloaded();

                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            notifyDataSetChanged();
                                                        }
                                                    });

                                                } catch (Exception e) {
                                                    if (file != null) {
                                                        file.delete();
                                                    }
                                                }
                                            }
                                        }).start();

                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyDataSetChanged();
                                                if (realCause != null) {
                                                    Toast.makeText(holder.itemView.getContext(), realCause.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                } else {
                    if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                        return;
                    }

                    //如果已经下载了，则让贴纸生效
                   TiSDKManager.getInstance().setSticker(tiSticker.getName());

                    //切换选中背景
                    int lastPosition = selectedPosition;
                    selectedPosition = holder.getAdapterPosition();
                    TiSelectedPosition.POSITION_STICKER = selectedPosition;
                    notifyItemChanged(selectedPosition);
                    notifyItemChanged(lastPosition);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return stickerList == null ? 0 : stickerList.size();
    }
}
