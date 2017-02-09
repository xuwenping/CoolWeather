package com.example.administrator.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.util.HttpCallbackListener;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utility;

public class WeatherActivity extends Activity implements View.OnClickListener{

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        weatherDesp = (TextView) findViewById(R.id.weather_desp);
        weahterType = (TextView) findViewById(R.id.weather_type);
        highTempText = (TextView) findViewById(R.id.temp1);
        lowTempText = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        switchCity.setOnClickListener(this);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        refreshWeather.setOnClickListener(this);

        String countryName = getIntent().getStringExtra("country_name");
        weatherDesp.setText("同步中");
        weatherInfoLayout.setVisibility(View.INVISIBLE);
        cityNameText.setVisibility(View.INVISIBLE);
        String address = "http://wthrcdn.etouch.cn/weather_mini?city="+countryName;
        Log.d(TAG, "The address is " + address);
        queryFromServer(address);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
    }
}
