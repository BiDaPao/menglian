package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiGiftAdapter;
import cn.tillusory.tiui.custom.TiConfigCallBack;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.model.TiGiftConfig;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiGiftFragment extends LazyFragment {

    private final List<TiGiftConfig.TiGift> items = new ArrayList<>();


    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_ti_sticker);

        if (getContext() == null) return;

        items.clear();
        items.add(TiGiftConfig.TiGift.NO_GIFT);

        TiGiftConfig gifList = TiConfigTools.getInstance().getGifList();
        if (gifList != null && gifList.getGifts() != null && gifList.getGifts().size() != 0) {
            items.addAll(gifList.getGifts());
            initRecycleView();
        } else {
            TiConfigTools.getInstance().getGiftsConfig(new TiConfigCallBack<List<TiGiftConfig.TiGift>>() {
                @Override public void success(List<TiGiftConfig.TiGift> list) {
                    items.addAll(list);
                    initRecycleView();
                }

                @Override public void fail(Exception error) {
                    error.printStackTrace();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void initRecycleView() {

        RecyclerView tiGiftRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        TiGiftAdapter tiGiftAdapter = new TiGiftAdapter(items);
        tiGiftRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        tiGiftRV.setAdapter(tiGiftAdapter);

        tiGiftAdapter.notifyDataSetChanged();
    }


}
