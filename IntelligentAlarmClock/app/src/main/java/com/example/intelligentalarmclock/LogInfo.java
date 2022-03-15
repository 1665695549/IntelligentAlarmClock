package com.example.intelligentalarmclock;

import android.content.Context;
import android.graphics.ColorSpace;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogInfo {

    private static boolean sDebug = true; //是否打印log
    private static String sTag = "coolWeather"; //默认的tag
    private static File dirPath;

    public LogInfo(String message){
        Log.d(sTag,"LogInfo start");
    }

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
                + targetStackTraceElement.getLineNumber() + ")" + msg);

         /*
        dirPath= new File("/data/data/com.example.intelligentalarmclock/files/log.txt");
        if (dirPath.exists()){
            Log.d(sTag,"dirPath.exists()");
        }else{
            dirPath= new File("/data/data/com.example.intelligentalarmclock/files");
            dirPath.mkdir();
            dirPath= new File("/data/data/com.example.intelligentalarmclock/files/log.txt");
            try {
                dirPath.createNewFile();
            }catch (IOException e){
                Log.d(sTag,"someThing wrong");
            }
        }
        try {
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-hh-mm aaa");
            Date date=new Date(calendar.getTimeInMillis());

            FileWriter fileWriter=new FileWriter(dirPath,true);
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            bufferedWriter.write(format.format(date)+finalTag+"(" + targetStackTraceElement.getFileName() + ":"
                    + targetStackTraceElement.getLineNumber() + ")" + String.format(msg));
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();

        }catch (IOException e){
            Log.d(sTag,"something wrong");
        }
        */
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
