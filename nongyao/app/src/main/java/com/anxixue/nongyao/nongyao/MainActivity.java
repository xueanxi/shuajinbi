package com.anxixue.nongyao.nongyao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * 这个工具的使用方法
 * 1.先启动悬浮窗，不要打开功能的开关
 * 2.进入王者农药，进入冒险模式，进入魔女的回忆关卡，进入有“闯关”的按钮界面
 * 3.打开悬浮窗上面的开关
 * 4.根据自己手机的配置可以自己增加减少 加载时间和游戏时间
 * 5.调整好时间之后，就挂机OK
 * 6.不想用的时候，关闭开关，然后点击红色的叉叉，关闭悬浮窗。
 * 7.结束
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "Ax/MainActivity";
    Button btStartService;
    Button btStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
    }

    private void initView() {
        btStartService = (Button) this.findViewById(R.id.bt_start_service);
        btStopService = (Button) this.findViewById(R.id.bt_stop_service);
        btStartService.setOnClickListener(this);
        btStopService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.bt_start_service){
            startFloatView();
        }else if(id == R.id.bt_stop_service){
            stopFloatView();
        }
    }

    private void stopFloatView() {
        Intent intent = new Intent(MainActivity.this,NongyaoService.class);
        intent.setAction(NongyaoService.ACTION_STOP_FLOAT_VIEW);
        startService(intent);
    }

    private void startFloatView() {
        LogUtils.d(TAG,"startFloatView()");
        Intent intent = new Intent();
        intent.setPackage("com.anxixue.nongyao.nongyao");
        intent.setClass(MainActivity.this,NongyaoService.class);
        intent.setAction(NongyaoService.ACTION_START_FLOAT_VIEW);
        startService(intent);
    }
}
