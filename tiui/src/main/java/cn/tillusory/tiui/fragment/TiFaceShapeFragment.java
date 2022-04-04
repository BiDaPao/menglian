package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import cn.tillusory.tiui.adapter.TiFaceShapeAdapter;
import cn.tillusory.tiui.custom.TiSharePreferences;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiFaceShape;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiFaceShapeFragment extends LazyFragment {

    private TiFaceShapeAdapter adapter;
    private List<TiFaceShape> list = new ArrayList<>();

    private RecyclerView tiFaceShapeRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        RxBus.get().register(this);

        tiFaceShapeRV = new RecyclerView(getContext());

        tiFaceShapeRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //R.layout.fragment_ti_recyclerview
        setContentView(tiFaceShapeRV);

        list.clear();
        list.addAll(Arrays.asList(TiFaceShape.values()));

        adapter = new TiFaceShapeAdapter(list, TiSharePreferences.getInstance().getFaceShapePosition());
        tiFaceShapeRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiFaceShapeRV.setAdapter(adapter);
        RxBus.get().post(RxBusAction.ACTION_FACE_SHAPE, list.get(TiSharePreferences.getInstance().getFaceShapePosition()));
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();

        RxBus.get().unregister(this);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = @Tag(RxBusAction.ACTION_RESET_BEAUTY))
    public void notifyChanged(Boolean bool) {
        if (adapter != null) {
            TiSharePreferences.getInstance().putFaceShapePosition(0);
            adapter.setSelectedPosition(0);
        }
    }


}


