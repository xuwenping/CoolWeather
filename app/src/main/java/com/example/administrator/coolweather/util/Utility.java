package com.example.administrator.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.County;
import com.example.administrator.coolweather.model.Province;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/24.
 */
public class Utility {

    /*
    * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            Map<String, String> map = gson.fromJson(response, new TypeToken<Map<String, String>>(){}.getType());
            if (map != null
                    && map.size() > 0) {
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    Province province = new Province();
                    province.setProvinceName(value);
                    province.setProvinceCode(key);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }

        return false;
    }

    /*
    * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            Map<String, String> map = gson.fromJson(response, new TypeToken<Map<String, String>>(){}.getType());
            if (map != null
                    && map.size() > 0) {
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    City city = new City();
                    city.setCityName(value);
                    city.setCityCode(key);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }

        return false;
    }

    /*
    * 解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            Map<String, String> map = gson.fromJson(response, new TypeToken<Map<String, String>>(){}.getType());
            if (map != null
                    && map.size() > 0) {
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    County county = new County();
                    county.setCountyName(value);
                    county.setCountyCode(key);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }

        return false;
    }

    /**
     * 解析服务器返回的json数据, 并将解析出来的数据存储在本地
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("data");
            String cityName = weatherInfo.getString("city");
            String weatherDesp = weatherInfo.getString("ganmao");
            JSONArray tempForecastList = weatherInfo.getJSONArray("forecast");
            JSONObject todayTempInfo = tempForecastList.getJSONObject(0);
            String highTemp = todayTempInfo.getString("high");
            String lowTemp = todayTempInfo.getString("low");
            String weatherType = todayTempInfo.getString("type");
            saveWeatherInfo(context, cityName, highTemp, lowTemp, weatherDesp, weatherType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的天气信息储存到sharedPreferences文件中
     * @param context
     * @param cityName
     * @param highTemp
     * @param lowTemp
     * @param weatherDesp
     * @param weatherType
     */
    public static void saveWeatherInfo(Context context,
                                       String cityName,
                                       String highTemp,
                                       String lowTemp,
                                       String weatherDesp,
                                       String weatherType) {
        SimpleDateFormat adf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("high_temp", highTemp);
        editor.putString("low_temp", lowTemp);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("weather_type", weatherType);
        editor.putString("current_date", adf.format(new Date()));
        editor.commit();
    }
}
