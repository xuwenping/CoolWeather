package com.example.administrator.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/2/14.
 */
public class SharepreferencesUtilSystemSettings {

    public final static String  SETTING = "settingPrefs";

    public static void putValue(Context context, String key, Object object) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        if (object instanceof String) {
            editor.putString(key, (String)object);
        }
        else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean)object);
        }
        else if (object instanceof Integer) {
            editor.putInt(key, (Integer)object);
        }
        else if (object instanceof Float) {
            editor.putFloat(key, (Float)object);
        }
        else if (object instanceof Long) {
            editor.putLong(key, (Long)object);
        }

        editor.commit();
    }

    public static String getValue(Context context, String key, String defValue) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        String value = prefs.getString(key, defValue);
        return value;
    }

    public static Boolean getValue(Context context, String key, Boolean defValue) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        Boolean value = prefs.getBoolean(key, defValue);
        return value;
    }

    public static int getValue(Context context, String key, int defValue) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        int value = prefs.getInt(key, defValue);
        return value;
    }
}
