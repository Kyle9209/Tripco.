package com.tripco.www.tripco.net;

import android.widget.Toast;

import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.model.ResponseArrayModel;
import com.tripco.www.tripco.model.ResponseModel;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetProcess {
    private static final NetProcess ourInstance = new NetProcess();
    public static NetProcess getInstance() {
        return ourInstance;
    }
    private NetProcess() {}

    public void netLoginJoinSimple(MemberModel req, String str) {
        Call<ResponseModel<MemberModel>> res = null;
        if(str.equals("login")){
            res = Net.getInstance().getApiIm().login(req);
        } else if(str.equals("join")) {
            res = Net.getInstance().getApiIm().join(req);
        } else if(str.equals("simple")){
            res = Net.getInstance().getApiIm().simple(req);
        }
        res.enqueue(new Callback<ResponseModel<MemberModel>>() {
            @Override
            public void onResponse(Call<ResponseModel<MemberModel>> call, Response<ResponseModel<MemberModel>> response) {
                // 성공처리
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 1) { // 성공 ->
                        if(str.equals("simple")){
                            U.getInstance().setUserModel(
                                    new MemberModel(
                                            response.body().getResult().getUser_no(),
                                            response.body().getResult().getUser_id(),
                                            response.body().getResult().getUser_pw(),
                                            response.body().getResult().getUser_nick(),
                                            response.body().getResult().getUser_token(),
                                            response.body().getResult().getUser_uuid(),
                                            response.body().getResult().getUser_image(),
                                            response.body().getResult().getUser_yn()
                                    )
                            );
                            U.getInstance().getBus().post("getUserInfo");
                        } else {
                            Toast.makeText(U.getInstance().getContext(), response.body().getResult().getUser_nick() + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                            U.getInstance().setString("email", response.body().getResult().getUser_id());
                            U.getInstance().setBoolean("login", true);
                            U.getInstance().getBus().post("loginSuccess");
                        }
                    } else if (response.body().getCode() == 0) { // 실패 -> 실패메세지 출력
                        Toast.makeText(U.getInstance().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        U.getInstance().getBus().post("loginFailed");
                    }
                } else {
                    // 실패처리
                    Toast.makeText(U.getInstance().getContext(), "서버통신 실패-1", Toast.LENGTH_SHORT).show();
                    U.getInstance().getBus().post("loginFailed");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel<MemberModel>> call, Throwable t) {
                //실패처리
                Toast.makeText(U.getInstance().getContext(), "서버통신 실패-2", Toast.LENGTH_SHORT).show();
                U.getInstance().getBus().post("loginFailed");
                U.getInstance().log(t.toString());
            }
        });
    }

    public void netNick(MemberModel req){
        Call<ResponseModel> res = Net.getInstance().getApiIm().nick(req);
        res.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.isSuccessful()){
                    if(response.body().getCode() == 1) {
                        Toast.makeText(U.getInstance().getContext(), "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        U.getInstance().getBus().post("nickChangeSuccess");
                    } else {
                        Toast.makeText(U.getInstance().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        U.getInstance().getBus().post("nickChangeFailed");
                    }
                } else {
                    Toast.makeText(U.getInstance().getContext(), "서버통신 실패-1", Toast.LENGTH_SHORT).show();
                    U.getInstance().getBus().post("nickChangeFailed");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(U.getInstance().getContext(), "서버통신 실패-2", Toast.LENGTH_SHORT).show();
                U.getInstance().getBus().post("nickChangeFailed");
            }
        });
    }

    public void netPassword(MemberModel req, String str){
        Call<ResponseModel> res = null;
        if(str.equals("check")) {
            res = Net.getInstance().getApiIm().check_pw(req);
        } else if(str.equals("change")){
            res = Net.getInstance().getApiIm().change_pw(req);
        }
        res.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.isSuccessful()){
                    if(response.body().getCode() == 1) {
                        if(str.equals("check")) U.getInstance().getBus().post("pwdCheckSuccess");
                        else if(str.equals("change")) U.getInstance().getBus().post("pwdChangeSuccess");
                    } else {
                        Toast.makeText(U.getInstance().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        U.getInstance().getBus().post("pwdFailed");
                    }
                } else {
                    Toast.makeText(U.getInstance().getContext(), "서버통신 실패-1", Toast.LENGTH_SHORT).show();
                    U.getInstance().getBus().post("pwdFailed");
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(U.getInstance().getContext(), "서버통신 실패-2", Toast.LENGTH_SHORT).show();
                U.getInstance().getBus().post("pwdFailed");
            }
        });
    }

    public void netPartner(TripModel req){
        Call<ResponseModel<MemberModel>> res = Net.getInstance().getApiIm().find_partner(req);
        res.enqueue(new Callback<ResponseModel<MemberModel>>() {
            @Override
            public void onResponse(Call<ResponseModel<MemberModel>> call, Response<ResponseModel<MemberModel>> response) {
                if(response.isSuccessful()){
                    if(response.body().getCode() == 1){
                        U.getInstance().setPartnerModel(new MemberModel(
                                0,
                                response.body().getResult().getUser_id(),
                                null,
                                response.body().getResult().getUser_nick(),
                                null,
                                null,
                                response.body().getResult().getUser_image(),
                                0));
                        U.getInstance().getBus().post("findPartnerSuccess");
                    } else {
                        Toast.makeText(U.getInstance().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        U.getInstance().getBus().post("responseFailed");
                    }
                } else {
                    Toast.makeText(U.getInstance().getContext(), "서버통신 실패-1", Toast.LENGTH_SHORT).show();
                    U.getInstance().getBus().post("responseFailed");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel<MemberModel>> call, Throwable t) {
                Toast.makeText(U.getInstance().getContext(), "서버통신 실패-2", Toast.LENGTH_SHORT).show();
                U.getInstance().getBus().post("responseFailed");
            }
        });
    }

    public void netCrUpDeTrip(TripModel req, String str){
        Call<ResponseModel> res = null;
        if(str.equals("생성")) res = Net.getInstance().getApiIm().create_trip(req);
        else if(str.equals("수정")) res = Net.getInstance().getApiIm().update_trip(req);
        else if(str.equals("삭제")) res = Net.getInstance().getApiIm().delete_trip(req);
        res.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.isSuccessful()){
                    if(response.body().getCode() == 1){
                        netListTrip(new MemberModel(U.getInstance().getUserModel().getUser_id()));
                        Toast.makeText(U.getInstance().getContext(), "여행이 "+str+"되었습니다.", Toast.LENGTH_SHORT).show();
                        U.getInstance().getBus().post("responseSuccess");
                    } else {
                        Toast.makeText(U.getInstance().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        U.getInstance().getBus().post("responseFailed");
                    }
                } else {
                    Toast.makeText(U.getInstance().getContext(), "서버통신 실패-1", Toast.LENGTH_SHORT).show();
                    U.getInstance().getBus().post("responseFailed");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(U.getInstance().getContext(), "서버통신 실패-2", Toast.LENGTH_SHORT).show();
                U.getInstance().getBus().post("responseFailed");
            }
        });
    }

    public void netListTrip(MemberModel req){
        Call<ResponseArrayModel<TripModel>> res = Net.getInstance().getApiIm().list_trip(req);
        res.enqueue(new Callback<ResponseArrayModel<TripModel>>() {
            @Override
            public void onResponse(Call<ResponseArrayModel<TripModel>> call, Response<ResponseArrayModel<TripModel>> response) {
                if(response.isSuccessful()){
                    if(response.body().getCode() == 1) {
                        ArrayList<TripModel> list = new ArrayList<>();
                        for(int i=0; i < response.body().getResult().size(); i++) {
                            list.add(new TripModel(
                                    response.body().getResult().get(i).getTrip_no(),
                                    response.body().getResult().get(i).getTrip_title(),
                                    response.body().getResult().get(i).getStart_date(),
                                    response.body().getResult().get(i).getEnd_date(),
                                    response.body().getResult().get(i).getUser_id(),
                                    response.body().getResult().get(i).getPartner_id(),
                                    response.body().getResult().get(i).getHashtag(),
                                    null
                            ));
                        }
                        U.getInstance().setTripListModel(list);
                        U.getInstance().getBus().post("tripListInit");
                    } else {
                        Toast.makeText(U.getInstance().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        U.getInstance().getBus().post("tripListFailed");
                    }
                } else {
                    Toast.makeText(U.getInstance().getContext(), "서버통신 실패-1", Toast.LENGTH_SHORT).show();
                    U.getInstance().getBus().post("tripListFailed");
                }
            }
            @Override
            public void onFailure(Call<ResponseArrayModel<TripModel>> call, Throwable t) {
                Toast.makeText(U.getInstance().getContext(), "서버통신 실패-2", Toast.LENGTH_SHORT).show();
                U.getInstance().getBus().post("tripListFailed");
            }
        });
    }
}
