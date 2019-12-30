package com.example.intelligentalarmclock;

import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.intelligentalarmclock.db.Alarm;
import org.litepal.LitePal;

public class RingBellActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private TextView alarmText;
    private Button stopButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogInfo.d("onCreate start");
        setContentView(R.layout.ring_bell);
        alarmText=findViewById(R.id.alarm_text);
        stopButton=findViewById(R.id.stop_button);
        Intent intent=getIntent();
        final boolean isFromNotification=intent.getBooleanExtra("fromNotificatin",false);
        final int alarmID=intent.getIntExtra("AlarmID",0);
        int testID=intent.getIntExtra("AlarmID",0);
        LogInfo.d("alarmID="+alarmID+"; testID"+testID);
        if (alarmID!=0){
            Alarm alarm=LitePal.findAll(Alarm.class).get(0);
            if (alarm!=null){
                String title=alarm.getTitle()+" "+alarm.getAPm()+" "+alarm.getHour()+":"+alarm.getMinute();
                alarmText.setText(title);
            }
        }
        mediaPlayer=MediaPlayer.create(this, RingtoneManager.getActualDefaultRingtoneUri(RingBellActivity.this,RingtoneManager.TYPE_RINGTONE));
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("onClick start");
                if (true==isFromNotification){
                    LogInfo.d("from Notifiction. Cancel the notification");
                    NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(alarmID);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    //onDestroy();
                    finish();
                }else{
                    //return the action, when back to the AlarmActivity, so that AlarmActivity can refresh.
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    Intent intent=new Intent();
                    intent.putExtra("stop",true);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogInfo.d("onDestroy start");
    }
}
