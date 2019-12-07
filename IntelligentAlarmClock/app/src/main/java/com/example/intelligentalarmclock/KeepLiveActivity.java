package com.example.intelligentalarmclock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class KeepLiveActivity extends AppCompatActivity {

    public static void actionToLiveActivity(Context pContext){
        LogInfo.d("actionToLiveActivity start");
        Intent intent = new Intent(pContext,KeepLiveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pContext.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogInfo.d("onCreate");
        setContentView(R.layout.keep_live_layout);

        Window window=getWindow();
        window.setGravity(Gravity.LEFT|Gravity.TOP);
        WindowManager.LayoutParams params=window.getAttributes();
        params.x=0;
        params.y=0;
        params.height=1;
        params.width=1;
        window.setAttributes(params);
        KeepManage.getInstance(this).setActivity(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogInfo.d("onDestroy start");
    }
}
