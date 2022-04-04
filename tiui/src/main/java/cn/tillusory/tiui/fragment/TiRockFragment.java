package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import cn.tillusory.tiui.adapter.TiRockAdapter;
import cn.tillusory.tiui.model.TiRock;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiRockFragment extends LazyFragment {

    private List<TiRock> rockList = new ArrayList<>();


    private RecyclerView tiRockRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        tiRockRV = new RecyclerView(getContext());

        tiRockRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(tiRockRV);

        rockList.clear();
        rockList.addAll(Arrays.asList(TiRock.values()));

        TiRockAdapter rockAdapter = new TiRockAdapter(rockList);
        tiRockRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiRockRV.setAdapter(rockAdapter);
    }
}
