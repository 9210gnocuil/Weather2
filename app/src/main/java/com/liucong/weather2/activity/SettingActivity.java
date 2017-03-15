package com.liucong.weather2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.liucong.weather2.R;
import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.service.UpdateService;
import com.liucong.weather2.utils.ServiceUtils;
import com.liucong.weather2.utils.SharedPrefsUtil;
import com.liucong.weather2.view.SettingView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.setting_item)
    SettingView sv;
    @BindView(R.id.setting_btn_updatienterval)
    Button btnUpdateInterval;
    
    private Intent startUpdateService;
    private Intent changeUpdateTimeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        startUpdateService = new Intent(SettingActivity.this, UpdateService.class);
        changeUpdateTimeService = new Intent(SettingActivity.this, UpdateService.class);
    }

    private void initView() {

        //首先判断自动更新服务是否已经开启，开起了就要设置SettingView为选中状态
        boolean isRunning = ServiceUtils.isServiceRunning(UpdateService.class.getName());
        sv.setChecked(isRunning);

        //设置点击侦听
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv.setChecked(!sv.isChecked());

                btnUpdateInterval.setVisibility(sv.isChecked()?View.VISIBLE:View.INVISIBLE);

                //开启服务：
                if(sv.isChecked()){
                    //服务默认是1小时更新
                    startService(startUpdateService);
                }else{
                    stopService(startUpdateService);
                }
            }
        });

        //服务正在运行时就显示，未在运行就不显示
        btnUpdateInterval.setVisibility(isRunning?View.VISIBLE:View.INVISIBLE);
        btnUpdateInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示个多项选择对话框
                Log.i("onClick", "onClicked");
                showDialog();
            }
        });

    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("请选择更新时间间隔:");
        final String[] items = new String[]{"1小时","2小时","4小时","8小时","12小时","24小时"};
        final int[] times = new int[]{1,2,4,8,12,24};
        int choiced = 0;
        
        //从sp文件中找到存放的更新事件
        int updateTimes = SharedPrefsUtil.get(ActivityConstant.UPDATE_TIME_INTERVAL, -1);
        if(updateTimes != -1){
            choiced = getCheckedIndex(times,updateTimes);
        }

        builder.setSingleChoiceItems(items, choiced, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //更改服务更新时间
                changeUpdateTimeService.putExtra("TimeInterval",times[which]*1000*3600L);
                startService(changeUpdateTimeService);
                //将更新事件存到sp文件中
                SharedPrefsUtil.save(ActivityConstant.UPDATE_TIME_INTERVAL,times[which]);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private int getCheckedIndex(int[] times,int data){
        for(int i=0;i<times.length;i++){
            if(times[i]==data)
                return i;
        }
        return 0;
    }

}
