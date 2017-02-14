package com.example.administrator.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.service.AutoUpdateService;
import com.example.administrator.coolweather.util.Utility;

public class SettingActivity extends Activity {

    private Switch backendAutoUpdateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        backendAutoUpdateSwitch = (Switch) findViewById(R.id.backend_auto_update_switch);
        final SharedPreferences prefs = getSharedPreferences("settingPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("backend_auto_update", false)) {
            backendAutoUpdateSwitch.setChecked(true);
        }
        else {
            backendAutoUpdateSwitch.setChecked(false);
        }
        backendAutoUpdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent = new Intent(SettingActivity.this, AutoUpdateService.class);
                if (b) {
                    startService(intent);
                }
                else {
                    // 停止后台更新天气服务
                    stopService(intent);
                }
                // 更新SharePreferences内容
                Utility.saveSettingInfo(SettingActivity.this, b);
            }
        });
    }
}
