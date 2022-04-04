package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import cn.tillusory.tiui.adapter.TiFaceTrimAdapter;
import cn.tillusory.tiui.model.TiFaceTrim;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiFaceTrimFragment extends LazyFragment {

    private List<TiFaceTrim> faceTrimList = new ArrayList<>();

    private RecyclerView tiFaceTrimRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        tiFaceTrimRV = new RecyclerView(getContext());

        tiFaceTrimRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(tiFaceTrimRV);

        faceTrimList.clear();
        faceTrimList.addAll(Arrays.asList(TiFaceTrim.values()));

        TiFaceTrimAdapter faceTrimAdapter = new TiFaceTrimAdapter(faceTrimList);
        tiFaceTrimRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiFaceTrimRV.setAdapter(faceTrimAdapter);
    }

}
