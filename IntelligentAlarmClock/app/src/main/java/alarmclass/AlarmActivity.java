package alarmclass;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.intelligentalarmclock.KeepManage;
import com.example.intelligentalarmclock.LogInfo;
import com.example.intelligentalarmclock.R;
import com.example.intelligentalarmclock.RerecyclerView;
import com.example.intelligentalarmclock.ScreenBroadcastListener;
import com.example.intelligentalarmclock.db.Alarm;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import serviceclass.AlarmForegroundService;
import serviceclass.AlarmJobIntentService;
import serviceclass.ReceiveNotifyService;

public class AlarmActivity extends AppCompatActivity {

    private ScreenBroadcastListener mlistener;
    private static boolean isServiceKeepLive=false;
    protected static int mSelectedAlarmID=0;
    public static AlarmItemLayout current=null;
    public static boolean isDeleteIconShown=false;
    public static RerecyclerView rerecyclerView;
    private TextView noAlarmNotify;
    private List<Alarm> alarmItemList=new ArrayList<>();
    AlarmAdapter alarmAdapter;
    private final static int CREATE=0;
    private final static int EDIT=1;

    /**
     *初始化，显示设置的所有闹钟信息
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogInfo.d("AlarmActivity onCreate start.Thread="+Thread.currentThread().getId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_info);
        Button addAlarm=findViewById(R.id.add_alarm);
        Button back=findViewById(R.id.back_weather);
        noAlarmNotify=findViewById(R.id.no_alarm_notify);
        rerecyclerView = findViewById(R.id.recycler_view);
        alarmItemList= LitePal.findAll(Alarm.class);
        if (0==alarmItemList.size()){
            noAlarmNotify.setVisibility(View.VISIBLE);
        }else{
            noAlarmNotify.setVisibility(View.INVISIBLE);
        }
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        rerecyclerView.setLayoutManager(layoutManager); // 确定rerecyclerView的滑动方向，一定要初始化

        alarmAdapter=new AlarmAdapter(alarmItemList);
        rerecyclerView.setAdapter(alarmAdapter);

        //返回上一个活动
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

         //跳转到创建闹钟页面
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("addAlarm click.Thread="+Thread.currentThread().getId());
                Intent intent=new Intent(AlarmActivity.this, CreateAlarmActivity.class);
                intent.putExtra("item_flag",0);
                startActivityForResult(intent,CREATE);

            }
        });

        //如果正在响铃时，且在AlarmActivity页面，则打开锁屏时，关闭闹钟
        final KeepManage keepManage=KeepManage.getInstance(AlarmActivity.this);
        mlistener=new ScreenBroadcastListener(this);
        mlistener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
            }

            @Override
            public void onScreenOff() {

            }

            @Override
            public void onScreenUserPresent() {
                LogInfo.d("onScreenUserPresent start.ThreadID="+Thread.currentThread().getId());
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    int ringID= AlarmForegroundService.getRingingAlarmID();
                    if (0!=ringID)
                    {
                        keepManage.stopRing(ringID);
                        AlarmForegroundService.resetRingingAlarmID();
                    }
                }else{
                    int ringID=ReceiveNotifyService.getRingingAlarmID();
                    if (0!=ringID)
                    {
                        keepManage.stopRing(ringID);
                        ReceiveNotifyService.resetRingingAlarmID();
                    }
                }

            }
        });

        //第一次打开时提示用户，打开自动运行权限
        SharedPreferences shared= getSharedPreferences("is", MODE_PRIVATE);
        boolean isfer=shared.getBoolean("isfer", true);
        SharedPreferences.Editor editor = shared.edit();
        if (isfer){
            LogInfo.d("第一次打开app,弹出提示");
            final AlertDialog.Builder dialog=new AlertDialog.Builder(AlarmActivity.this);
            dialog.setTitle("友情提示");
            dialog.setMessage("请确保在设置或智能管理器中打开app的“自启动权限”，否则闹钟将无效");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialog.show();
            editor.putBoolean("isfer",false);
            editor.commit();
        }
    }

    /**
     *由其他页面返回到此页面时，初始化变量状态.在此函数调用refreshList，而不是在onActivityResult里调用的原因是，当从响铃页面点击停止闹钟回到此页面时，需要刷新页面
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        LogInfo.d("onPostResume start.Thread="+Thread.currentThread().getId());
        refreshList();
    }

    /**
     * 刷新闹钟列表,此处不使用rerecyclerView的notify方法，而是使用rerecyclerView.setAdapter(alarmAdapter)直接重构，是因为rerecyclerView里的布局自定义了，
     * 使用rerecyclerView的notify方法，会有很多错误，比如onBindViewHolder方法并不重新执行等
     */
    private void refreshList(){
        LogInfo.d("refreshList start");
        isDeleteIconShown=false;
        alarmItemList.clear();
        alarmItemList.addAll(LitePal.findAll(Alarm.class));
        if (0==alarmItemList.size()){
            noAlarmNotify.setVisibility(View.VISIBLE);
        }else{
            noAlarmNotify.setVisibility(View.INVISIBLE);
        }
        rerecyclerView.setAdapter(alarmAdapter);
    }


    /**
     *     删除指定闹钟,alarmID从1起
     */
    public void deleteAlarm(int alarmID){
        LogInfo.d("deleteAlarm start alarmID="+alarmID+".ThreadID="+Thread.currentThread().getId());
        LitePal.deleteAll(Alarm.class,"alarmID=?",String.valueOf(alarmID));
        List<Alarm> alarmList=LitePal.where("alarmID=?",String.valueOf(alarmID)).find(Alarm.class);
        refreshList();
        cancelNotify(alarmID);
    }

    /**
     *以编辑的方式打开，CreateAlarmActivity页面
     *startActivityForResult的第二个参数是请求码，用于在之后的回调中判断数据的来源（需要时唯一值）
     */
    public void startEditAlarmActivity(int alarmID){
        LogInfo.d("startEditAlarmActivity start alarmID="+String.valueOf(alarmID)+".ThreadID="+Thread.currentThread().getId());
        Intent intent=new Intent(AlarmActivity.this, CreateAlarmActivity.class);
        intent.putExtra("item_flag",alarmID);
        startActivityForResult(intent,EDIT);
    }

    /**
     * 说明：当前用startActivityForResult()打开的页面关闭时的回调函数，通过此函数判断是否创建新闹钟，或删除、修改已存在的闹钟，若有则刷新list，并创建或修改通知
     * 参数：requestCode：请求码（startActivityForResult函数传入）
     * 参数：resultCode：返回数据时传入的处理结果
     * 参数：data：携带着返回数据的Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogInfo.d("AlarmActivity onActivityResult start.ThreadID="+Thread.currentThread().getId());
        switch (requestCode){
            case CREATE:
                LogInfo.d("onActivityResult CREATE");
                if (resultCode==RESULT_OK){
                     int ID=data.getIntExtra("alarmID",1000);
                    if (0!=ID && 1000!=ID){
                        createNotify(ID);
                    }else{
                        //do nothing
                    }
                }
                break;
            case EDIT:
                LogInfo.d("onActivityResult EDIT");
                if (resultCode==RESULT_OK){
                    int ID=data.getIntExtra("alarmID",1000);
                    if (0!=ID && 1000!=ID){
                        Alarm alarm=LitePal.where("alarmID=?",String.valueOf(ID)).find(Alarm.class).get(0);
                        alarm.setVality(true);
                        alarm.save();
                        createNotify(ID);
                    }else{
                        //do nothing
                    }
                }
                break;
        }
    }
    /**
     * 说明：取消通知
     * 参数：alarmID：特定的闹钟ID
     */
    public void cancelNotify(int alarmID){
        LogInfo.d("cancelNotify start.ThreadID="+Thread.currentThread().getId());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            LogInfo.d("Version under KITKAT");
            Intent intent=new Intent();
            intent.setAction("START_NOTIFY_BROADCAST");
            intent.putExtra("alarmID",alarmID);
            PendingIntent pendingIntent=PendingIntent.getService(AlarmActivity.this,alarmID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        else if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT  && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            LogInfo.d("Version between KITKAT and M ");
            Intent intent=new Intent();
            intent.setAction("START_NOTIFY_BROADCAST");
            intent.putExtra("alarmID",alarmID);
            PendingIntent pendingIntent=PendingIntent.getService(AlarmActivity.this,alarmID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            LogInfo.d("Version up M");
            Intent intent=new Intent();
            intent.setAction("START_NOTIFY_BROADCAST");
            intent.putExtra("alarmID",alarmID);
            PendingIntent pendingIntent=PendingIntent.getService(AlarmActivity.this,alarmID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent intent=new Intent(AlarmActivity.this, AlarmForegroundService.class);
            intent.putExtra("alarmID",alarmID);
            PendingIntent pendingIntent=PendingIntent.getForegroundService(AlarmActivity.this,alarmID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * 说明：创建通知
     * 参数：alarmID：特定的闹钟ID
     */
    public void createNotify(int alarmID){
        LogInfo.d("createNotify start.ThreadID="+Thread.currentThread().getId());
        LogInfo.d("createNotify alarmID="+alarmID);
        List<Alarm> alarmList=LitePal.where("alarmID=?",String.valueOf(alarmID)).find(Alarm.class);
        if (alarmList.size()!=0){
            Calendar c =getNotifyTime(alarmID);
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-hh-mm aaa");
            Date date;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                LogInfo.d("Version under KITKAT");
                Intent intent=new Intent();
                intent.setAction("START_NOTIFY_BROADCAST");
                intent.putExtra("alarmID",alarmID);
                PendingIntent pendingIntent=PendingIntent.getService(AlarmActivity.this,alarmID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                date=new Date(c.getTimeInMillis());
                LogInfo.d(format.format(date));
                alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
            }
            else if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT  && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                LogInfo.d("Version between KITKAT and M ");
                Intent intent=new Intent();
                intent.setAction("START_NOTIFY_BROADCAST");
                intent.putExtra("alarmID",alarmID);
                PendingIntent pendingIntent=PendingIntent.getService(AlarmActivity.this,alarmID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                LogInfo.d(String.valueOf(c.getTimeInMillis()));
                date=new Date(c.getTimeInMillis());
                LogInfo.d(format.format(date));
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                LogInfo.d("Version up M");
                Intent intent=new Intent();
                intent.setAction("START_NOTIFY_BROADCAST");
                intent.putExtra("alarmID",alarmID);
                PendingIntent pendingIntent=PendingIntent.getService(AlarmActivity.this,alarmID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                date=new Date(c.getTimeInMillis());
                LogInfo.d(format.format(date));
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),pendingIntent),pendingIntent);

            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Intent intent=new Intent(AlarmActivity.this, AlarmForegroundService.class);
                intent.putExtra("alarmID",alarmID);
                PendingIntent pendingIntent=PendingIntent.getForegroundService(AlarmActivity.this,alarmID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                LogInfo.d(String.valueOf(c.getTimeInMillis()));
                date=new Date(c.getTimeInMillis());
                LogInfo.d(format.format(date));
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),pendingIntent),pendingIntent);
            }
        }
    }
    /**
     * 说明：保存当前选中的闹钟的ID,当从编辑页面修改某个闹钟后返回到此页面时，需要根据此ID修改相应的通知
     * 参数：selectedAlarmID：特定的闹钟ID
     */
    protected static void setSelectedAlarmID(int selectedAlarmID){
        LogInfo.d("setSelectedAlarmID start ");
        mSelectedAlarmID=selectedAlarmID;
        LogInfo.d("selectedAlarmID="+selectedAlarmID);
    }

    /**
     * 说明：获取某个闹钟的时间
     * 参数：alarmID：请求的闹钟id
     * 返回：Calendar：闹钟的时间
     */
    private Calendar getNotifyTime(int alarmID) {
        LogInfo.d("getNotifytime start.Thread="+Thread.currentThread().getId());
        Calendar c = Calendar.getInstance();
        long currrentTime = c.getTimeInMillis();
        int mWay = c.get(Calendar.DAY_OF_WEEK);
        LogInfo.d("current mMay="+mWay);
        //print time info
        Date date = new Date(c.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm aaa");
        LogInfo.d(format.format(date));

        List<Alarm> alarmList = LitePal.where("alarmID=?", String.valueOf(alarmID)).find(Alarm.class);
        Alarm alarm=alarmList.get(0);
        String APmString = alarm.getAPm();
        boolean status = APmString.contains("上");
        int hour = alarm.getHour();
        int minute = Integer.parseInt(alarm.getMinute());
        if (status == true) {
            //LogInfo.d("AM");
            if (hour == 12) {
                hour = 0;
            }
        } else {
            LogInfo.d("PM");
            if (hour != 12) {
                hour = hour + 12;
            }
        }
        c.set(Calendar.HOUR_OF_DAY, hour);//Calendar.HOUR-12小时制，Calendar.HOUR_OF_DAY-24小时制
        c.set(Calendar.MINUTE, minute);
        date = new Date(c.getTimeInMillis());
        LogInfo.d(format.format(date));
        String repeate = alarm.getRepeate();

        String currentDay=" ";

        if (1==mWay){
            LogInfo.d("当前是周天");
            currentDay="天";
        }else if (2==mWay){
            LogInfo.d("当前是周一");
            currentDay="一";
        }else if (3==mWay){
            LogInfo.d("当前是周二");
            currentDay="二";
        }else if (4==mWay){
            LogInfo.d("当前是周三");
            currentDay="三";
        }else if (5==mWay){
            LogInfo.d("当前是周四");
            currentDay="四";
        }else if (6==mWay){
            LogInfo.d("当前是周五");
            currentDay="五";
        }else if (7==mWay){
            LogInfo.d("当前是周六");
            currentDay="六";
        }
        boolean isRingToday=false;
        //设置时间在当前时间之后，则要多判断是不是今天响铃
        if (currrentTime < c.getTimeInMillis()){
            LogInfo.d("currrentTime < c.getTimeInMillis()");
            if (repeate.indexOf("工作日") != -1){
                LogInfo.d("工作日");
                if (currentDay.equals("六") || currentDay.equals("天")){
                    //do nothong
                }else {
                    isRingToday=true;
                }
            }else if (repeate.indexOf("周末") != -1){
                LogInfo.d("周末");
                if (currentDay.equals("六") || currentDay.equals("天")){
                    isRingToday=true;
                }else {
                    //do nothong
                }
            }else if (repeate.indexOf("每天") != -1){
                LogInfo.d("每天");
                isRingToday=true;
            } else if (repeate.indexOf("不") != -1) {
                isRingToday = true;
            } else if (repeate.indexOf(currentDay) != -1) {
                isRingToday = true;
            }
        }

        if (false==isRingToday){
            LogInfo.d("ring bell another day");
            //当设置为不重复或每天重复，则通知设置在一天之后,否则则要从一天后是星期几开始循环匹配重复的日期
            if (repeate.indexOf("每天") != -1 || repeate.indexOf("永不") != -1) {
                LogInfo.d("repeate is every day");
                c.add(Calendar.DAY_OF_MONTH, 1);
            } else if (repeate.indexOf("工作日") != -1) {
                LogInfo.d("repeate is workday");
                if (6==mWay) {
                    LogInfo.d("当前为周五");
                    //mWay ="五"，当前为周五，则闹钟设置在3天后
                    c.add(Calendar.DAY_OF_MONTH, 3);
                } else if (7==mWay){
                    LogInfo.d("当前是周六");
                    //mWay ="六";当前是周六，则闹钟设置在2天后
                    c.add(Calendar.DAY_OF_MONTH, 2);
                }else {
                    LogInfo.d("闹钟设置为明天");
                    //当前不是周五也不是周六，则闹钟设置为明天
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }
            } else if (repeate.indexOf("周末") != -1) {
                LogInfo.d("repeate is weekend");
                if (1==mWay) {
                    LogInfo.d("当前是周天");
                    //mWay ="天"
                    c.add(Calendar.DAY_OF_MONTH, 6);
                } else if (7==mWay){
                    LogInfo.d("当前是周六");
                    //mWay ="六";
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }else {
                    LogInfo.d("当前是工作日");
                    LogInfo.d("ring bell "+ (7-mWay) +" later");
                    c.add(Calendar.DAY_OF_MONTH, (7-mWay));
                }
            } else {
                LogInfo.d("repeate is not special");
                int day=mWay;
                for (int i=1;i<=7;i++){
                    LogInfo.d("i="+i);
                    day=day+1;
                    if (day>7){
                        day=day-7;
                    }
                    if (1==day){
                        if (repeate.indexOf("天") != -1) {
                            LogInfo.d("周天响铃");
                            c.add(Calendar.DAY_OF_MONTH, i);
                            break;
                        }
                    }else if (2==day){
                        if (repeate.indexOf("一") != -1) {
                            LogInfo.d("周一响铃");
                            c.add(Calendar.DAY_OF_MONTH, i);
                            break;
                        }
                    }else if (3==day){
                        if (repeate.indexOf("二") != -1) {
                            LogInfo.d("周二响铃");
                            c.add(Calendar.DAY_OF_MONTH, i);
                            break;
                        }
                    }else if (4==day){
                        if (repeate.indexOf("三") != -1) {
                            LogInfo.d("周三响铃");
                            c.add(Calendar.DAY_OF_MONTH, i);
                            break;
                        }
                    }else if (5==day){
                        if (repeate.indexOf("四") != -1) {
                            LogInfo.d("周四响铃");
                            c.add(Calendar.DAY_OF_MONTH, i);
                            break;
                        }
                    }else if (6==day){
                        if (repeate.indexOf("五") != -1) {
                            LogInfo.d("周五响铃");
                            c.add(Calendar.DAY_OF_MONTH, i);
                            break;
                        }
                    }else if (7==day){
                        if (repeate.indexOf("六") != -1) {
                            LogInfo.d("周六响铃");
                            c.add(Calendar.DAY_OF_MONTH, i);
                            break;
                        }
                    }
                }
            }
        }else {
            LogInfo.d("ring bell today");
        }
        alarm.setTimeInMillis(c.getTimeInMillis());
        alarm.save();
        date = new Date(c.getTimeInMillis());
        LogInfo.d("ring bell time = "+format.format(date));
        return c;
    }
}
