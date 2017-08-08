package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPasswordActivity extends AppCompatActivity {

    @BindView(R.id.modify_btn)
    Button modifyBtn;
    @BindView(R.id.complete_btn)
    Button completeBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.originalPW)
    EditText originalPW;
    @BindView(R.id.newPW)
    EditText newPW;
    @BindView(R.id.newPWconfirm)
    EditText newPWconfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.modify_btn, R.id.complete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.modify_btn:
                modifyPW();
                break;

            case R.id.complete_btn:

                boolean cancel = false;
                View focusView = null;

                newPW.setError(null);
                newPWconfirm.setError(null);
                String newpw = newPW.getText().toString();
                String newpwConfirm = newPWconfirm.getText().toString();

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
                    newPWconfirm.setError("This filed is required");
                    focusView = newPWconfirm;
                    cancel = true;
                    if (cancel) {focusView.requestFocus();}
                }
                else if (!isConfirmPassword(newpw,newpwConfirm))
                {
                    newPWconfirm.setError("비밀번호가 서로 다릅니다.");
                    focusView = newPWconfirm;
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
            } //else { //모두 통과하면 서버로
//            connectServer(email, password); }
        } else {
            modifyBtn.setVisibility(View.GONE);
            completeBtn.setVisibility(View.VISIBLE);
            originalPW.setVisibility(View.GONE);
            newPW.setVisibility(View.VISIBLE);
            newPWconfirm.setVisibility(View.VISIBLE);


        }
    }

    public boolean isConfirmPassword(String newpw, String newpwConfrim) {
        return newpw.equals(newpwConfrim);
    }

}

