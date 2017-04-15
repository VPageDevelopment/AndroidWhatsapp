package com.vpage.shareInfo.tools;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Preferences {

    private static final String TAG = Preferences.class.getName();

    static String preferenceName = "ShareInfo";

    public static void save(String Key, String Value) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.putString(Key, Value);
        editor.commit();
    }


    public static String get(String Key) {
        SharedPreferences preference;
        String text;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        text = preference.getString(Key, null);
        return text;
    }

    public static void clear(String Key) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.remove(Key);
        editor.commit();
    }

    public static void clearAll() {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.clear();
        editor.commit();
    }

    public static void saveAppInstallVariable(String key,boolean value){
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getAppInstallVariable(String key){
        SharedPreferences preference;
        boolean val;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        val = preference.getBoolean(key, false);
        return val;
    }

    public static void saveTimeForNotification(String key,long value){
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getTimeForNotification(String key){
        SharedPreferences preference;
        long val;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        val = preference.getLong(key, 0);
        return val;
    }

    public static void saveLocalNotificationMsg(String key, List<String> msgs){
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preference.edit();
        Set<String> set = new HashSet<>();
        set.addAll(msgs);
        editor.putStringSet(key, set);
        editor.commit();
    }

    public static List<String> getLoacalNotificationMsgs(String key){
        SharedPreferences preference;
        List<String> msgs=new ArrayList<>();
        preference = ShareApplication.getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>();
        set = preference.getStringSet(key, new HashSet<String>());
        msgs.addAll(set);
        return msgs;
    }
}
