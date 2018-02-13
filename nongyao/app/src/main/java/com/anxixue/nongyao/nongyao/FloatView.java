package com.anxixue.nongyao.nongyao;

import android.content.Context;
import android.graphics.PixelFormat;

import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by user on 11/29/17.
 */

public class FloatView implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "Ax/FloatView";

    private static final int DEFAULT_TIME_LOAD = 19;            // 默认加载时间
    private static final int DEFAULT_TIME_PLAY = 35;            // 默认战斗时间

    // 使用比例是为了适配各种尺寸显示屏的手机
    /*private static final float KEY_CHUANGUAN_X = 1450f / 1920f;   // 闯关按钮的X坐标比例
    private static final float KEY_CHUANGUAN_Y = 920f / 1080f;

    private static final float KEY_ZIDONG_X = 1810f / 1920f;      // 自动战斗按钮的X坐标比例
    private static final float KEY_ZIDONG_Y = 40f / 1080f;

    private static final float KEY_TIAOGUO_X = 1810f / 1920f;     // 跳过按钮的X坐标比例
    private static final float KEY_TIAOGUO_Y = 80f / 1080f;

    private static final float KEY_ZAICITIAOZHAN_X = 1450f / 1920f;   // 再次挑战按钮的X坐标比例
    private static final float KEY_ZAICITIAOZHAN_Y = 1000f / 1080f;*/

    // 有些手机有导航栏，导致计算高度错误，所以写死高度，不是用比例
    private static final float KEY_CHUANGUAN_X = 1450f;   // 闯关按钮的X坐标比例
    private static final float KEY_CHUANGUAN_Y = 920f;

    private static final float KEY_ZIDONG_X = 1810f;      // 自动战斗按钮的X坐标比例
    private static final float KEY_ZIDONG_Y = 40f;

    private static final float KEY_TIAOGUO_X = 1700f;     // 跳过按钮的X坐标比例
    private static final float KEY_TIAOGUO_Y = 85f;

    private static final float KEY_ZAICITIAOZHAN_X = 1450f;   // 再次挑战按钮的X坐标比例
    private static final float KEY_ZAICITIAOZHAN_Y = 1000f;

    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private RelativeLayout mContentView;        // 悬浮窗的布局
    private TextView mTvLoad;                   // 显示加载时间的文本
    private TextView mTvPlay;                   // 显示游戏时间的文本
    private TextView mTvLoadTimeCount;          // 记录加载了多长时间的文本
    private TextView mTvPlayTimeCount;          // 记录游戏了多长事件的文本
    private Switch mSwitch;                     // 功能的开关
    private ImageButton mBtClose;               // 关闭悬浮窗的按钮
    private Button mBtLoadReduce;               // 减少加载等待时间的按钮
    private Button mBtLoadPlus;                 // 增加加载等待时间的按钮
    private Button mBtPlayReduce;               // 减少游戏等待时间的按钮
    private Button mBtPlayPlus;                 // 增加游戏等待时间的按钮

    private int mViewWidth;                     // 悬浮窗的宽度
    private int mViewHeight;                    // 悬浮窗的高度
    private int mScreenWidth;                   // 手机屏幕的宽度
    private int mScreenHeight;                  // 手机屏幕的高度

    float mXInScreen;
    float mYInScreen;
    float mXDownInScreen;
    float mYDownInScreen;
    float mXInView;
    float mYInView;
    int mStatusBarHeight = 0;                   // 状态栏的高度
    boolean mIsWork = false;                    // 是否正在工作
    int mLoadTime = 0;                          // 加载等待时间
    int mPlayTime = 0;                          // 游戏等待时间
    ExecutorService mExecutor;
    boolean mIsCountLoadTime = false;           // 是否在统计加载时间
    boolean mIsCountPlayTime = false;           // 是否在统计游戏时间

    public FloatView(Context context) {
        LogUtils.d(TAG, "FloatView()");
        mContext = context;
        init();
    }

    private void init() {
        LogUtils.d(TAG, "init()");
        getWindowManager();
        getStatusBarHeight();
        mScreenWidth = getScreenWidth(mContext);
        mScreenHeight = getScreenHeight(mContext);
        //Toast.makeText(mContext,"ScreenWidth = "+mScreenWidth+" ScreenHeight = "+mScreenHeight,Toast.LENGTH_SHORT).show();
        mViewWidth = (int) mContext.getResources().getDimension(R.dimen.float_view_width);
        mViewHeight = (int) mContext.getResources().getDimension(R.dimen.float_view_height);
        initFloatView();
    }

    /**
     * 获得窗口服务
     *
     * @return
     */
    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 初始化悬浮控件
     */
    private void initFloatView() {
        LogUtils.d(TAG, "initFloatView()");
        // 从配置文件获取当前的大小
        ViewGroup.LayoutParams lp;
        mContentView = (RelativeLayout) View.inflate(mContext, R.layout.layout_floatview, null);
        initTextView();
        initButton();
        mParams = new WindowManager.LayoutParams();
        mParams.setTitle("NongyaoTools");
        //设置window type
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置图片格式，效果为背景透明
        mParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.width = mViewWidth;
        mParams.height = mViewHeight;
        mParams.x = 0;
        mParams.y = 0;
        mContentView.setOnTouchListener(this);
    }

    /**
     * 初始化按钮
     */
    private void initButton() {
        mBtLoadReduce = (Button) mContentView.findViewById(R.id.bt_load_reduce);
        mBtLoadPlus = (Button) mContentView.findViewById(R.id.bt_load_plus);
        mBtPlayReduce = (Button) mContentView.findViewById(R.id.bt_play_reduce);
        mBtPlayPlus = (Button) mContentView.findViewById(R.id.bt_play_plus);
        mBtClose = (ImageButton) mContentView.findViewById(R.id.bt_close);
        mSwitch = (Switch) mContentView.findViewById(R.id.switch_floatview);


        mBtLoadReduce.setOnClickListener(this);
        mBtLoadPlus.setOnClickListener(this);
        mBtPlayReduce.setOnClickListener(this);
        mBtPlayPlus.setOnClickListener(this);
        mBtClose.setOnClickListener(this);
        mSwitch.setOnCheckedChangeListener(this);
    }

    /**
     * 初始化悬浮窗中的文本控件
     */
    private void initTextView() {
        mTvLoad = (TextView) mContentView.findViewById(R.id.et_loading);
        mTvPlay = (TextView) mContentView.findViewById(R.id.et_play);
        mTvLoad.setText(DEFAULT_TIME_LOAD + "");
        mTvPlay.setText(DEFAULT_TIME_PLAY + "");

        mTvLoadTimeCount = (TextView) mContentView.findViewById(R.id.tv_loading_time);
        mTvPlayTimeCount = (TextView) mContentView.findViewById(R.id.tv_play_time);
    }

    /***
     * 显示悬浮窗
     */
    public void showFloatView() {
        LogUtils.d(TAG, "showFloatView()");
        if (mContentView.isAttachedToWindow()) {
            LogUtils.d(TAG, "mContentView isAttached To Window ,so remove first.");
            getWindowManager().removeView(mContentView);
        }
        try {
            getWindowManager().addView(mContentView, mParams);
            LogUtils.d(TAG, "mContentView is added to window.");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏悬浮窗控件
     */
    public void hideFloatView() {
        LogUtils.d(TAG, "hideFloatView()");
        try {
            if (mContentView != null) {
                getWindowManager().removeView(mContentView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.d(TAG, "=== down ===");
                mXInScreen = event.getRawX();
                //mYInScreen = event.getRawY() - getStatusBarHeight();
                mYInScreen = event.getRawY();
                mXDownInScreen = event.getRawX();
                //mYDownInScreen = event.getRawY() - getStatusBarHeight();
                mYDownInScreen = event.getRawY();
                mXInView = event.getX();
                mYInView = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.d(TAG, "=== move ===");
                mXInScreen = event.getRawX();
                //mYInScreen = event.getRawY() - getStatusBarHeight();
                mYInScreen = event.getRawY();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.d(TAG, "=== up ===");
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY();
                updateViewPosition();
                break;
        }
        return true;
    }


    /**
     * 更新悬浮球的位置
     */
    private void updateViewPosition() {
        if (mParams == null) return;

        mParams.x = (int) (mXInScreen - mXInView);
        mParams.y = (int) (mYInScreen - mYInView) - mStatusBarHeight;

        if (mParams.x < 0) {
            mParams.x = 0;
        }
        if (mParams.y < 0) {
            mParams.y = 0;
        }
        if ((mParams.x + mViewWidth) > mScreenWidth) {
            mParams.x = mScreenWidth - mViewWidth;
        }
        if (mParams.y + mViewHeight > mScreenHeight) {
            mParams.y = mScreenHeight - mViewHeight;
        }
        getWindowManager().updateViewLayout(mContentView, mParams);
    }

    /**
     * 获得状态栏的高度，用于调整悬浮窗的位置
     *
     * @return
     */
    private int getStatusBarHeight() {
        if (mStatusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                mStatusBarHeight = mContext.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mStatusBarHeight;
    }

    /**
     * 获得屏幕的高度
     *
     * @param context
     * @return
     */
    public int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得屏幕的宽度
     * @param context
     * @return
     */
    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtils.d(TAG, "onCheckedChanged() isChecked:" + isChecked);
        if (isChecked) {
            updateLoadTime();
            updatePlayTime();
            startWork();
        } else {
            stopWork();
        }
    }

    /**
     * 从mTvLoad更新等待时间
     */
    private void updateLoadTime() {
        mLoadTime = DEFAULT_TIME_LOAD;
        if (mTvLoad != null) {
            if (TextUtils.isEmpty(mTvLoad.getText())) {
                mLoadTime = DEFAULT_TIME_LOAD;
            } else {
                try {
                    mLoadTime = Integer.valueOf(mTvLoad.getText().toString());
                } catch (Exception e) {
                    LogUtils.e(TAG, "mTvLoad get number fail!!!");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从mTvPlay更新游戏时间
     */
    private void updatePlayTime() {
        mPlayTime = DEFAULT_TIME_PLAY;
        if (mTvPlay != null) {
            if (TextUtils.isEmpty(mTvPlay.getText())) {
                mPlayTime = DEFAULT_TIME_PLAY;
            } else {
                try {
                    mPlayTime = Integer.valueOf(mTvPlay.getText().toString());
                } catch (Exception e) {
                    LogUtils.e(TAG, "mTvPlay get number fail!!!");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 停止工作
     */
    private void stopWork() {
        mIsWork = false;
        LogUtils.d(TAG, "stopWork()");
        if (null != mExecutor && !mExecutor.isShutdown()) {
            mExecutor.shutdownNow();
        }

        if (mSwitch != null && mSwitch.isChecked()) {
            mSwitch.setOnCheckedChangeListener(null);
            mSwitch.setChecked(false);
            mSwitch.setOnCheckedChangeListener(this);
        }

        if (mTvLoadTimeCount != null) {
            mTvLoadTimeCount.setText("Load:0 S");
        }
        if (mTvPlayTimeCount != null) {
            mTvPlayTimeCount.setText("Play:0 S");
        }
    }

    /**
     * 开始工作
     */
    private void startWork() {
        LogUtils.d(TAG, "startWork()");
        mIsWork = true;
        if (null != mExecutor && !mExecutor.isShutdown()) {
            mExecutor.shutdownNow();
        }

        mExecutor = Executors.newCachedThreadPool();

        try {
            mExecutor.execute(new WorkThread());
        } catch (RejectedExecutionException e) {
            Toast.makeText(mContext, "start work fail !!! please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_close:
                stopWork();
                hideFloatView();
                break;
            case R.id.bt_load_reduce:
                modifyTextView(mTvLoad, true);
                updateLoadTime();
                break;
            case R.id.bt_load_plus:
                modifyTextView(mTvLoad, false);
                updateLoadTime();
                break;
            case R.id.bt_play_reduce:
                modifyTextView(mTvPlay, true);
                updatePlayTime();
                break;
            case R.id.bt_play_plus:
                modifyTextView(mTvPlay, false);
                updatePlayTime();
                break;
        }
    }

    /**
     * 点击加减按钮之后，修改文本上面的时间
     * @param tv
     * @param isReduce
     */
    private void modifyTextView(TextView tv, boolean isReduce) {
        if (tv == null) return;
        int result = 0;
        if (!TextUtils.isEmpty(tv.getText())) {
            try {
                result = Integer.valueOf(tv.getText().toString());
                if (isReduce) {
                    result--;
                    if (result < 0) result = 0;
                } else {
                    result++;
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "mTvPlay get number fail!!!");
                e.printStackTrace();
            }
        }

        tv.setText(String.valueOf(result));
    }

    /**
     * 工作线程
     */
    class WorkThread implements Runnable {

        @Override
        public void run() {
            LogUtils.d(TAG, "run(): mPlayTime=" + mLoadTime + " mPlayTime=" + mPlayTime);
            try {
                while (mIsWork) {
                    mIsCountPlayTime = false;
                    mIsCountLoadTime = true;
                    mExecutor.execute(new loadTimeThread());

                    // 点击闯关
                    if (!mIsWork) break;
                    LogUtils.d(TAG, "点击闯关");
                    mExecutor.execute(new TapChuanGuan());

                    if (!mIsWork) break;
                    Thread.sleep(1000);
                    LogUtils.d(TAG, "点击闯关");
                    mExecutor.execute(new TapChuanGuan());

                    if (!mIsWork) break;
                    Thread.sleep(1000);
                    LogUtils.d(TAG, "点击闯关");
                    mExecutor.execute(new TapChuanGuan());

                    if (!mIsWork) break;
                    Thread.sleep((mLoadTime - 2) * 1000);
                    mIsCountLoadTime = false;

                    mIsCountPlayTime = true;
                    mExecutor.execute(new playTimeThread());

                    // 点击自动战斗
                    if (!mIsWork) break;
                    Thread.sleep(1000);
                    LogUtils.d(TAG, "点击自动战斗");
                    mExecutor.execute(new TapZiDongZhanDou());

                    for (int i = 0; i < mPlayTime - 8; i++) {
                        if (!mIsWork) break;
                        LogUtils.d(TAG, "点击跳过 " + i);
                        mExecutor.execute(new TapTiaoGuo());
                        Thread.sleep(1000);
                    }

                    // 点击再次挑战
                    if (!mIsWork) break;
                    LogUtils.d(TAG, "点击再次挑战");
                    mExecutor.execute(new TapZaiCiTiaoZhan());

                    if (!mIsWork) break;
                    Thread.sleep(7000);
                    mIsCountPlayTime = false;
                }
            } catch (InterruptedException e) {
                LogUtils.d(TAG, "run(): InterruptedException" + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 点击再次挑战的按钮任务
     */
    class TapZaiCiTiaoZhan implements Runnable {
        @Override
        public void run() {
            //ShellUtils.execCommand("input tap " + mScreenHeight * KEY_ZAICITIAOZHAN_X + " " + mScreenWidth * KEY_ZAICITIAOZHAN_Y, false);
            ShellUtils.execCommand("input tap " +  KEY_ZAICITIAOZHAN_X + " " +  KEY_ZAICITIAOZHAN_Y, false);
        }
    }

    /**
     * 点击跳过的任务
     */
    class TapTiaoGuo implements Runnable {
        @Override
        public void run() {
            //ShellUtils.execCommand("input tap " + mScreenHeight * KEY_TIAOGUO_X + " " + mScreenWidth * KEY_TIAOGUO_Y, false);
            ShellUtils.execCommand("input tap " +  KEY_TIAOGUO_X + " " +  KEY_TIAOGUO_Y, false);
        }
    }

    /**
     * 点击自动战斗的任务
     */
    class TapZiDongZhanDou implements Runnable {
        @Override
        public void run() {
            ShellUtils.execCommand("input tap " +   KEY_ZIDONG_X + " " +  KEY_ZIDONG_Y, false);
            //ShellUtils.execCommand("input tap " + mScreenHeight * KEY_ZIDONG_X + " " + mScreenWidth * KEY_ZIDONG_Y, false);
        }
    }

    /**
     * 点击闯关的任务
     */
    class TapChuanGuan implements Runnable {
        @Override
        public void run() {
            //ShellUtils.execCommand("input tap " + mScreenHeight * KEY_CHUANGUAN_X + " " + mScreenWidth * KEY_CHUANGUAN_Y, false);
            ShellUtils.execCommand("input tap " + KEY_CHUANGUAN_X + " " + KEY_CHUANGUAN_Y, false);
        }
    }


    /**
     * 统计加载时间的线程
     */
    class loadTimeThread implements Runnable {
        int loadTime = 0;

        loadTimeThread() {
            loadTime = 0;
        }

        @Override
        public void run() {
            try {
                while (mIsCountLoadTime) {
                    Thread.sleep(998);
                    loadTime++;
                    Message msg = new Message();
                    msg.what = 1000;
                    msg.arg1 = loadTime;
                    mHandler.sendMessage(msg);
                }
            } catch (InterruptedException e) {
                LogUtils.d(TAG, "run(): InterruptedException" + e);
                e.printStackTrace();
                mIsCountLoadTime = false;
            }
        }
    }

    /**
     * 统计游戏时间的线程
     */
    class playTimeThread implements Runnable {
        int playTime = 0;

        playTimeThread() {
            playTime = 0;
        }

        @Override
        public void run() {
            try {
                while (mIsCountPlayTime) {
                    Thread.sleep(998);
                    playTime++;
                    Message msg = new Message();
                    msg.what = 1001;
                    msg.arg1 = playTime;
                    mHandler.sendMessage(msg);
                }
            } catch (InterruptedException e) {
                LogUtils.d(TAG, "run(): InterruptedException" + e);
                e.printStackTrace();
                mIsCountPlayTime = false;
            }
        }
    }

    /**
     * 处理器
     */
    android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    mTvLoadTimeCount.setText("Load Time: " + msg.arg1);
                    break;
                case 1001:
                    mTvPlayTimeCount.setText("Play Time: " + msg.arg1);
                    break;
            }
        }
    };
}
