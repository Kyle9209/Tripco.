package com.tripco.www.tripco.ui;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.util.U;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CandidateInfoActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.location_name) TextView locationName;
    @BindView(R.id.address) TextView address;
    @BindView(R.id.memo1) TextView memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_info);
        ButterKnife.bind(this);
        toolbarInit();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("위치명");
        toolbarRightBtn.setText("수정");
    }

    @OnClick(R.id.toolbar_right_btn)
    public void onViewClicked() {
        //수정페이지에서 완료 후 값을 받아오기 위해서
        Intent intent = new Intent(CandidateInfoActivity.this, ModifyCandidateInfoActivity.class);
        //기존의 값을 전달
        intent.putExtra("locationName", locationName.getText().toString());
        intent.putExtra("memo1", memo.getText().toString());
        startActivityForResult(intent, 2); //값을 가져오기로 위해서
    }

    @OnClick(R.id.delete_btn)
    public void onClickBtn(){
        U.getInstance().showAlertDialog(this, "주의!", "해당 정보를 삭제하시겠습니까?",
                "예", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                },
                "아니오", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
    }

    //수정 페이지에서 받아온 결과 값으로 바꾸기
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2 && data != null) {
            String result_location = data.getStringExtra("resultSetting_location");
            locationName.setText(result_location);
            String result_memo = data.getStringExtra("resultSetting_memo");
            memo.setText(result_memo);
        }
    }
}
