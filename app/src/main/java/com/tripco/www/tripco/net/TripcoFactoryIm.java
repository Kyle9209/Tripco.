package com.tripco.www.tripco.net;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripcoFactoryIm {
    // 로그인
    @POST("/login")
    Call<Res_ResultCode> login(@Body Req_Login req);

    // 회원가입
    @POST("/join")
    Call<Res_ResultCode> join(@Body Req_Join req);

    /*// 로그인
    @POST("/login")
    Call<RequestModel> login2(@Body Req_Login req);

    // 회원가입
    @POST("/join")
    Call<RequestModel<MemberModel>> join2(@Body Req_Join req);*/
}

