package com.anxixue.nongyao.nongyao;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by user on 11/29/17.
 */

public class NongyaoService extends Service {
    private static final String TAG = "Ax/NongyaoService";

    public static final String ACTION_START_FLOAT_VIEW = "ACTION_START_FLOAT_VIEW";
    public static final String ACTION_STOP_FLOAT_VIEW = "ACTION_STOP_FLOAT_VIEW";
    private FloatView mFloatView;

    @Override
    public void onCreate() {
        LogUtils.d(TAG,"onCreate()");
        super.onCreate();
        getFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_START_FLOAT_VIEW:
                    startFloatView();
                    break;
                case ACTION_STOP_FLOAT_VIEW:
                    stopFloatView();
                    break;
            }
        } else if (intent == null) {
            // 服务异常被杀死的情况,intent 为 null

        }
        return START_STICKY;
    }

    private void stopFloatView() {
        LogUtils.d(TAG,"service stopFloatView()");
        getFloatView().hideFloatView();
    }

    private void startFloatView() {
        LogUtils.d(TAG,"service startFloatView()");
        getFloatView().showFloatView();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public FloatView getFloatView() {
        LogUtils.d(TAG,"getFloatView()");
        if(mFloatView == null){
            mFloatView = new FloatView(this);
        }
        return mFloatView;
    }
}
