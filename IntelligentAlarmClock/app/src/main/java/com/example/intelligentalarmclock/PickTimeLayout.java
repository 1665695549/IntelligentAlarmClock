package com.example.intelligentalarmclock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class PickTimeLayout extends LinearLayout{

    private Context mContext;

    private String[] AmPmList ={"上午","下午"};

    private String[] minuteList = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25",
            "26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56",
            "57","58","59"};

    //选择框之间距离
    private int mOffsetMargin=12;

    //视图控件
    private NumberPicker mNpLeft,mNpMiddle,mNpRight;

    public PickTimeLayout(Context context){
        super(context);
        this.mContext=context;
        generateView();
        initPicker();
    }

    public PickTimeLayout(Context context, AttributeSet attr){
        super(context, attr);
        this.mContext=context;
        generateView();
        initPicker();
    }

    public PickTimeLayout(Context context, AttributeSet attr, int def){
        super(context, attr, def);
        this.mContext=context;
        generateView();
        initPicker();
    }

    private void generateView(){
        //设置当前的布局属性
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);

        mNpLeft = new NumberPicker(mContext);//在mmContext里建一个时间拾取器(控件)
        mNpMiddle = new NumberPicker(mContext);//在mmContext里建一个时间拾取器
        mNpRight = new NumberPicker(mContext);//在mmContext里建一个时间拾取器

        mNpLeft.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);//mNpLeft会覆盖子类控件而直接获得焦点，让选项不可编辑
        mNpMiddle.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);//mNpLeft会覆盖子类控件而直接获得焦点，让选项不可编辑
        mNpRight.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);//mNpLeft会覆盖子类控件而直接获得焦点，让选项不可编辑

        //设置高和边距
        ViewGroup.LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LayoutParams) params).setMargins(0,0,0,0);
        mNpLeft.setLayoutParams(params); //设置左边时间拾取器控件的边距
        ((LayoutParams) params).setMargins(dip2px(mOffsetMargin),0,0,0);
        mNpMiddle.setLayoutParams(params); //设置中间时间拾取器控件的边距
        mNpRight.setLayoutParams(params); //设置右边时间拾取器控件的边距

        mNpLeft.setId(R.id.Np_Left);
        mNpMiddle.setId(R.id.Np_Middle);
        mNpRight.setId(R.id.Np_Right);


        //添加控件
        this.addView(mNpLeft);
        this.addView(mNpMiddle);
        this.addView(mNpRight);

    }

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    private int dip2px(int dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;//获取屏幕参数,屏幕密度
        return (int) (scale * dp + 0.5f);
    }

    private void initPicker(){
        mNpLeft.setDisplayedValues(AmPmList);
        mNpLeft.setMinValue(0);
        mNpLeft.setMaxValue(AmPmList.length-1);
        mNpLeft.setValue(0);

        mNpMiddle.setMinValue(1);
        mNpMiddle.setMaxValue(12);
        mNpMiddle.setValue(5);

        mNpRight.setMinValue(0);
        mNpRight.setMaxValue(minuteList.length-1);
        mNpRight.setDisplayedValues(minuteList);
        mNpRight.setValue(0);
    }
}
