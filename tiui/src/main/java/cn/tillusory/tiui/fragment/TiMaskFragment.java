package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiMaskAdapter;
import cn.tillusory.tiui.custom.TiConfigCallBack;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.model.TiMaskConfig;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anko on 2019-07-12.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiMaskFragment extends LazyFragment {

    private final List<TiMaskConfig.TiMask> items = new ArrayList<>();
    private TiMaskAdapter tiMaskAdapter;


    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_ti_sticker);

        if (getContext() == null) return;

        items.clear();
        items.add(TiMaskConfig.TiMask.NO_MASK);
        TiMaskConfig masks = TiConfigTools.getInstance().getMaskList();

        if (masks != null && masks.getMasks().size() != 0) {

            items.addAll(masks.getMasks());
            initRecyclerView();
        } else {
            //从缓存文件中读取配置文件
            TiConfigTools.getInstance().getMaskConfig(new TiConfigCallBack<List<TiMaskConfig.TiMask>>() {
                @Override public void success(List<TiMaskConfig.TiMask> list) {
                    items.addAll(list);
                    initRecyclerView();
                }

                @Override public void fail(Exception error) {
                    error.printStackTrace();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initRecyclerView() {
        RecyclerView tiMaskRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        tiMaskAdapter = new TiMaskAdapter(items);
        tiMaskRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        tiMaskRV.setAdapter(tiMaskAdapter);
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
    }
}
