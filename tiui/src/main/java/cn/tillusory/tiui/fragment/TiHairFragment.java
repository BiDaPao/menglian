package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import cn.tillusory.tiui.adapter.TiHairAdapter;
import cn.tillusory.tiui.model.TiHair;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiHairFragment extends LazyFragment {

    private List<TiHair> hairList = new ArrayList<>();

    private RecyclerView tiHairRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        tiHairRV = new RecyclerView(getContext());
        tiHairRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(tiHairRV);
        hairList.clear();
        hairList.addAll(Arrays.asList(TiHair.values()));
        TiHairAdapter hairAdapter = new TiHairAdapter(hairList);
        tiHairRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiHairRV.setAdapter(hairAdapter);
    }

}
