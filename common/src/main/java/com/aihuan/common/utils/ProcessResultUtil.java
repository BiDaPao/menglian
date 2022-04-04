package com.aihuan.common.utils;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aihuan.common.fragment.ProcessFragment;
import com.aihuan.common.interfaces.ActivityResultCallback;
import com.aihuan.common.interfaces.CommonCallback;

/**
 * Created by cxf on 2018/9/29.
 */

public class ProcessResultUtil {

    protected ProcessFragment mFragment;


    public ProcessResultUtil(FragmentActivity activity) {
        mFragment = new ProcessFragment();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.add(mFragment, "ProcessFragment").commit();
    }

    public void requestPermissions(String[] permissions, CommonCallback<Boolean> commonCallback) {
        mFragment.requestPermissions(permissions, commonCallback);
    }


    public void startActivityForResult(Intent intent, ActivityResultCallback callback){
        mFragment.startActivityForResult(intent,callback);
    }


    public void release(){
        if(mFragment!=null){
            mFragment.release();
        }
    }

}
