package com.tripco.www.tripco.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by kkmnb on 2017-07-07.
 * FCM 사용을 위한 구성요소중 유저의 토큰을 획득하는 서비스
 * token의 신규발급부터 변경시 이벤트를 전달하여 서버쪽으로 갱신하는 역할을 담당
 */

public class TripcoFirebaseInstanceIDService extends FirebaseInstanceIdService {
    // 토큰 갱신시 호출
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i("FCM", "22onTokenRefresh > " + refreshedToken);

        // 서버쪽으로 보내서 갱신 가능
    }
}
