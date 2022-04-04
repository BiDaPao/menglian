package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiGreenScreenAdapter;
import cn.tillusory.tiui.custom.TiConfigCallBack;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.model.TiGreenScreenConfig;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anko on 2020/3/25.
 * Copyright (c) 2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiGreenScreenFragment extends LazyFragment {

    private final List<TiGreenScreenConfig.TiGreenScreen> items = new ArrayList<>();




    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_ti_sticker);

        if (getContext() == null) return;

        items.clear();
        items.add(TiGreenScreenConfig.TiGreenScreen.NO_GreenScreen);
        items.add(new TiGreenScreenConfig.TiGreenScreen(TiGreenScreenAdapter.EDIT_GREEN_SCREEN, "", true));

        TiGreenScreenConfig greenScreenList = TiConfigTools.getInstance().getGreenScreenList();

        if (greenScreenList != null && greenScreenList.getGreenScreens() != null && greenScreenList.getGreenScreens().size() != 0) {
            items.addAll(greenScreenList.getGreenScreens());
            initRecyclerView();
        } else {
            TiConfigTools.getInstance().getGreenScreenConfig(new TiConfigCallBack<List<TiGreenScreenConfig.TiGreenScreen>>() {
                @Override public void success(List<TiGreenScreenConfig.TiGreenScreen> list) {
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
        RecyclerView tiGreenScreenRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        TiGreenScreenAdapter greenScreenAdapter = new TiGreenScreenAdapter(items);
        tiGreenScreenRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        tiGreenScreenRV.setAdapter(greenScreenAdapter);
    }
}
