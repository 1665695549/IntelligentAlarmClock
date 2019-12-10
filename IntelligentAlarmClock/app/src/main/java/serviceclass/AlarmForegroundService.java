package serviceclass;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.intelligentalarmclock.LogInfo;

/**
 *8.0以上版本的AlarmManager只能通过前台服务启动app
 */
public class AlarmForegroundService extends IntentService {

    public AlarmForegroundService() {
        super("AlarmForegroundService");
    }

    /**
     *启动前台服务后，再启动AlarmJobIntentService，进行天气和闹钟是否重复的处理
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        LogInfo.d("AlarmForegroundService onHandleIntent start.ThreadID="+Thread.currentThread().getId());
        if (intent != null) {
            int alarmID=intent.getIntExtra("alarmID",0);
            LogInfo.d("alarmID="+alarmID);

            if (0==alarmID){
                LogInfo.d("alarmID is 0, wrong");
                stopSelf();
            }else{
                createNotificationChanel("alarm");
                Notification notification=new NotificationCompat.Builder(this,"alarm")
                        .build();
                startForeground(1,notification);
                LogInfo.d("the version is up 8.0");
                Intent intent1=new Intent(AlarmForegroundService.this, AlarmJobIntentService.class);
                intent1.putExtra("alarmID",alarmID);
                AlarmJobIntentService.enqueueWork(AlarmForegroundService.this,intent1);
                LogInfo.d("the option end");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogInfo.d("AlarmForegroundService onDestroy start.Thread="+Thread.currentThread().getId());
    }

    private void createNotificationChanel( String chanelID){
        LogInfo.d("createNotificationChanel start");
        //创建通知渠道的代码只在第一次执行的时候才会创建，以后每次执行创建代码系统会检测到该通知渠道已经存在了，因此不会重复创建
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelName="闹钟";
            String description="允许闹钟响应";
            int importance= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel(chanelID,channelName,importance);
            channel.setDescription(description);
            channel.canBypassDnd(); //发布到此频道的通知是否可以绕过“请勿打扰”
            channel.setBypassDnd(true);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
