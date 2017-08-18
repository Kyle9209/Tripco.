package com.tripco.www.tripco.net;

import com.tripco.www.tripco.util.U;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Net {
    private static Net ourInstance = new Net();
    public static Net getInstance() {
        return ourInstance;
    }
    private Net() {}

    // retrofit 통신 객체
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(U.getInstance().getString("MAIN_SERVER_DOMAIN"))
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public Retrofit getRetrofit() {
        return retrofit;
    }


    // 팩토리 설정
    private TripcoFactoryIm apiIm;

    public TripcoFactoryIm getApiIm() {
        if(apiIm == null){
            apiIm = retrofit.create(TripcoFactoryIm.class);
        }
        return apiIm;
    }
}
