package com.tripco.www.tripco.net;

import android.widget.Toast;

import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.model.ResponseModel;
import com.tripco.www.tripco.util.U;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetProcess {
    private static final NetProcess ourInstance = new NetProcess();
    public static NetProcess getInstance() {
        return ourInstance;
    }
    private NetProcess() {}

    public void netLoginJoin(MemberModel req, String str) {
        Call<ResponseModel<MemberModel>> res = null;
        if(str.equals("login")){
            res = Net.getInstance().getApiIm().login(req);
        } else if(str.equals("join")) {
            res = Net.getInstance().getApiIm().join(req);
        }
        res.enqueue(new Callback<ResponseModel<MemberModel>>() {
            @Override
            public void onResponse(Call<ResponseModel<MemberModel>> call, Response<ResponseModel<MemberModel>> response) {
                // 성공처리
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 1) { // 성공 ->
                        U.getInstance().setMemberModel(
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
                        Toast.makeText(U.getInstance().getContext(), response.body().getResult().getUser_nick() + "님\n환영합니다.", Toast.LENGTH_SHORT).show();
                        U.getInstance().setBoolean("login", true);
                        U.getInstance().getBus().post("loginSuccess");
                    } else if (response.body().getCode() == 0) { // 실패 -> 실패메세지 출력
                        Toast.makeText(U.getInstance().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 실패처리
                    Toast.makeText(U.getInstance().getContext(), "실패-1", Toast.LENGTH_SHORT).show();
                    U.getInstance().getBus().post("loginFailed");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel<MemberModel>> call, Throwable t) {
                //실패처리
                Toast.makeText(U.getInstance().getContext(), "실패-2", Toast.LENGTH_SHORT).show();
                U.getInstance().getBus().post("loginFailed");
            }
        });
    }
}
