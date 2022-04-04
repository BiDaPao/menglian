package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cn.tillusory.tiui.adapter.TiDistortionAdapter;
import cn.tillusory.tiui.model.TiDistortion;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiDistortionFragment extends LazyFragment {

    private List<TiDistortion> distortionList = new ArrayList<>();

    private RecyclerView tiDistortionRV;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        tiDistortionRV = new RecyclerView(getContext());
        tiDistortionRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(tiDistortionRV);

        distortionList.clear();
        distortionList.addAll(Arrays.asList(TiDistortion.values()));

        TiDistortionAdapter distortionAdapter = new TiDistortionAdapter(distortionList);
        tiDistortionRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiDistortionRV.setAdapter(distortionAdapter);
    }
}
