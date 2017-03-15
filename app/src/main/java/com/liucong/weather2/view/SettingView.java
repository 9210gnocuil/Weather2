package com.liucong.weather2.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liucong.weather2.R;
import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.service.UpdateService;
import com.liucong.weather2.utils.SharedPrefsUtil;

/**
 * Created by liucong on 2017/3/15.
 * 用于在设置中心中的设置item 直接用组合控件
 *
 * 回顾组合控件步骤：
 *
 * //自定义属性
 * 1） 添加自定义属性<declare-styleable>到xml文件中
 *      <resources>
            <declare-styleable name="setting">
                    <attr name="text" format="string" />
                    <attr name="labelPosition" format="enum">
                    <enum name="left" value="0"/>
                    <enum name="right" value="1"/>
                </attr>
            </declare-styleable>
        </resources>
 * 2） 在xml的<declare-styleable>中，指定属性的值
 * 3） 在view中获取xml中的值
 *      TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.setting,0, 0);
        try {
            mShowText = a.getString(R.styleable.setting_text, false);

        } finally {
            a.recycle();
        }
 * 4） 将获取的值应用到view中
 */

public class SettingView extends RelativeLayout{


    private Context ctx;
    private CheckBox settingCb;
    private TextView settingTv;
    private TextView settingTvDesc;

    private String textDesc;
    private String text;

    private Intent startUpdateService;

    public SettingView(Context context) {
        this(context,null);
    }

    public SettingView(Context context, AttributeSet attrs) {

        this(context, attrs,0);
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.settingView, 0, 0);

        try {
            text = typedArray.getString(R.styleable.settingView_text);
            textDesc = typedArray.getString(R.styleable.settingView_textDesc);
        } finally {
            typedArray.recycle();
        }

        ctx = context;
        init();
    }

    //初始化
    private void init(){
        //先加载布局
        View settingView = LayoutInflater.from(ctx).inflate(R.layout.setting_item_view, this);
        settingCb = (CheckBox) settingView.findViewById(R.id.setting_item_cb);
        settingTv = (TextView) settingView.findViewById(R.id.setting_item_tv);
        settingTvDesc = (TextView) settingView.findViewById(R.id.setting_item_tv_desc);

        settingCb.setChecked(SharedPrefsUtil.get(ActivityConstant.UPDATE_STATUS,false));
        settingTv.setText(text);
        settingTvDesc.setText(textDesc);


    }

    public void setChecked(boolean checked){
        settingCb.setChecked(checked);
        settingTvDesc.setText(checked?text+"已开启":text+"已关闭");
    }

    public boolean isChecked(){
        return settingCb.isChecked();
    }
}
