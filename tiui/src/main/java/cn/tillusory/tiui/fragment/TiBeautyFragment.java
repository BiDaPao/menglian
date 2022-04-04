package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import cn.tillusory.tiui.adapter.TiBeautyAdapter;
import cn.tillusory.tiui.model.TiBeauty;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiBeautyFragment extends LazyFragment {

    private List<TiBeauty> beautyList = new ArrayList<>();

    private RecyclerView tiBeautyRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        tiBeautyRV = new RecyclerView(getContext());
        tiBeautyRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(tiBeautyRV);
        beautyList.clear();
        beautyList.addAll(Arrays.asList(TiBeauty.values()));
        TiBeautyAdapter beautyAdapter = new TiBeautyAdapter(beautyList);
        tiBeautyRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiBeautyRV.setAdapter(beautyAdapter);
    }

}
