package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiPortraitAdapter;
import cn.tillusory.tiui.custom.TiConfigCallBack;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.model.TiPortraitConfig;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiPortraitFragment extends LazyFragment {

    private List<TiPortraitConfig.TiPortrait> items = new ArrayList<>();




    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_ti_sticker);

        if (getContext() == null) return;

        items.clear();
        items.add(TiPortraitConfig.TiPortrait.NO_Portrait);

        TiPortraitConfig portraitConfig = TiConfigTools.getInstance().getPortraitList();
        if (portraitConfig != null) {
            items.addAll(portraitConfig.getPortraits());
            initRecyclerView();
        } else {
            TiConfigTools.getInstance().getPortraitConfig(new TiConfigCallBack<List<TiPortraitConfig.TiPortrait>>() {
                @Override public void success(List<TiPortraitConfig.TiPortrait> list) {
                    items.addAll(list);
                    initRecyclerView();
                }

                @Override public void fail(Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initRecyclerView() {
        TiPortraitAdapter adapter = new TiPortraitAdapter(items);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tiRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
