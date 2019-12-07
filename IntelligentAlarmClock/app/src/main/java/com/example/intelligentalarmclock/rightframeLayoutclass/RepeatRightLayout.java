package com.example.intelligentalarmclock.rightframeLayoutclass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import alarmclass.CreateAlarmActivity;
import com.example.intelligentalarmclock.LogInfo;
import com.example.intelligentalarmclock.R;

public class RepeatRightLayout extends Fragment {

    private RelativeLayout[] datesList = new RelativeLayout[7]; //星期天到星期一
    private TextView[] imgesList=new TextView[7];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repeat_right_fragment_area, container,false);
        LogInfo.d("RepeatRightLayout onCreateView" );
        //初始化控件变量
        datesList[0]=(RelativeLayout)view.findViewById(R.id.sun_layout);
        imgesList[0]=(TextView)view.findViewById(R.id.sun_image);
        datesList[1]=(RelativeLayout)view.findViewById(R.id.mon_layout);
        imgesList[1]=(TextView)view.findViewById(R.id.mon_image);
        datesList[2]=(RelativeLayout)view.findViewById(R.id.tue_layout);
        imgesList[2]=(TextView)view.findViewById(R.id.tue_image);
        datesList[3]=(RelativeLayout)view.findViewById(R.id.wed_layout);
        imgesList[3]=(TextView)view.findViewById(R.id.wed_image);
        datesList[4]=(RelativeLayout)view.findViewById(R.id.thu_layout);
        imgesList[4]=(TextView)view.findViewById(R.id.thu_image);
        datesList[5]=(RelativeLayout)view.findViewById(R.id.fri_layout);
        imgesList[5]=(TextView)view.findViewById(R.id.fri_image);
        datesList[6]=(RelativeLayout)view.findViewById(R.id.sat_layout);
        imgesList[6]=(TextView)view.findViewById(R.id.sat_image);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogInfo.d("onActivityCreated start");

        //监听控件的点击事件
        datesList[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (View.VISIBLE==imgesList[0].getVisibility()){
                    imgesList[0].setVisibility(View.INVISIBLE);
                }else{
                    imgesList[0].setVisibility(View.VISIBLE);
                }
            }
        });
        datesList[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (View.VISIBLE==imgesList[1].getVisibility()){
                    imgesList[1].setVisibility(View.INVISIBLE);
                }else{
                    imgesList[1].setVisibility(View.VISIBLE);
                }
            }
        });
        datesList[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (View.VISIBLE==imgesList[2].getVisibility()){
                    imgesList[2].setVisibility(View.INVISIBLE);
                }else{
                    imgesList[2].setVisibility(View.VISIBLE);
                }
            }
        });
        datesList[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (View.VISIBLE==imgesList[3].getVisibility()){
                    imgesList[3].setVisibility(View.INVISIBLE);
                }else{
                    imgesList[3].setVisibility(View.VISIBLE);
                }
            }
        });
        datesList[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (View.VISIBLE==imgesList[4].getVisibility()){
                    imgesList[4].setVisibility(View.INVISIBLE);
                }else{
                    imgesList[4].setVisibility(View.VISIBLE);
                }
            }
        });
        datesList[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (View.VISIBLE==imgesList[5].getVisibility()){
                    imgesList[5].setVisibility(View.INVISIBLE);
                }else{
                    imgesList[5].setVisibility(View.VISIBLE);
                }
            }
        });
        datesList[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (View.VISIBLE==imgesList[6].getVisibility()){
                    imgesList[6].setVisibility(View.INVISIBLE);
                }else{
                    imgesList[6].setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public String getRepeatString(){
        LogInfo.d("getRepeatString start");
        StringBuffer repeatString = new StringBuffer();
        if ( (imgesList[0].getVisibility()==View.VISIBLE) && (imgesList[6].getVisibility()==View.VISIBLE)){
            int i;
            for (i=1; i<6; i++){
                if (imgesList[i].getVisibility()==View.VISIBLE){
                    break;
                }
            }
            if (i==6){
                repeatString.append("周末 ");
            }
            if (0==repeatString.length()){
                for (i=1; i<6; i++){
                    if (imgesList[i].getVisibility()==View.INVISIBLE){
                        break;
                    }
                }
                if (i==6){
                    repeatString.append("每天 ");
                }
            }

            if (0==repeatString.length()){
                LogInfo.d("非特殊时间");
                String[] dates={"星期天 ","星期一 ","星期二 ","星期三 ","星期四 ","星期五 ","星期六 "};
                for (i=0; i<7; i++){
                    if (imgesList[i].getVisibility()==View.VISIBLE){
                        repeatString.append(dates[i]);
                    }
                }
            }
        }else if ((imgesList[0].getVisibility()==View.INVISIBLE) && (imgesList[6].getVisibility()==View.INVISIBLE)){
            int i;
            for (i=1; i<6; i++){
                if (imgesList[i].getVisibility()==View.INVISIBLE){
                    break;
                }
            }
            if (6==i){
                repeatString.append("工作日 ");
            }
            if (repeatString.length()==0){
                LogInfo.d("非特殊时间");
                String[] dates={"星期天 ","星期一 ","星期二 ","星期三 ","星期四 ","星期五 ","星期六 "};
                for (i=1; i<6; i++){
                    if (imgesList[i].getVisibility()==View.VISIBLE){
                        repeatString.append(dates[i]);
                    }
                }
            }

            if (repeatString.length()==0){
                repeatString.append("永不 ");
            }
        }else {
            LogInfo.d("非特殊时间");
            String[] dates={"星期天 ","星期一 ","星期二 ","星期三 ","星期四 ","星期五 ","星期六 "};
            for (int i=0; i<7; i++){
                if (imgesList[i].getVisibility()==View.VISIBLE){
                    repeatString.append(dates[i]);
                }
            }
            if (repeatString.length()==0){
                repeatString.append("永不 ");
            }
        }
        String selectDate = repeatString.toString();
        LogInfo.d("getRepeatString end");
        return selectDate;
    }

    public void initRepeatText(String string){
        LogInfo.d("initRepeatText start");
        if (string.equals("每天 ")){
            for(int i=0; i<=7; i++){
                imgesList[i].setVisibility(View.VISIBLE);
            }
        }else if (string.equals("周末 ")){
            imgesList[0].setVisibility(View.VISIBLE);
            imgesList[6].setVisibility(View.VISIBLE);
        }else if (string.equals("工作日 ")){
            for(int i=1; i<=5; i++){
                imgesList[i].setVisibility(View.VISIBLE);
            }
        }else{
            String[] flagList = {"天","一","二","三","四","五","六"};
            for (int i=0;i<flagList.length; i++){
                boolean status=string.contains(flagList[i]);
                if (true==status){
                    imgesList[i].setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
