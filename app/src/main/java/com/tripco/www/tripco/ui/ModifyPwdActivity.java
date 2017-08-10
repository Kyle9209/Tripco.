package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPwdActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.originalPW) EditText originalPW;
    @BindView(R.id.newPW) EditText newPW;
    @BindView(R.id.newPWconfirm) EditText newPWConfirm;
    private boolean originalPwdCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        toolbarInit();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("비밀번호 변경");
        toolbarRightBtn.setText("다음");
    }

    @OnClick({R.id.toolbar_right_btn})
    public void onViewClicked(View view) {
        if(!originalPwdCheck){
            modifyPW();
        } else {
            boolean cancel = false;
            View focusView = null;

            newPW.setError(null);
            newPWConfirm.setError(null);
            String newpw = newPW.getText().toString();
            String newpwConfirm = newPWConfirm.getText().toString();

            //새 비밀번호가 빈칸일 경우 알려주기
            if (TextUtils.isEmpty(newpw))
            {
                newPW.setError("This filed is required");
                focusView = newPW;
                cancel = true;
                if (cancel) {focusView.requestFocus();}
            }
            // 새 비밀번호 확인이 빈칸일 경우
            else if (TextUtils.isEmpty(newpwConfirm))
            {
                newPWConfirm.setError("This filed is required");
                focusView = newPWConfirm;
                cancel = true;
                if (cancel) {focusView.requestFocus();}
            }
            else if (!isConfirmPassword(newpw,newpwConfirm))
            {
                newPWConfirm.setError("비밀번호가 서로 다릅니다.");
                focusView = newPWConfirm;
                cancel = true;
                if (cancel) {focusView.requestFocus();}
            }
            else
                finish();
        }
    }

    public void modifyPW() {
        originalPW.setError(null);

        boolean cancel = false;
        View focusView = null;

        String original = originalPW.getText().toString();


        // 비밀번호 미입력시 알려주기
        if (TextUtils.isEmpty(original)) {
            originalPW.setError(getString(R.string.error_field_required));
            focusView = originalPW;
            cancel = true;
            if (cancel) { // 하나라도 걸리면 다시
                focusView.requestFocus();
            } //else 모두 통과하면 서버로
        } else {
            originalPwdCheck = true;
            toolbarRightBtn.setText("완료");
            originalPW.setVisibility(View.GONE);
            newPW.setVisibility(View.VISIBLE);
            newPWConfirm.setVisibility(View.VISIBLE);
        }
    }

    public boolean isConfirmPassword(String newpw, String newpwConfrim) {
        return newpw.equals(newpwConfrim);
    }
}

