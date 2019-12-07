package com.example.intelligentalarmclock;

import android.text.TextUtils;
import android.util.Log;

public class LogInfo {

    private static boolean sDebug = true; //是否打印log
    private static String sTag = "coolWeather"; //默认的tag

    //重置sDebug和sTag
    public static void init(boolean debug, String tag){
        LogInfo.sDebug = debug;
        LogInfo.sTag = tag;
    }

    //不输入tag时，用默认tag
    public static void d(String msg){
        d(null, msg);
    }

    //输入tag时，重置tag
    public static void d(String tag, String msg){
        if (!sDebug) return;

        String finalTag = getFinalTag(tag);

        //TODO 通过stackElement打印具体log执行的行数
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.d(finalTag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")" + String.format(msg));
    }

    private static String getFinalTag(String tag){
        if (!TextUtils.isEmpty(tag)){
            return tag;
        }
        return sTag;
    }

    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(LogInfo.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}
