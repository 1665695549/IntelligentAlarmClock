package com.example.intelligentalarmclock.rightframeLayoutclass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.intelligentalarmclock.LogInfo;
import com.example.intelligentalarmclock.PickTimeLayout;
import com.example.intelligentalarmclock.R;

public class ConditionRightLayout extends Fragment {

    private CheckBox rainCheckBox,cloudyCheckBox,sundyCheckBox;
    PickTimeLayout pickTimeLayout;
    NumberPicker NpLeft,NpMeddle,NpRight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.condition_right_fragment_area,container,false);
        rainCheckBox=(CheckBox)view.findViewById(R.id.rain_check_box);
        cloudyCheckBox=(CheckBox)view.findViewById(R.id.cloudy_check_box);
        sundyCheckBox=(CheckBox)view.findViewById(R.id.sundy_check_box);
        NpLeft=(NumberPicker)view.findViewById(R.id.Np_Left);
        NpMeddle=(NumberPicker)view.findViewById(R.id.Np_Middle);
        NpRight=(NumberPicker)view.findViewById(R.id.Np_Right);
        return view;
    }

    public String getConditionString(){
        StringBuffer stringBuffer=new StringBuffer();
        if (null != rainCheckBox)
        {
            if (true==rainCheckBox.isChecked()){
                stringBuffer.append("雨 ");
            }
            if (true==cloudyCheckBox.isChecked()){
                stringBuffer.append("多云 ");
            }
            if (true==sundyCheckBox.isChecked()){
                stringBuffer.append("晴 ");
            }

        }
        if (stringBuffer.length()==0){
            stringBuffer.append("无 ");
        }else{
            stringBuffer.insert(0,getPickTiemString());
        }
        return stringBuffer.toString();
    }

    private String getPickTiemString(){
        StringBuffer stringBuffer=new StringBuffer();
        String[] valuesList=NpLeft.getDisplayedValues();
        int i=NpLeft.getValue();
        stringBuffer.append(valuesList[i]+" ");

        i=NpMeddle.getValue();
        stringBuffer.append(String.valueOf(i)+":");

        valuesList=NpRight.getDisplayedValues();
        i=NpRight.getValue();
        stringBuffer.append(valuesList[i]+" ");
        return stringBuffer.toString();
    }
}
