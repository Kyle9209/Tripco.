package com.tripco.www.tripco.net;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Net {
    private static Net ourInstance = new Net();
    public static Net getInstance() {
        return ourInstance;
    }
    private Net() { }

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
