package com.example.intelligentalarmclock.rightframeLayoutclass;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.intelligentalarmclock.LogInfo;
import com.example.intelligentalarmclock.R;

import alarmclass.CreateAlarmActivity;

public class TitleRightLayout extends Fragment {
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.title_right_fragment_area,container,false);
        editText=view.findViewById(R.id.edit_text);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public String getEditText() {
        String text = editText.getText().toString();
        if (text.length()==0)
        {
            text="闹钟";
        }
        return text;
    }

    public void initEditText(String string){
        LogInfo.d("initEditText start");
        editText.setText(string);
        editText.setSelection(editText.getText().length());
        LogInfo.d("initEditText end");
    }

    /*
     *隐藏输入法键盘，只有侧滑页面打开完成后，getActivity才会有值；只有侧换页面完全隐藏后，getActivity才会为空
     */

    public void hideInput(){
        LogInfo.d("hideInput start");
        CreateAlarmActivity createAlarmActivity =(CreateAlarmActivity)getActivity();
        LogInfo.d("createAlarmActivity ok");
        if (createAlarmActivity !=null){
            InputMethodManager imm =  (InputMethodManager)createAlarmActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
        }
        LogInfo.d("hideInput end");
    }

}
