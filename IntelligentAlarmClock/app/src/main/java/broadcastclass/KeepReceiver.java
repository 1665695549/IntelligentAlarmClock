package broadcastclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class KeepReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        String action=intent.getAction();
        if (TextUtils.equals(action,Intent.ACTION_SCREEN_OFF)){

        }else if (TextUtils.equals(action,Intent.ACTION_SCREEN_ON)){

        }
    }
}
