package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import cn.tillusory.tiui.adapter.TiFilterAdapter;
import cn.tillusory.tiui.custom.TiSharePreferences;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiBeautyFilter;
import com.hwangjr.rxbus.RxBus;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiFilterFragment extends LazyFragment {

    private final List<TiBeautyFilter> filterList = new ArrayList<>();

    private RecyclerView tiFilterRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        tiFilterRV = new RecyclerView(getContext());
        tiFilterRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(tiFilterRV);
        int selectPosition = TiSharePreferences.getInstance().getFilterSelectPosition();
        filterList.clear();
        filterList.addAll(Arrays.asList(TiBeautyFilter.values()));

        TiFilterAdapter filterAdapter = new TiFilterAdapter(filterList, selectPosition);
        tiFilterRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiFilterRV.setAdapter(filterAdapter);
        RxBus.get().post(RxBusAction.ACTION_FILTER_SELECTION, filterList.get(selectPosition).getFilterName());
    }

}
