package com.example.intelligentalarmclock;

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
        int alarmID=intent.getIntExtra("AlarmID",0);
        LogInfo.d("alarmID="+alarmID);
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
                mediaPlayer.stop();
                mediaPlayer.release();
                Intent intent=new Intent();
                intent.putExtra("stop",true);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
