package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import cn.tillusory.tiui.adapter.TiQuickBeautyAdapter;
import cn.tillusory.tiui.custom.TiSharePreferences;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiQuickBeauty;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiQuickBeautyFragment extends LazyFragment {

    private TiQuickBeautyAdapter adapter;
    private final List<TiQuickBeauty> quickBeautyList = new ArrayList<>();

    private RecyclerView tiQuickBeautyRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        int selectedPosition = TiSharePreferences.getInstance().getQuickBeautySelectionPosition();
        tiQuickBeautyRV = new RecyclerView(getContext());
        tiQuickBeautyRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        RxBus.get().register(this);
        setContentView(tiQuickBeautyRV);
        quickBeautyList.clear();
        quickBeautyList.addAll(Arrays.asList(TiQuickBeauty.values()));
        adapter = new TiQuickBeautyAdapter(quickBeautyList, selectedPosition);
        tiQuickBeautyRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiQuickBeautyRV.setAdapter(adapter);
        RxBus.get().post(RxBusAction.ACTION_QUICK_BEAUTY_SELECTION, quickBeautyList.get(selectedPosition).getQuickBeautyVal());
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();

        RxBus.get().unregister(this);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = @Tag(RxBusAction.ACTION_RESET_BEAUTY))
    public void notifyChanged(Boolean bool) {
        if (adapter != null) {
            adapter.setSelectedPosition(0);
        }
    }


}


