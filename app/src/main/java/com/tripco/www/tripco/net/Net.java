package com.tripco.www.tripco.net;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.tripco.www.tripco.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Net {
    private static Net ourInstance = new Net();

    public static Net getInstance() {
        return ourInstance;
    }

    private Net() {
        final long startTime = System.currentTimeMillis();
        // 원격 구성 정보 져오기!
        // 1. 원격 구성 객체 획득
        final FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        // 2. 설정
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        config.setConfigSettings(configSettings);
        // 3. 패치
        // 개발자 모드에서는 0으로, 상용에서는 3600
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (config.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        config.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // 4. 성공했을 경우에만 실제 fetch 진행
                if( task.isSuccessful() ){
                    // 실제 패치
                    config.activateFetched();
                    // 5. 획득
                    String MAIN_SERVER_DOMAIN = config.getString("MAIN_SERVER_DOMAIN");
                    Log.i("T", "메인서버주소:" + MAIN_SERVER_DOMAIN + ":" + ((System.currentTimeMillis()-startTime)));
                }else{
                    // 대체 정보로 이동
                }
            }
        });
    }

    // retrofit 통신 객체
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://13.124.11.192:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public Retrofit getRetrofit() {
        return retrofit;
    }

   /* StoresFactoryIm apiIm;

    public StoresFactoryIm getApiIm() {
        if (apiIm == null) {
            apiIm = retrofit.create(StoresFactoryIm.class);
        }
        return apiIm;
    }*/
}
