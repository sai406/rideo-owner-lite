package com.mstech.rideioliteowner.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages all shared preferences keys with getter and setter methods.
 *
 * @author PurpleTalk, Inc.
 */
public class SharedPreferenceUtils {
    protected Context mContext;
    private static String Preference_Name = "RideioSharedpreference";
    private static int Preference_Mode = Context.MODE_PRIVATE;
    protected SharedPreferences mSettings;
    protected static SharedPreferences.Editor mEditor;

    public SharedPreferenceUtils(Context ctx, String prefFileName) {
        mContext = ctx;
        mSettings = mContext.getSharedPreferences(prefFileName,
                Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
    }

    public static void savePreference(Context context, String key, String value) {
        context.getSharedPreferences(Preference_Name, Preference_Mode).edit().putString(key, value).commit();
    }

    public static String getPreference(Context context, String key, String defaultValue) {
        String toReturn = context.getSharedPreferences(Preference_Name, Preference_Mode).getString(key, defaultValue);
        if (toReturn == null) {
            toReturn = "";
        }
        return toReturn;
    }

    //    public static void savePreference(Context context, String key, boolean value) {
//        context.getSharedPreferences(Preference_Name, Preference_Mode).edit().putBoolean(key, value).commit();
//    }
//
//    public static boolean getPreference(Context context, String key, boolean defaultValue) {
//        return context.getSharedPreferences(Preference_Name, Preference_Mode).getBoolean(key, defaultValue);
//    }
//
//    public static void savePreference(Context context, String key, int value) {
//        context.getSharedPreferences(Preference_Name, Preference_Mode).edit().putInt(key, value).commit();
//    }
//
//    public static int getPreference(Context context, String key, int defaultValue) {
//        int toReturn;
//        toReturn = context.getSharedPreferences(Preference_Name, Preference_Mode).getInt(key, defaultValue);
//        return toReturn;
//    }
//
//    public static void savePreference(Context context, String key, Set<String> value) {
//        context.getSharedPreferences(Preference_Name, Preference_Mode).edit().putStringSet(key, value).commit();
//    }
//
//    public static Set<String> getPreference(Context context, String key, Set<String> defaultValue) {
//        return context.getSharedPreferences(Preference_Name, Preference_Mode).getStringSet(key, defaultValue);
//    }
//
    public static void clear(Context context) {
        context.getSharedPreferences(Preference_Name, Preference_Mode).edit().clear().commit();
    }
//
//    public static void savePreferenceGBCODE(Context context, String key, String value) {
//        context.getSharedPreferences(Preference_Name, Preference_Mode).edit().putString(key, value).commit();
//    }
//
//    public static String getPreferenceGBCODE(Context context, String key, String defaultValue) {
//        String toReturn = context.getSharedPreferences(Preference_Name, Preference_Mode).getString(key, defaultValue);
//        if (toReturn == null) {
//            toReturn = "";
//        }
//        return toReturn;
//    }

    public static void removeValue(String key) {
        if (mEditor != null) {
            mEditor.remove(key).commit();
        }
    }

    public boolean clear() {
        try {
            mEditor.clear().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}