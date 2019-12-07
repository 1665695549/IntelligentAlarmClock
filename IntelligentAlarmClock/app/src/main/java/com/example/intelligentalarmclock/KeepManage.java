package com.example.intelligentalarmclock;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import java.lang.ref.WeakReference;

public class KeepManage {
    private Context mContext;
    private static WeakReference<Activity> mKeepAct;
    public static KeepManage ourInstance;

    public static KeepManage getInstance(Context pContext){
        if (ourInstance==null){
            ourInstance=new KeepManage(pContext.getApplicationContext());
        }
        return ourInstance;
    }
    public KeepManage(Context pContext){
        this.mContext=pContext;
    }

    public void setActivity(Activity pActivity){
        mKeepAct=new WeakReference<Activity>(pActivity);
    }

    public void startActivity(){
       KeepLiveActivity.actionToLiveActivity(mContext);
    }

    public void finishActivity(){
        LogInfo.d("finishActivity");
        if (mKeepAct != null){
            Activity activity=mKeepAct.get();
            if (activity !=null){
                activity.finish();
            }
        }
    }

    public void stopRing(int alarmID){
        LogInfo.d("stopRing start");
        NotificationManager notificationManager=(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(alarmID);
    }

}