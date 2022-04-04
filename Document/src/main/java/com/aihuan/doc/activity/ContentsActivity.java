package com.aihuan.doc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.aihuan.doc.R;
import com.aihuan.doc.adapter.ContentsAdapter;
import com.aihuan.doc.bean.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by cxf on 2019/3/13.
 * 目录结构
 */

public class ContentsActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ContentsActivity.class));
    }

    private RecyclerView mRecyclerView;
    private Handler mHandler;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contents;
    }

    @Override
    protected void main() {
        setTitle("代码目录结构文档");

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj != null) {
                    List<Node> list = (List<Node>) msg.obj;
                    if (mRecyclerView != null) {
                        ContentsAdapter adapter = new ContentsAdapter(mContext);
                        adapter.setData(list);
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Node> list = getNodeList();
                if (list != null && mHandler != null) {
                    Message msg = Message.obtain();
                    msg.obj = list;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private List<Node> getNodeList() {
        List<Node> list = null;
        BufferedReader br = null;
        try {
            InputStream inputStream = mContext.getResources().getAssets().open("contents.json");
            br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String content = sb.toString();
            if (!TextUtils.isEmpty(content)) {
                list = JSON.parseArray(content, Node.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        super.onDestroy();
    }
}
