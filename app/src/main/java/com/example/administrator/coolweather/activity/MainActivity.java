package com.example.administrator.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.County;
import com.example.administrator.coolweather.model.Province;
import com.example.administrator.coolweather.util.HttpCallbackListener;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.SharepreferencesUtilSystemSettings;
import com.example.administrator.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private static final String PROVINCEURL = "http://www.weather.com.cn/data/city3jdata/china.html";

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    public static class REGION {
        public static final String REGION_PROVINCE = "province";
        public static final String REGION_CITY = "city";
        public static final String REGION_COUNTY = "county";
    }

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /*
    * 省列表
     */
    private List<Province> provinceList;

    /*
    * 市列表
     */
    private List<City> cityList;

    /*
    * 县列表
     */
    private List<County> countyList;

    /*
    * 选中的省份
     */
    private Province selectedProvince;

    /*
    * 选中的城市
     */
    private City selectedCity;

    /*
    * 当前选中的级别
     */
    private int currentLevel;

    /**
     * 是否从WeatherActivity中跳转过来
     */
    private boolean isFromWeatherActivity;

    private Button settingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Test-onCreate()");


        float xdpi = getResources().getDisplayMetrics().xdpi;
        float ydpi = getResources().getDisplayMetrics().ydpi;
        Log.d(TAG, "xdpi is " + xdpi);
        Log.d(TAG, "ydpi is " + ydpi);
        Log.d(TAG, "width is " + getResources().getDisplayMetrics().widthPixels);
        Log.d(TAG, "heigth is " + getResources().getDisplayMetrics().heightPixels);

        SharedPreferences settingPrefs = getSharedPreferences("settingPrefs", MODE_PRIVATE);
        // 如果是第一次安装，初始化相关设置，如果不是首次安装，不更新配置
        if (!settingPrefs.getBoolean("first_instatll", false)) {
            SharepreferencesUtilSystemSettings.putValue(this, "backend_auto_update", true);
            SharepreferencesUtilSystemSettings.putValue(this, "first_instatll", true);
            SharepreferencesUtilSystemSettings.putValue(this,"interval", "8");
        }

        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected", false)
                && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countryName = countyList.get(i).getCountyName();
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("country_name", countryName);
                    startActivity(intent);
                    finish();
                }
            }
        });

        settingBtn = (Button) findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(this);

        queryProvinces();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_btn:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvince();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }
        else {
            queryFromSever(null, REGION.REGION_PROVINCE);
        }
    }

    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }
        else {
            queryFromSever(selectedProvince.getProvinceCode(), REGION.REGION_CITY);
        }
    }

    private void queryCounties() {
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }
        else {
            queryFromSever(selectedProvince.getProvinceCode()+selectedCity.getCityCode(), REGION.REGION_COUNTY);
        }
    }

    private void queryFromSever(final String code, final String type) {
        String address = "";
        if (TextUtils.isEmpty(code)) {
            address = PROVINCEURL;
        }
        else if (type.equals(REGION.REGION_CITY)) {
            address = "http://www.weather.com.cn/data/city3jdata/provshi/"+code+".html";
        }
        else if (type.equals(REGION.REGION_COUNTY)) {
            address = "http://www.weather.com.cn/data/city3jdata/station/"+code+".html";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (type.equals(REGION.REGION_PROVINCE)) {
                    result = Utility.handleProvinceResponse(coolWeatherDB, response);
                }
                else if (type.equals(REGION.REGION_CITY)) {
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
                }
                else if (type.equals(REGION.REGION_COUNTY)) {
                    result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
                }

                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals(REGION.REGION_PROVINCE)) {
                                queryProvinces();
                            }
                            else if (type.equals(REGION.REGION_CITY)) {
                                queryCities();
                            }
                            else if (type.equals(REGION.REGION_COUNTY)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        }
        else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        }
        else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
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
