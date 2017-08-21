package com.tripco.www.tripco;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 만들어진 모든 엑티비티들의 부모
 * 모든 엑티비티가 가져야할 공통 사항을 구현해둠
 * ex) 진행율을 표시하는 프로그래스바, 변수
 */

public class RootActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ProgressDialog progressDialog;

    public void showPD(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("잠시만 기다려주세요.");
        }
        progressDialog.show();
    }

    public void stopPD(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
