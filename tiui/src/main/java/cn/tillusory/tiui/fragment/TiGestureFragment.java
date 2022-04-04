package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiGestureAdapter;
import cn.tillusory.tiui.custom.TiConfigCallBack;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiGestureConfig;
import cn.tillusory.tiui.model.TiSelectedPosition;
import com.hwangjr.rxbus.RxBus;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anko.
 * Copyright (c) 2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiGestureFragment extends LazyFragment {

    private final List<TiGestureConfig.TiGesture> items = new ArrayList<>();

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_ti_sticker);

        if (getContext() == null) return;

        items.clear();
        items.add(TiGestureConfig.TiGesture.NO_Gesture);

        TiGestureConfig gestureList = TiConfigTools.getInstance().getGestureList();

        if (gestureList != null && gestureList.getGestures() != null && gestureList.getGestures().size() != 0) {
            items.addAll(gestureList.getGestures());
            initRecyclerView();
        } else {
            TiConfigTools.getInstance().getGestureConfig(new TiConfigCallBack<List<TiGestureConfig.TiGesture>>() {
                @Override public void success(List<TiGestureConfig.TiGesture> list) {
                    items.addAll(list);
                    initRecyclerView();
                }

                @Override public void fail(Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        RxBus.get().post(RxBusAction.ACTION_SHOW_GESTURE, items.get(TiSelectedPosition.POSITION_GESTURE).getHint());
    }

    public void initRecyclerView() {
        RecyclerView gestureRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        TiGestureAdapter tiGestureAdapter = new TiGestureAdapter(items);
        gestureRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        gestureRV.setAdapter(tiGestureAdapter);
        tiGestureAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
    }
}


