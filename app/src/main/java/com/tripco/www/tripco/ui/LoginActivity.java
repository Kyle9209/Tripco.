package com.tripco.www.tripco.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.model.LoginModel;
import com.tripco.www.tripco.net.Net;
import com.tripco.www.tripco.net.Req_Login;
import com.tripco.www.tripco.net.Res_ResultCode;
import com.tripco.www.tripco.util.U;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.email) EditText mEmailView;
    @BindView(R.id.password) EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        toolbarInit();

        mPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mPasswordView.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("로그인");
        toolbarRightBtn.setText("완료");
    }

    @OnClick(R.id.toolbar_right_btn)
    public void onClickEmailSignInButton(){
        attemptLogin();
    }

    private void attemptLogin() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else { // 서버로
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
           //connectServer(email, password);
        }
    }

    private void connectServer(String email, String password){
        Req_Login req_login = new Req_Login();
        req_login.setLoginModel(new LoginModel(email, password));

        Call<Res_ResultCode> res = Net.getInstance().getApiIm().login(req_login);

        res.enqueue(new Callback<Res_ResultCode>() {
            @Override
            public void onResponse(Call<Res_ResultCode> call, Response<Res_ResultCode> response) {
                if(response.isSuccessful()) { // 성공
                    if(response.body() != null){ // 내용이 있을 때
                        U.getInstance().log("로그인 성공");
                        Toast.makeText(LoginActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                    }  else { // 비어 있을 때
                        U.getInstance().log("통신 실패1 : 내용이 비어있음");
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                } else { // 실패
                    try {
                        U.getInstance().log("통신 실패2 : " + response.errorBody().string());
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        U.getInstance().log("통신 실패3 : " + e.toString());
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Res_ResultCode> call, Throwable t) {
                U.getInstance().log("통신 실패 onFailure : " + t.getLocalizedMessage());
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}

