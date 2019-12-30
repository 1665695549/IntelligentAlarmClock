package alarmclass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.intelligentalarmclock.LogInfo;
import com.example.intelligentalarmclock.MyDrawerLayout;
import com.example.intelligentalarmclock.R;
import com.example.intelligentalarmclock.db.Alarm;
import com.example.intelligentalarmclock.db.SelectedInfo;
import com.example.intelligentalarmclock.rightframeLayoutclass.ConditionRightLayout;
import com.example.intelligentalarmclock.rightframeLayoutclass.RepeatRightLayout;
import com.example.intelligentalarmclock.rightframeLayoutclass.TitleRightLayout;

import org.litepal.LitePal;

import java.util.List;

public class CreateAlarmActivity extends AppCompatActivity {

    private final static int REPEAT=1,TITLE=2,CONDITION=3;
    private static int createOrEdit=0;
    private int selectedLayout=REPEAT;

    private Button saveButton,backButton;
    private NumberPicker NpLeft,NpMiddle,NpRight;
    private RelativeLayout repeatLayout,titlelauout,conditionLayout;
    private MyDrawerLayout drawerLayout;
    private FrameLayout rightFragment;
    private RepeatRightLayout repeatRightLayout;
    private TitleRightLayout titleRightLayout;
    private ConditionRightLayout conditionRightLayout;
    private TextView repeatValue,titleValue,conditionValue;

    /**
     *判断是编辑页面还是创建页面，并初始化页面
     */
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_alarm);
        LogInfo.d("CreateAlarmActivity onCreate start.Thread="+Thread.currentThread().getId());
        saveButton=findViewById(R.id.finish);
        backButton=findViewById(R.id.cancle);
        NpLeft=findViewById(R.id.Np_Left);
        NpMiddle=findViewById(R.id.Np_Middle);
        NpRight=findViewById(R.id.Np_Right);
        repeatLayout=findViewById(R.id.repeat_layout);
        titlelauout=findViewById(R.id.title_layout);
        conditionLayout=findViewById(R.id.condition_layout) ;
        rightFragment=findViewById(R.id.right_fragment);
        drawerLayout=findViewById(R.id.right_drawerLayout);
        repeatValue=findViewById(R.id.repeat_value);
        titleValue=findViewById(R.id.title_value);
        conditionValue=findViewById(R.id.condition_value);

        Intent receiveIntent=getIntent();
        createOrEdit=receiveIntent.getIntExtra("item_flag",100);
        if (0==createOrEdit){
            //do nothing
        }else{
            //Edit
            List<Alarm> testList=LitePal.findAll(Alarm.class);
            for (Alarm alarm:testList){
                LogInfo.d("alarmID="+alarm.getAlarmID());
            }
            List<Alarm> alarmList=LitePal.where("alarmID=?",String.valueOf(createOrEdit)).find(Alarm.class);
            Alarm alarm=alarmList.get(0);
            String APM=alarm.getAPm();
            if (-1!=APM.indexOf("上")){
                LogInfo.d("APM="+APM);
                NpLeft.setValue(0);
            }else{
                NpLeft.setValue(1);
            }
            int hour=alarm.getHour();
            NpMiddle.setValue(hour);
            int minute=Integer.valueOf(alarm.getMinute());
            NpRight.setValue(minute);
            repeatValue.setText(alarm.getRepeate());
            titleValue.setText(alarm.getTitle());
            conditionValue.setText(alarm.getCondition());
        }

        //监听保存按钮,把数据存入数据库,并返回AlarmActivity
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("saveButton.setOnClickListener start.ThreadID="+Thread.currentThread().getId());
                if (0==createOrEdit){
                    creatNewAlarm();
                }else{
                    editDatabaseAlarm(createOrEdit);
                }
            }
        });
        //监听返回按钮,放弃保存数据，并返回AlarmActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("backButton onClick.ThreadID="+Thread.currentThread().getId());
                Intent intent=new Intent();
                intent.putExtra("alarmID",0);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        //监听重复布局
        repeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("repeatLayout onClick start.ThreadID="+Thread.currentThread().getId());
                selectedLayout=REPEAT;
                if (null == repeatRightLayout){
                    repeatRightLayout=new RepeatRightLayout();
                }
                resetFragment(repeatRightLayout);
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        //监听标签布局
        titlelauout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("titlelauout onClick start.ThreadID="+Thread.currentThread().getId());
                selectedLayout=TITLE;
                if (null == titleRightLayout){
                    titleRightLayout=new TitleRightLayout();
                }
                resetFragment(titleRightLayout);
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        //监听条件布局
        conditionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("conditionLayout onClick start.Thread="+Thread.currentThread().getId());
                selectedLayout=CONDITION;
                if (null==conditionRightLayout){
                    conditionRightLayout=new ConditionRightLayout();
                }
                resetFragment(conditionRightLayout);
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        //监听侧滑页面打开状态
        DrawerLayout.DrawerListener repeatDrawerListener=new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (selectedLayout==REPEAT){
                    String repeatString =repeatValue.getText().toString();
                    repeatRightLayout.initRepeatText(repeatString);
                }else if (selectedLayout==TITLE){
                    String titleString=titleValue.getText().toString();
                    titleRightLayout.initEditText(titleString);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (selectedLayout==REPEAT){
                    String repeatString = repeatRightLayout.getRepeatString();
                    repeatValue.setText(repeatString);
                }else if (selectedLayout==TITLE){
                    String titleString=titleRightLayout.getEditText();
                    titleValue.setText(titleString);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState==DrawerLayout.STATE_SETTLING){
                    if (selectedLayout==TITLE){
                        titleRightLayout.hideInput();
                    }else if (selectedLayout==CONDITION){
                        String conditionSrting=conditionRightLayout.getConditionString();
                        conditionValue.setText(conditionSrting);
                    }
                }
            }
        };
        drawerLayout.addDrawerListener(repeatDrawerListener);

    }

    /**
     * 给侧滑子页面配置相应的layout
     */
    private void resetFragment(Fragment frameLayout){
        LogInfo.d("resetFragment start.Thread="+Thread.currentThread().getId());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.right_fragment,frameLayout);
        fragmentTransaction.commit();
    }

    /**
     * 说明：创建新闹钟并存入数据库中
     * 参数：alarmID：特定的闹钟ID
     */
    private void creatNewAlarm(){
        //新建一个闹钟
        LogInfo.d("creatNewAlarm and save to the data base.Thread="+Thread.currentThread().getId());
        Alarm alarm=new Alarm();
        List<Alarm> list=LitePal.findAll(Alarm.class);
        if (0 == list.size()){
            LogInfo.d("alarm list is empty, alarmID=1");
            alarm.setAlarmID(1);
        }else{
            LogInfo.d("alarm list is not empty");
            for(int i=1;;i++){
                boolean id_ok=true;
                for (int j=0;j<list.size();j++){
                    if (i==list.get(j).getAlarmID()){
                        id_ok=false;
                        break;
                    }
                }
                if (true==id_ok){
                    alarm.setAlarmID(i);
                    break;
                }
            }
        }

        //获取上/下午
        String[] valuesList=NpLeft.getDisplayedValues();
        int i=NpLeft.getValue();
        alarm.setAPm(valuesList[i]);
        //获取小时值
        i=NpMiddle.getValue();
        alarm.setHour(i);
        //获取分钟值
        valuesList=NpRight.getDisplayedValues();
        i=NpRight.getValue();
        alarm.setMinute(valuesList[i]);
        //获取闹钟重复值并存入数据库
        alarm.setRepeate(repeatValue.getText().toString());
        //获取闹钟标签并存入数据库
        alarm.setTitle(titleValue.getText().toString());
        //获取闹钟条件并存入数据库

        alarm.setCondition(conditionValue.getText().toString());
        LogInfo.d("save alarm:"+alarm.getAlarmID()+alarm.getAPm()+alarm.getHour()+alarm.getMinute()+alarm.getTitle()+alarm.getRepeate()+alarm.getCondition());
        //要判断当前有没有已选择的城市
        List<SelectedInfo> selectedInfoList =  LitePal.findAll(SelectedInfo.class);
        if (0!=selectedInfoList.size()){
            SelectedInfo selectedInfo =selectedInfoList.get(0);
            alarm.setWeatherID(selectedInfo.getWeatherID());
        }else{
            LogInfo.d("SelectedInfo is null");
        }
        alarm.setVality(true);
        alarm.save();
        Intent intent=new Intent();
        intent.putExtra("alarmID",alarm.getAlarmID());
        setResult(RESULT_OK,intent);
        finish();
    }
    private void editDatabaseAlarm(int alarmID){
        LogInfo.d("editDatabaseAlarm start.ThreadID="+Thread.currentThread().getId());
        List<Alarm> alarmList=LitePal.where("alarmID=?",String.valueOf(alarmID)).find(Alarm.class);
        Alarm alarm=alarmList.get(0);
        //获取上/下午
        String[] valuesList=NpLeft.getDisplayedValues();
        int i=NpLeft.getValue();
        alarm.setAPm(valuesList[i]);
        //获取小时值
        i=NpMiddle.getValue();
        alarm.setHour(i);
        //获取分钟值
        valuesList=NpRight.getDisplayedValues();
        i=NpRight.getValue();
        alarm.setMinute(valuesList[i]);
        //获取闹钟重复值并存入数据库
        alarm.setRepeate(repeatValue.getText().toString());
        //获取闹钟标签并存入数据库
        alarm.setTitle(titleValue.getText().toString());
        //获取闹钟条件并存入数据库
        alarm.setCondition(conditionValue.getText().toString());
        alarm.save();
        Intent intent=new Intent();
        intent.putExtra("alarmID",alarmID);
        setResult(RESULT_OK,intent);
        finish();
    }
}
