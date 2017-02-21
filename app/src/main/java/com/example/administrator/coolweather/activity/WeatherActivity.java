package com.example.administrator.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.service.AutoUpdateService;
import com.example.administrator.coolweather.util.HttpCallbackListener;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utility;

public class WeatherActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{

    private static final String TAG = "WeatherActivity";

    private LinearLayout weatherInfoLayout;

    /**
     * 用于显示城市名
     */
    private TextView cityNameText;

    /**
     * 用于显示天气描述
     */
    private TextView weatherDesp;

    /**
     * 用于显示天气情况
     */
    private TextView weahterType;

    /**
     * 用于显示最高温度
     */
    private TextView highTempText;

    /**
     * 用于显示最低温度
     */
    private TextView lowTempText;

    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;

    /**
     * 切换城市按钮
     */
    private Button switchCity;

    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Test-onCreate()");
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        weatherDesp = (TextView) findViewById(R.id.weather_desp);
        weahterType = (TextView) findViewById(R.id.weather_type);
        highTempText = (TextView) findViewById(R.id.temp1);
        lowTempText = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        String countryName = getIntent().getStringExtra("country_name");
        weatherDesp.setText("同步中");
        weatherInfoLayout.setVisibility(View.INVISIBLE);
        cityNameText.setVisibility(View.INVISIBLE);
        String address = "http://wthrcdn.etouch.cn/weather_mini?city="+countryName;
        Log.d(TAG, "The address is " + address);
        queryFromServer(address);
    }

    private void queryFromServer(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this, response);
                Log.d(TAG, "The response is " + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherDesp.setText("同步失败");
                    }
                });
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int id = item.getItemId();
        switch(id) {
            case R.id.switch_city:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                weatherDesp.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String countryName = prefs.getString("city_name", "");
                if (!TextUtils.isEmpty(countryName)) {
                    String address = "http://wthrcdn.etouch.cn/weather_mini?city="+countryName;
                    queryFromServer(address);
                }
                break;
            default:
                break;
        }

        return false;
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "The city_name is " + prefs.getString("city_name", ""));
        cityNameText.setText(prefs.getString("city_name", ""));
        highTempText.setText(prefs.getString("high_temp", ""));
        lowTempText.setText(prefs.getString("low_temp", ""));
        weatherDesp.setText(prefs.getString("weather_desp", ""));
        weahterType.setText(prefs.getString("weather_type", ""));
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        SharedPreferences settingPrefs = getSharedPreferences("settingPrefs", MODE_PRIVATE);
        if (settingPrefs.getBoolean("backend_auto_update", false)) {
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }
        else {
            Intent intent = new Intent(this, AutoUpdateService.class);
            stopService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.config_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Test-onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Test-onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Test-onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Test-onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Test-onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "Test-onRestart()");
    }
}
