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
import com.tripco.www.tripco.model.TripDataModel;
import com.tripco.www.tripco.model.TripModel;

import java.util.ArrayList;
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

    // 관광, 맛집, 숙소
    public final static String category0 = "관광";
    public final static String category1 = "맛집";
    public final static String category2 = "숙소";

    // 해쉬태그 정보
    public final static String[] tags = {"#힐링", "#감성", "#먹방" ,"#쇼핑" , "#조용", "#활발", "#답사" ,
            "#레저" ,"#가성비" , "#여유로운", "#지식", "#트렌디", "#둘이서" , "#혼자서",
            "#커플" ,"#가족" ,"#우정" ,"#제주" , "#유럽" ,"#미국" ,"#동남아", "#일본" ,"#중국"};

    // GoogleApiClient 셋팅
    GoogleApiClient mGoogleApiClient = null;
    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }
    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    // GooglePlacesApi 에서 쓰는 리퀘스트 코드
    public final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    // 메인에서 로그인되어있으면 1번 가져와서 앱을 끌때까지 계속 가지고있는 회원정보
    MemberModel userModel;
    public MemberModel getUserModel() {
        return userModel;
    }
    public void setUserModel(MemberModel userModel) {
        this.userModel = userModel;
    }

    // 검색한 파트너정보
    MemberModel partnerModel;
    public MemberModel getPartnerModel() {
        return partnerModel;
    }
    public void setPartnerModel(MemberModel partnerModel) {
        this.partnerModel = partnerModel;
    }

    // 유저의 여행 리스트 정보
    ArrayList<TripModel> TripListModel;
    public ArrayList<TripModel> getTripListModel() {
        return TripListModel;
    }
    public void setTripListModel(ArrayList<TripModel> tripListModel) {
        this.TripListModel = tripListModel;
    }

    // 하나의 여행을 선택했을 때 계속 가지고 있어야하는 여행번호, 기간을 U에서 가지고 있음
    public TripDataModel tripDataModel = new TripDataModel();
}

