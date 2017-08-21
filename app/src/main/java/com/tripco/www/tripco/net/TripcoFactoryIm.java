package com.tripco.www.tripco.net;

import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripcoFactoryIm {
    // 회원데이터 가져오기
    @POST("/simple")
    Call<ResponseModel<MemberModel>> simple(@Body MemberModel req);

    // 로그인
    @POST("/login")
    Call<ResponseModel<MemberModel>> login(@Body MemberModel req);

    // 회원가입
    @POST("/join")
    Call<ResponseModel<MemberModel>> join(@Body MemberModel req);

    // 닉네임 변경
    @POST("/nick")
    Call<ResponseModel> nick(@Body MemberModel req);

    // 비밀번호 확인
    @POST("/check_pw")
    Call<ResponseModel> check_pw(@Body MemberModel req);

    // 비밀번호 변경
    @POST("/change_pw")
    Call<ResponseModel> change_pw(@Body MemberModel req);
}

