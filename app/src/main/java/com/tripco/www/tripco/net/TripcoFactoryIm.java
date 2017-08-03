package com.tripco.www.tripco.net;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripcoFactoryIm {
    // 회원가입
    @POST("/join")
    Call<Res_Join> joinMember(@Body Req_Join req);
}
