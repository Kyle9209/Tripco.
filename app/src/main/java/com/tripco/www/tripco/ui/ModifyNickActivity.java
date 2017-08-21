package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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

public class ModifyNickActivity extends RootActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.nickName_et) EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick);
        ButterKnife.bind(this);
        nickname.setText(getIntent().getStringExtra("nickName"));
        nickname.setSelection(nickname.length());
        toolbarInit();
        U.getInstance().getBus().register(this);
    }

    @Subscribe
    public void ottoBus(String str){
        if(str.equals("nickChangeSuccess")) {
            U.getInstance().getMemberModel().setUser_nick(nickname.getText().toString());
            U.getInstance().getBus().post("nickChange");
            Intent intent = new Intent();
            intent.putExtra("resultSetting", nickname.getText().toString());
            this.setResult(2, intent);
            stopPD();
            finish();
        } else if(str.equals("nickChangeFailed")) {
            stopPD();
            Toast.makeText(this, "죄송합니다.\n잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("닉네임 설정");
        toolbarRightBtn.setText("완료");
    }

    //완료버튼 누르면 설정한 닉네임 전달해주고 끝내기
    @OnClick(R.id.toolbar_right_btn)
    public void onViewClicked() {
        if(TextUtils.isEmpty(nickname.getText())){
            nickname.setError("닉네임을 입력해주세요.");
            nickname.requestFocus();
        } else {
            showPD();
            NetProcess.getInstance().netNick(new MemberModel(
                    U.getInstance().getMemberModel().getUser_id(),
                    nickname.getText().toString(),
                    U.getInstance().getMemberModel().getUser_pw()
                    )
            );
        }
    }

    @OnClick(R.id.clear_nick_btn)
    public void onClickClearBtn(){
        nickname.setText("");
    }
}
