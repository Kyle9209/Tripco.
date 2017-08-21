package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootActivity;
import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.util.U;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinActivity extends RootActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.join_email) EditText joinEmail;
    @BindView(R.id.join_password) EditText joinPassword;
    @BindView(R.id.confirm_password) EditText confirmPassword;
    @BindView(R.id.check_cb) CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        ButterKnife.bind(this);
        U.getInstance().getBus().register(this);
        toolbarInit();
    }

    @Subscribe
    public void ottoBus(String str){
        stopPD();
        if(str.equals("loginSuccess")) this.finish();
    }

    @Override
    protected void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("회원가입");
        toolbarRightBtn.setText("완료");
    }

    @OnClick(R.id.toolbar_right_btn)
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
            joinEmail.setError(getString(R.string.please_email));
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

        if(!checkBox.isChecked()){
            Toast.makeText(this, "이용약관에 체크해주세요.", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel) { // 하나라도 걸리면 다시
            if(focusView != null) {
                focusView.requestFocus();
            }
        } else { // 모두 통과하면 서버로
            connectServer(email, password);
        }
    }

    private void connectServer(String email, String password){
        showPD();
        NetProcess.getInstance().netLoginJoin(
                new MemberModel(
                        email,
                        password,
                        FirebaseInstanceId.getInstance().getToken(),
                        U.getInstance().getUUID(this),
                        email.split("@")[0]
                ), "join"
        );
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
