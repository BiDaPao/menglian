package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cn.tillusory.tiui.adapter.TiMakeupAdapter;
import cn.tillusory.tiui.model.TiMakeupType;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anko on 2019-09-05.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiMakeupFragment extends LazyFragment {

    private List<TiMakeupType> makeupList = new ArrayList<>();

    RecyclerView tiMakeupRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        tiMakeupRV = new RecyclerView(getContext());
        tiMakeupRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(tiMakeupRV);
        makeupList.clear();
        makeupList.addAll(Arrays.asList(TiMakeupType.values()));
        TiMakeupAdapter makeupAdapter = new TiMakeupAdapter(makeupList);
        tiMakeupRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
        tiMakeupRV.setAdapter(makeupAdapter);
    }
}
