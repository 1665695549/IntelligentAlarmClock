package com.example.intelligentalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenBroadcastListener {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceive;
    public ScreenStateListener mListener;

    public ScreenBroadcastListener(Context context){
        mContext=context.getApplicationContext();
        mScreenReceive=new ScreenBroadcastReceiver();
    }

    public interface ScreenStateListener{
        void onScreenOn();
        void onScreenOff();
        void onScreenUserPresent();
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver{
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            LogInfo.d("onReceive start.ThreadID="+Thread.currentThread().getId());
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)){
                LogInfo.d("ACTION_SCREEN_ON");
                mListener.onScreenOn();
            }else if (Intent.ACTION_SCREEN_OFF.equals(action)){
                LogInfo.d("ACTION_SCREEN_OFF");
                mListener.onScreenOff();
            }else if (Intent.ACTION_USER_PRESENT.equals(action)){
                LogInfo.d("ACTION_USER_PRESENT");
                mListener.onScreenUserPresent();
            }
        }
    }

    public void registerListener(ScreenStateListener listener){
        mListener=listener;
        registerListener();
    }
    private void registerListener(){
        LogInfo.d("registerListener start");
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceive,filter);
    }

    public void unregisterListener(){
        LogInfo.d("unregisterListener start");
        if (mScreenReceive != null){
            mContext.unregisterReceiver(mScreenReceive);
        }
    }

}
