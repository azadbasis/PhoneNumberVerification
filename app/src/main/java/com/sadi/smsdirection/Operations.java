package com.sadi.smsdirection;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by NanoSoft on 12/13/2017.
 */

public class Operations {



    public static void SaveToSharedPreference(Context ctx, String key, String value) {
        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static SharedPreferences sharedPreferences;

    public static String getStringFromSharedPreference(Context ctx, String key) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void IntSaveToSharedPreference(Context ctx, String key, int value) {
        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static int getIntegerSharedPreference(Context ctx, String key, int defaultValue) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }





}
