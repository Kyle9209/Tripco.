package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootActivity;
import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.util.U;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPwdActivity extends RootActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.originalPW) EditText originalPW;
    @BindView(R.id.newPwd) EditText newPW;
    @BindView(R.id.newPwdConfirm) EditText newPWConfirm;
    @BindView(R.id.check_pwd_line) LinearLayout checkPwdLine;
    @BindView(R.id.new_pwd_line) LinearLayout newPwdLine;
    private boolean originalPwdCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        toolbarInit();
        U.getInstance().getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void ottoBus(String str){
        if(str.equals("pwdCheckSuccess")){
            stopPD();
            originalPwdCheck = true;
            checkPwdLine.setVisibility(View.GONE);
            newPwdLine.setVisibility(View.VISIBLE);
            toolbarRightBtn.setText("완료");
        }
        if(str.equals("pwdFailed")){
            stopPD();
        }
        if(str.equals("pwdChangeSuccess")){
            Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            stopPD();
            finish();
        }
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("비밀번호 변경");
        toolbarRightBtn.setText("다음");
    }

    @OnClick({R.id.toolbar_right_btn})
    public void onViewClicked(View view) {
        if(!originalPwdCheck){
            originalPwdCheck();
        } else {
            boolean cancel = false;
            View focusView = null;

            newPW.setError(null);
            newPWConfirm.setError(null);

            String StrNewPwd = newPW.getText().toString();
            String StrPwdConfirm = newPWConfirm.getText().toString();

            //새 비밀번호가 빈칸일 경우 알려주기
            if (TextUtils.isEmpty(StrNewPwd) || !isPasswordValid(StrNewPwd)) {
                newPW.setError(getText(R.string.error_invalid_password));
                focusView = newPW;
                cancel = true;
            }
            // 새 비밀번호 확인이 빈칸일 경우
            if (TextUtils.isEmpty(StrPwdConfirm) || !isPasswordValid(StrPwdConfirm)) {
                newPWConfirm.setError(getText(R.string.error_invalid_password));
                focusView = newPWConfirm;
                cancel = true;
            }
            if (!isConfirmPassword(StrNewPwd,StrPwdConfirm)) {
                newPWConfirm.setError(getText(R.string.error_incorrect_password));
                focusView = newPWConfirm;
                cancel = true;
            }
            if (cancel) {
                focusView.requestFocus();
            } else {
                showPD();
                NetProcess.getInstance().netPassword(
                        new MemberModel(U.getInstance().getUserModel().getUser_id(), StrNewPwd), "change");
            }
        }
    }

    public void originalPwdCheck() {
        originalPW.setError(null);

        String original = originalPW.getText().toString();

        // 비밀번호 미입력이거나 짧으면 알려줌
        if (TextUtils.isEmpty(original) || !isPasswordValid(original)) {
            originalPW.setError(getString(R.string.error_invalid_password));
            originalPW.requestFocus();
        } else {
            showPD();
            NetProcess.getInstance().netPassword(
                    new MemberModel(U.getInstance().getUserModel().getUser_id(), original), "check");
        }
    }

    public boolean isConfirmPassword(String newPwd, String newPwdConfirm) {
        return newPwd.equals(newPwdConfirm);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }
}

