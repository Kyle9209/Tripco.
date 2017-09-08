package com.tripco.www.tripco.net;

import com.tripco.www.tripco.model.AlarmModel;
import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.model.ResponseArrayModel;
import com.tripco.www.tripco.model.ResponseModel;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.model.TripModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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

    // 회원탈퇴
    @POST("/stop")
    Call<ResponseModel> stop(@Body MemberModel req);

    //사진 업로드
    @Multipart
    @POST("change_img")
    Call<ResponseModel<ScheduleModel>> change_img(@Part MultipartBody.Part part, @Part("user_id") RequestBody requestBody2);

    // 닉네임 변경
    @POST("/nick")
    Call<ResponseModel> nick(@Body MemberModel req);

    // 비밀번호 확인
    @POST("/check_pw")
    Call<ResponseModel> check_pw(@Body MemberModel req);

    // 비밀번호 변경
    @POST("/change_pw")
    Call<ResponseModel> change_pw(@Body MemberModel req);

    // 여행 리스트
    @POST("/list_trip")
    Call<ResponseArrayModel<TripModel>> list_trip(@Body MemberModel req);

    // 여행 만들기
    @POST("/create_trip")
    Call<ResponseModel> create_trip(@Body TripModel req);

    // 파트너 찾기
    @POST("/find_partner")
    Call<ResponseModel<MemberModel>> find_partner(@Body TripModel req);

    // 여행 수정
    @POST("/update_trip")
    Call<ResponseModel> update_trip(@Body TripModel req);

    // 여행 삭제
    @POST("/delete_trip")
    Call<ResponseModel> delete_trip(@Body TripModel req);

    // 일정 생성
    @POST("/create_item")
    Call<ResponseModel> create_item(@Body ScheduleModel req);

    // 일정 삭제
    @POST("/delete_item")
    Call<ResponseModel> delete_item(@Body ScheduleModel req);

    // 일정 수정
    @POST("/update_item")
    Call<ResponseModel> update_item(@Body ScheduleModel req);

    // 일정 리스트
    @POST("/list_item")
    Call<ResponseArrayModel<ScheduleModel>> list_item(@Body ScheduleModel req);

    // 최종 일정 추가/삭제
    @POST("/check_item")
    Call<ResponseModel> check_item(@Body ScheduleModel req);

    // 최종일정 시간변경
    @POST("/time_final")
    Call<ResponseModel> time_final(@Body ScheduleModel req);

    // 알림 리스트
    @POST("/list_notice")
    Call<ResponseArrayModel<AlarmModel>> list_notice(@Body MemberModel req);

    // 비밀번호 찾기
    @POST("/send_pw")
    Call<ResponseModel> send_pw(@Body MemberModel req);
}

