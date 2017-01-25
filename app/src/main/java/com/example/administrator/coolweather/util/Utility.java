package com.example.administrator.coolweather.util;

import android.text.TextUtils;

import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.County;
import com.example.administrator.coolweather.model.Province;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
}
