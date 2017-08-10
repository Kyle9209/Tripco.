package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tripco.www.tripco.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyCandidateInfoActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.location_name) EditText locationName;
    @BindView(R.id.memo1) EditText memo1;
    @BindView(R.id.days_spin) Spinner daysSpin;
    String modifyLocation;
    String modifyMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_candidate_info);
        ButterKnife.bind(this);
        spinnerInit();
        toolbarInit();

        //기존의 값 받아오기
        locationName.setText(getIntent().getStringExtra("locationName"));
        memo1.setText(getIntent().getStringExtra("memo1"));
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("위치명");
        toolbarRightBtn.setText("완료");
    }

    @OnClick(R.id.toolbar_right_btn)
    public void onViewClicked() {
        //이 페이지에서 수정한 값 전달해서 변경하기
        modifyLocation = locationName.getText().toString(); //위치 이름 변경시
        modifyMemo = memo1.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("resultSetting_location", modifyLocation);
        intent.putExtra("resultSetting_memo", modifyMemo);
        this.setResult(2, intent);
        finish();
    }

    public void spinnerInit() {
        ArrayList<String> items = new ArrayList<>(Arrays.asList("1일차", "2일차"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.add("3일차");
        adapter.notifyDataSetChanged();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpin.setAdapter(adapter);
    }
}
