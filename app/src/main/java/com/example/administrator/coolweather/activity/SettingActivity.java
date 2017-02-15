package com.example.administrator.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.service.AutoUpdateService;
import com.example.administrator.coolweather.util.SharepreferencesUtilSystemSettings;

public class SettingActivity extends Activity {

    private static final String DEFAULT_INTERVAL = "8";

    private Switch backendAutoUpdateSwitch;

    private EditText intervalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        intervalText = (EditText) findViewById(R.id.backend_auto_update_interval);
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
                // 更新settingPrefs中backend_auto_update对应的值
                SharepreferencesUtilSystemSettings.putValue(SettingActivity.this, "backend_auto_update", b);
            }
        });

        intervalText.setText(SharepreferencesUtilSystemSettings.getValue(SettingActivity.this, "interval", DEFAULT_INTERVAL));
    }

    @Override
    public void onBackPressed() {

        SharepreferencesUtilSystemSettings.putValue(SettingActivity.this, "interval", intervalText.getText().toString());

        super.onBackPressed();
    }
}
