package com.example.intelligentalarmclock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;

public class RerecyclerView extends RecyclerView {

    public RerecyclerView(Context context){
        super(context);
    }

    public RerecyclerView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }

    public RerecyclerView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
    }
    boolean mInterceptTouch=false;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        /*
         *如果事件为ACTION_DOWN，初始化mInterceptTouch
         */
        if (e.getAction() == MotionEvent.ACTION_DOWN)
        {
            //LogInfo.d("RerecyclerView onInterceptTouchEvent init mInterceptTouch=false");
            mInterceptTouch=false;
        }
        if (false==mInterceptTouch )
        {
            return false;
        }else{
            return true;
        }
    }

    public void setInterceptTouch(boolean interceptTouch) {
        mInterceptTouch=interceptTouch;
    }

    /*
     * 说明：此方法为了解决第一次进入闹钟列表页面时，上下滑动列表，列表无响应问题（但是先左右滑动一下某个item后，再上下滑动列表，列表就会有响应）；
     * 当子item确定是上下滑动时，会调用setInterceptTouch（true），让父view(RerecyclerView)拦截接下来的事件，接下来的事件一般是ACTION_MOVE,若此前没有
     * 左右滑动过，则RerecyclerView会消费ACTION_MOVE事件，但无响应，所以再mInterceptTouch为true时，把MotionEvent由ACTION_MOVE改为ACTION_DOWN，再让父view消费
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        LogInfo.d("onTouchEvent  start");
        if (mInterceptTouch==true){
            mInterceptTouch=false;
            e.setAction(MotionEvent.ACTION_DOWN);
        }
        return super.onTouchEvent(e);
    }
}
