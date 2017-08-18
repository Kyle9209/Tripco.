package com.tripco.www.tripco.net;

import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripcoFactoryIm {
    //로그인
    @POST("/login")
    Call<ResponseModel<MemberModel>> login(@Body MemberModel req);

    // 회원가입
    @POST("/join")
    Call<ResponseModel<MemberModel>> join(@Body MemberModel req);
}

