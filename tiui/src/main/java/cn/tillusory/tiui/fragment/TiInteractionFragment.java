package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiInteractionAdapter;
import cn.tillusory.tiui.custom.TiConfigCallBack;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiInteractionConfig;
import cn.tillusory.tiui.model.TiSelectedPosition;
import com.hwangjr.rxbus.RxBus;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anko on 2020/4/27.
 * Copyright (c) 2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiInteractionFragment extends LazyFragment {

    private final List<TiInteractionConfig.TiInteraction> items = new ArrayList<>();
    private TiInteractionAdapter tiInteractionAdapter;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_ti_sticker);

        if (getContext() == null) return;
        items.clear();
        items.add(TiInteractionConfig.TiInteraction.NO_INTERACTION);

        TiInteractionConfig interactionList = TiConfigTools.getInstance().getInteractionList();

        if (interactionList != null && interactionList.getInteractions() != null && interactionList.getInteractions().size() != 0) {

            items.addAll(interactionList.getInteractions());
            initRecyclerView();
            RxBus.get().post(RxBusAction.ACTION_SHOW_INTERACTION, items.get(TiSelectedPosition.POSITION_INTERACTION).getHint());
        } else {

            TiConfigTools.getInstance().getInteractionConfig(new TiConfigCallBack<List<TiInteractionConfig.TiInteraction>>() {
                @Override public void success(List<TiInteractionConfig.TiInteraction> list) {
                    items.addAll(list);
                    initRecyclerView();
                    RxBus.get().post(RxBusAction.ACTION_SHOW_INTERACTION, items.get(TiSelectedPosition.POSITION_INTERACTION).getHint());
                }

                @Override public void fail(Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public void initRecyclerView() {
        RecyclerView tiInteractionRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        tiInteractionAdapter = new TiInteractionAdapter(items);
        tiInteractionRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        tiInteractionRV.setAdapter(tiInteractionAdapter);
        tiInteractionAdapter.notifyDataSetChanged();
    }

}
