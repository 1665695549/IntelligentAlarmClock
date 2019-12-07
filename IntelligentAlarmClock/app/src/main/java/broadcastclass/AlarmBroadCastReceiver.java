package broadcastclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.intelligentalarmclock.LogInfo;
import serviceclass.AlarmJobIntentService;
import serviceclass.ReceiveNotifyService;

public class AlarmBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        LogInfo.d("boot receiver start");
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            LogInfo.d("the version is up 8.0");
            Intent intent1=new Intent(context, AlarmJobIntentService.class);
            intent1.putExtra("isBootStart",true);
            AlarmJobIntentService.enqueueWork(context,intent1);
        }else{
        LogInfo.d("the version is bellow 8.0");
        Intent intent1=new Intent(context,ReceiveNotifyService.class);
        intent1.putExtra("isBootStart",true);
        context.startService(intent1);
        }
        }
}
