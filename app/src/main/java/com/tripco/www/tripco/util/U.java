package com.tripco.www.tripco.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Bus;
import com.tripco.www.tripco.model.MemberModel;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

public class U {
    private static U ourInstance = new U();
    public static U getInstance() {
        return ourInstance;
    }
    private U() {}

    // 로그출력
    final String TAG = "T";
    public void log(String msg) {
        if (msg != null) {
            Log.i(TAG, "=========================================================================");
            Log.i(TAG, msg);
            Log.i(TAG, "=========================================================================");
        }
    }

    // UUID 획득 : 사용자 고유 기기 정보 획득 동일 기종으로 가입했다면 앱을 삭제후 재설치해도 별도 가입처리 X
    public String getUUID(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        UUID uuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return uuid.toString();
    }

    // 영속저장 =============================================================================
    Context context;
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    String SAVE_TAG = "ref";
    // String data
    public void setString(String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SAVE_TAG, 0).edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String getString(String key) {
        return context.getSharedPreferences(SAVE_TAG, 0).getString(key, "");
    }
    // Int data
    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SAVE_TAG, 0).edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public int getInt(String key) {
        return context.getSharedPreferences(SAVE_TAG, 0).getInt(key, 0);
    }
    // Boolean data
    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SAVE_TAG, 0).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public boolean getBoolean(String key) {
        return context.getSharedPreferences(SAVE_TAG, 0).getBoolean(key, false);
    }
    // 영속저장 =============================================================================

    // 다이얼로그창 띄우기
    public void showAlertDialog(Context context, String title, String msg,
                                String pStr, DialogInterface.OnClickListener pOnClickListener,
                                String nStr, DialogInterface.OnClickListener nOnClickListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(pStr, pOnClickListener)
                .setNegativeButton(nStr, nOnClickListener);

        dialog.create().show();
    }

    // 오토버스
    Bus bus = new Bus();
    public Bus getBus() {
        return bus;
    }

    // 날짜 포맷 설정
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    // 스피너의 날짜 추출
    public String getDate(String str){
        String[] itemDate = str.split("\\(");
        return itemDate[1].substring(0, itemDate[1].length()-1);
    }

    // 관광, 맛집, 숙소
    public String category0 = "관광";
    public String category1 = "맛집";
    public String category2 = "숙소";
    public String getCategory0() {
        return category0;
    }
    public String getCategory1() {
        return category1;
    }
    public String getCategory2() {
        return category2;
    }

    // GoogleApiClient 셋팅
    private GoogleApiClient mGoogleApiClient = null;
    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }
    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    // 구글플레이스 코드
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public int getPLACE_AUTOCOMPLETE_REQUEST_CODE() {
        return PLACE_AUTOCOMPLETE_REQUEST_CODE;
    }

    // 메인 리스트 하나 클릭했을 때 계속 가지고 있어야하는 값
    private int tripNo;
    private String startDate;
    private String endDate;
    private String selectDate;
    private String spinnerDate;
    public int getTripNo() {
        return tripNo;
    }
    public void setTripNo(int tripNo) {
        this.tripNo = tripNo;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getSelectDate() {
        return selectDate;
    }
    public void setSelectDate(String selectDate) {
        this.selectDate = selectDate;
    }
    public String getSpinnerDate() {
        return spinnerDate;
    }
    public void setSpinnerDate(String spinnerDate) {
        this.spinnerDate = spinnerDate;
    }

    // 멤버정보 갖고있기
    private MemberModel memberModel;
    public MemberModel getMemberModel() {
        return memberModel;
    }
    public void setMemberModel(MemberModel memberModel) {
        this.memberModel = memberModel;
    }
}

