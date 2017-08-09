package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NicknameActivity extends AppCompatActivity {
    @BindView(R.id.complete_btn) Button completeBtn;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nickname) EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);
        ButterKnife.bind(this);

        nickname.setText(getIntent().getStringExtra("nickName"));
    }

    //완료버튼 누르면 설정한 닉네임 전달해주고 끝내기
    @OnClick(R.id.complete_btn)
    public void onViewClicked() {
        if(TextUtils.isEmpty(nickname.getText())){
            nickname.setError("닉네임을 입력해주세요.");
            nickname.requestFocus();
        } else {
            Intent intent = new Intent();
            intent.putExtra("resultSetting", nickname.getText().toString());
            this.setResult(2, intent);
            finish();
        }
    }

    @OnClick(R.id.clear_nick_btn)
    public void onClickClearBtn(){
        nickname.setText("");
    }
}
