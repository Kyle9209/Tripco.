package com.tripco.www.tripco;

/**
 * Created by Tacademy on 2017-08-04.
 */

import android.app.Application;

import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;

/**
 * Created by Tacademy on 2017-07-14.
 * Application을 재정의할때 사용 -> extends Application
 * 왜?
 * - 자체적으로 가용 메모리를 확보하여 사용하는 라이브러리 류
 * - 메소드의 개수 65536개를 초과할 경우 Dex 처리를 하는데 이 경우
 * - 맨 처음에 로드시킨 후 여러 곳에서 사용해야 할 경우
 * => context 객체를 전역적으로 처리해야 할 경우
 */

public class CameraApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        // 파파라조 라이브러리 등록
        RxPaparazzo.register(this);
    }
}
