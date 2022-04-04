package com.aihuan.doc.activity;

import android.view.View;

import com.aihuan.doc.R;

/**
 * Created by cxf on 2019/3/13.
 */

public class DocumentActivity extends AbsActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_document;
    }

    @Override
    protected void main() {
        setTitle("文档说明");
    }

    public void docClick(View v) {
        switch (v.getId()) {
            case R.id.btn_contents:
                ContentsActivity.forward(mContext);
                break;
        }
    }
}
