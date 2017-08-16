package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.util.U;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleInfoActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.place_img_iv) ImageView placeImgIv;
    @BindView(R.id.trip_title) TextView tripTitle;
    @BindView(R.id.check_cb) CheckBox checkCb;
    @BindView(R.id.rbs_rg) RadioGroup rbsGroup;
    @BindView(R.id.rb0) RadioButton rb0;
    @BindView(R.id.rb1) RadioButton rb1;
    @BindView(R.id.rb2) RadioButton rb2;
    @BindView(R.id.place_name_tv) TextView locationName;
    @BindView(R.id.place_address_tv) TextView address;
    @BindView(R.id.schedule_date_tv) TextView scheduleDateTv;
    @BindView(R.id.memo) TextView memo;
    @BindView(R.id.loading_img_pb) ProgressBar loadingImgPb;
    @BindView(R.id.open_url_tv) TextView openUrlTv;
    @BindView(R.id.time_tv) TextView timeTv;
    private ScheduleModel scheduleModel;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);
        ButterKnife.bind(this);
        scheduleModel = (ScheduleModel) getIntent().getSerializableExtra("scheduleModel");
        getScheduleData();
        toolbarInit();
        uiInit();
    }

    private void getScheduleData(){
        String sql = "select * from ScheduleList_Table where trip_no=" + scheduleModel.getTrip_no() +
                " and schedule_no="+ scheduleModel.getSchedule_no()+";";
        Cursor csr = DBOpenHelper.dbOpenHelper.getWritableDatabase().rawQuery(sql, null);
        while (csr.moveToNext()) {
            scheduleModel = new ScheduleModel(
                    csr.getInt(0),
                    csr.getInt(1),
                    csr.getString(2),
                    csr.getString(3),
                    csr.getInt(4),
                    csr.getString(5),
                    csr.getString(6),
                    csr.getString(7),
                    csr.getString(8),
                    csr.getString(9),
                    csr.getInt(10),
                    csr.getString(11)
            );
        }
        csr.close();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText(scheduleModel.getItem_title());
        toolbarRightBtn.setText("수정");
    }

    private void uiInit(){
        // placeId가 있으면 이미지, 위치명, 주소 입력 없으면 로딩끝
        if(!scheduleModel.getItem_placeid().equals("null")) getPlaceData();
        else loadingImgPb.setVisibility(View.GONE);
        // 제목없으면 타이틀 없애기
        if(scheduleModel.getItem_title().equals("")) tripTitle.setVisibility(View.GONE);
        else tripTitle.setText(scheduleModel.getItem_title());
        // 유형체크
        if(scheduleModel.getItem_check() == 1) checkCb.isChecked();
        if(scheduleModel.getCate_no() == 0) rb0.setChecked(true);
        if(scheduleModel.getCate_no() == 1) rb1.setChecked(true);
        if(scheduleModel.getCate_no() == 2) rb2.setChecked(true);
        // 날짜입력
        scheduleDateTv.setText(getIntent().getStringExtra("n") + "일차(" + scheduleModel.getSchedule_date().replace("-",".") + ")");
        // 메모입력
        memo.setText(scheduleModel.getItem_memo());
        // url입력
        openUrlTv.setText(scheduleModel.getItem_url());
        // 최종일정에서 들어오면 시간보이기
        if(getIntent().getBooleanExtra("fin", false)){
            if(scheduleModel.getItem_time() != null) timeTv.setText(scheduleModel.getItem_time());
            timeTv.setVisibility(View.VISIBLE);
        }
        // 체크확인
        if(scheduleModel.getItem_check() == 1) checkCb.setChecked(true);
    }

    private void getPlaceData(){
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, scheduleModel.getItem_placeid())
                .setResultCallback(places -> {
                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                        final Place myPlace = places.get(0);
                        locationName.setText(myPlace.getName());
                        address.setText(myPlace.getAddress());
                        placePhotosAsync();
                    } else {
                        U.getInstance().log("에러에러에러에러");
                    }
                    places.release();
                });
    }

    @OnClick({R.id.toolbar_right_btn, R.id.open_url_line, R.id.delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_right_btn: //수정페이지로
                Intent intent = new Intent(ScheduleInfoActivity.this, ModifyScheduleActivity.class);
                intent.putExtra("scheduleModel", scheduleModel);
                startActivityForResult(intent, 2);
                break;
            case R.id.open_url_line:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(openUrlTv.getText().toString())));
                } catch (Exception e) {
                    Toast.makeText(this, "잘못된 URL페이지이거나 이동할 URL페이지가 없습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.delete_btn:
                U.getInstance().showAlertDialog(this, "주의!", "해당 정보를 삭제하시겠습니까?",
                        "예", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            finish();
                        },
                        "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && data != null){
            getScheduleData();
            toolbarInit();
            uiInit();
        }
    }

    // 구글에서 사진을 비동기로 가져옴
    private void placePhotosAsync() {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, scheduleModel.getItem_placeid())
                .setResultCallback(photos -> {
                    if (!photos.getStatus().isSuccess()) return;
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    if (photoMetadataBuffer.getCount() > 0) {
                        photoMetadataBuffer.get(0)
                                .getScaledPhoto(mGoogleApiClient, placeImgIv.getWidth(), placeImgIv.getHeight())
                                .setResultCallback(mDisplayPhotoResultCallback);
                    }
                    photoMetadataBuffer.release();
                });
    }
    // 사진받기위한 콜백메소드
    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback = placePhotoResult -> {
        if (!placePhotoResult.getStatus().isSuccess()) return;
        loadingImgPb.setVisibility(View.GONE);
        placeImgIv.setImageBitmap(placePhotoResult.getBitmap());
    };
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
