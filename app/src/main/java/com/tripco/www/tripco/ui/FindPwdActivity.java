package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.net.NetProcess;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPwdActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.find_id_et) EditText findIdEt;
    @BindView(R.id.clear_btn)
    ImageButton clearIdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        ButterKnife.bind(this);
        toolbarInit();
    }


    private void toolbarInit(){
        toolbarTitleTv.setText("비밀번호 초기화");
        toolbarRightBtn.setText("완료");
    }

    @OnClick(R.id.toolbar_right_btn)
    public void onViewClicked() {
        if (TextUtils.isEmpty(findIdEt.getText())) {
            findIdEt.setError("아이디를 입력해주세요.");
            findIdEt.requestFocus();
        }
        else{
            NetProcess.getInstance().netSendPw(new MemberModel(findIdEt.getText().toString()));
        }
    }

    @OnClick(R.id.clear_btn)
    public void onClickClearBtn(){
        findIdEt.setText("");
    }

}
