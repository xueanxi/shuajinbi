package com.anxixue.nongyao.nongyao;

import android.util.Log;

/**
 * Created by user on 11/29/17.
 */

public class LogUtils {
    private static boolean mIsLog = true;
    private static final String TAG= "anxiLog";

    public static void d(String tag,String content){
        if(mIsLog){
            Log.d(tag,content);
            Log.d(TAG,tag+" "+content);
        }
    }

    public static void e(String tag,String content){
        if(mIsLog){
            Log.e(tag,content);
            Log.e(TAG,content);
        }
    }
}
