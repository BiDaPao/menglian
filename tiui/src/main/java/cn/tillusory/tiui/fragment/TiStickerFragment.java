package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiStickerAdapter;
import cn.tillusory.tiui.custom.TiConfigCallBack;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.model.TiStickerConfig;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiStickerFragment extends LazyFragment {

    private final List<TiStickerConfig.TiSticker> items = new ArrayList<>();



    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_ti_sticker);

        if (getContext() == null) return;

        items.clear();
        items.add(TiStickerConfig.TiSticker.NO_STICKER);

        TiStickerConfig stickerList = TiConfigTools.getInstance().getStickerConfig();
        if (stickerList == null) {
            TiConfigTools.getInstance().getStickersConfig(new TiConfigCallBack<List<TiStickerConfig.TiSticker>>() {
                @Override public void success(List<TiStickerConfig.TiSticker> list) {
                    items.addAll(list);
                    initRecyclerView();
                }

                @Override public void fail(Exception error) {
                    error.printStackTrace();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            items.addAll(stickerList.getStickers());
            initRecyclerView();
        }

    }

    public void initRecyclerView() {
        RecyclerView tiStickerRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        TiStickerAdapter tiStickerAdapter = new TiStickerAdapter(items);
        tiStickerRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        tiStickerRV.setAdapter(tiStickerAdapter);
        tiStickerAdapter.notifyDataSetChanged();

    }

}
