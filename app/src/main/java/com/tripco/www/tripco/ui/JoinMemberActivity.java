package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.net.Net;
import com.tripco.www.tripco.net.Req_Join;
import com.tripco.www.tripco.net.Res_ResultCode;
import com.tripco.www.tripco.util.U;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinMemberActivity extends AppCompatActivity {
    @BindView(R.id.join_email) AutoCompleteTextView joinEmail;
    @BindView(R.id.join_password) EditText joinPassword;
    @BindView(R.id.confirm_password) EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.email_join_in_button)
    public void onClickEmailJoinInButton(){
        attemptJoin();
    }

    private void attemptJoin() {
        joinEmail.setError(null);
        joinPassword.setError(null);
        confirmPassword.setError(null); //추가2

        String email = joinEmail.getText().toString();
        String password = joinPassword.getText().toString();
        String confirmPwd = confirmPassword.getText().toString(); //추가2

        boolean cancel = false;
        View focusView = null;

        // 이메일 확인
        if (TextUtils.isEmpty(email)) {
            joinEmail.setError(getString(R.string.error_field_required));
            focusView = joinEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            joinEmail.setError(getString(R.string.error_invalid_email));
            focusView = joinEmail;
            cancel = true;
        }

        //비밀번호 칸이 비었으면 알림
        if (TextUtils.isEmpty(password)) {
            joinPassword.setError(getString(R.string.error_field_required));
            focusView = joinPassword;
            cancel = true;
        }

        //비밀번호 확인칸이 비었으면 알림
        if (TextUtils.isEmpty(confirmPwd)) {
            confirmPassword.setError(getString(R.string.error_field_required));
            focusView = confirmPassword;
            cancel = true;
        }

        // 비밀번호 확인이 일치하는지 체크
        if (!TextUtils.isEmpty(confirmPwd) && !isConfirmPassword(confirmPwd, password)) {
            confirmPassword.setError("비밀번호가 다릅니다.");
            focusView = confirmPassword;
            cancel = true;
        }

        // 확인비밀번호가 길이 확인
        if (!TextUtils.isEmpty(confirmPwd) && !isPasswordValid(confirmPwd)) {
            confirmPassword.setError(getString(R.string.error_invalid_password));
            focusView = confirmPassword;
            cancel = true;
        }

        // 비밀번호가 길이 확인
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            joinPassword.setError(getString(R.string.error_invalid_password));
            focusView = joinPassword;
            cancel = true;
        }

        if (cancel) { // 하나라도 걸리면 다시
            focusView.requestFocus();
        } else { // 모두 통과하면 서버로
            connectServer(email, password);
        }
    }

    private void connectServer(String email, String password){
        Req_Join req_join = new Req_Join();
        req_join.setMember(new MemberModel(
                email,
                password,
                FirebaseInstanceId.getInstance().getToken(),
                U.getInstance().getUUID(this),
                email.split("@")[0]
        ));

        Call<Res_ResultCode> res = Net.getInstance().getApiIm().join(req_join);

        res.enqueue(new Callback<Res_ResultCode>() {
            @Override
            public void onResponse(Call<Res_ResultCode> call, Response<Res_ResultCode> response) {
                if(response.isSuccessful()) { // 성공
                    if(response.body() != null){ // 내용이 있을 때
                        U.getInstance().log("회원가입 성공");
                    }  else { // 비어 있을 때
                        U.getInstance().log("통신 실패1 : 내용이 비어있음");
                    }
                } else { // 실패
                    try {
                        U.getInstance().log("통신 실패2 : " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        U.getInstance().log("통신 실패3 : " + e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<Res_ResultCode> call, Throwable t) {
                U.getInstance().log("통신 실패 onFailure : " + t.getLocalizedMessage());
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isConfirmPassword(String confirmPwd, String password) {
        return confirmPwd.equals(password);
    }
}
