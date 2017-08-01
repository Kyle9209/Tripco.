package com.tripco.www.tripco.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

/**
 * Created by kkmnb on 2017-07-06.
 */

public class U {
    private static U ourInstance = new U();
    public static U getInstance() {
        return ourInstance;
    }
    private U() {
    }

    // 로그출력
    final String TAG = "T";
    public void log(String msg){
        if(msg != null) {
            Log.i(TAG, msg);
        }
    }

    // UUID 획득 : 사용자 고유 기기 정보 획득 동일 기종으로 가입했다면 앱을 삭제후 재설치해도 별도 가입처리 X
    public String getUUID(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice  = "" + tm.getDeviceId();
        String tmSerial  = "" + tm.getSimSerialNumber();
        String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        UUID uuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return  uuid.toString();
    }

    // 영속저장 =============================================================================
    String SAVE_TAG = "ref";
    // String data
    public void setString(Context context, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SAVE_TAG, 0).edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String getString(Context context, String key){
        return context.getSharedPreferences(SAVE_TAG, 0).getString(key, "");
    }
    // Int data
    public void setInt(Context context, String key, int value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SAVE_TAG, 0).edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public int getInt(Context context, String key){
        return context.getSharedPreferences(SAVE_TAG, 0).getInt(key, 0);
    }
    // Boolean data
    public void setBoolean(Context context, String key, boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SAVE_TAG, 0).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public boolean getBoolean(Context context, String key){
        return context.getSharedPreferences(SAVE_TAG, 0).getBoolean(key, false);
    }
    // 영속저장 =============================================================================
}

