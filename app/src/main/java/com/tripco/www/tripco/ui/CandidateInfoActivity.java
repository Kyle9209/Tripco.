package com.tripco.www.tripco.ui;

import android.content.Intent;
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
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.util.U;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CandidateInfoActivity extends AppCompatActivity
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
    @BindView(R.id.location_name) TextView locationName;
    @BindView(R.id.address) TextView address;
    @BindView(R.id.schedule_date_tv) TextView scheduleDateTv;
    @BindView(R.id.memo) TextView memo;
    @BindView(R.id.loading_img_pb) ProgressBar loadingImgPb;
    @BindView(R.id.open_url_tv) TextView openUrlTv;
    private ScheduleModel scheduleModel;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_info);
        ButterKnife.bind(this);
        scheduleModel = (ScheduleModel) getIntent().getSerializableExtra("scheduleModel");
        toolbarInit();

        if(!scheduleModel.getItem_placeid().equals("null")) getPlaceData();
        else loadingImgPb.setVisibility(View.GONE);

        if(scheduleModel.getItem_title().equals("")) tripTitle.setVisibility(View.GONE);
        else tripTitle.setText(scheduleModel.getItem_title());

        if(scheduleModel.getItem_check() == 1) checkCb.isChecked();
        if(scheduleModel.getCate_no() == 0) rb0.setChecked(true);
        if(scheduleModel.getCate_no() == 1) rb1.setChecked(true);
        if(scheduleModel.getCate_no() == 2) rb2.setChecked(true);
        scheduleDateTv.setText(scheduleModel.getSchedule_date());
        memo.setText(scheduleModel.getItem_memo());
        openUrlTv.setText(scheduleModel.getItem_url());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText(scheduleModel.getItem_title());
        toolbarRightBtn.setText("수정");
    }

    private void getPlaceData(){
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

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

    @OnClick({R.id.toolbar_right_btn, R.id.open_url_line, R.id.delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_right_btn:
                //수정페이지에서 완료 후 값을 받아오기 위해서
                Intent intent = new Intent(CandidateInfoActivity.this, ModifyCandidateInfoActivity.class);
                //기존의 값을 전달
                intent.putExtra("locationName", locationName.getText().toString());
                intent.putExtra("memo1", memo.getText().toString());
                startActivityForResult(intent, 2); //값을 가져오기로 위해서
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
                        },
                        "아니오", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });
                break;
        }
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
