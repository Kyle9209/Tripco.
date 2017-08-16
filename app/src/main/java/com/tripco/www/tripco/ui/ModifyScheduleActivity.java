package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rey.material.widget.Spinner;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.util.U;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyScheduleActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.place_img_iv) ImageView placeImgIv;
    @BindView(R.id.trip_title) EditText tripTitle;
    @BindView(R.id.check_cb) CheckBox checkCb;
    @BindView(R.id.rbs_rg) RadioGroup rbsGroup;
    @BindView(R.id.rb0) RadioButton rb0;
    @BindView(R.id.rb1) RadioButton rb1;
    @BindView(R.id.rb2) RadioButton rb2;
    @BindView(R.id.place_name_tv) TextView placeNameTv;
    @BindView(R.id.place_address_tv) TextView PlaceAddrTv;
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.memo) EditText memo;
    @BindView(R.id.loading_img_pb) ProgressBar loadingImgPb;
    @BindView(R.id.open_url_tv) TextView openUrlTv;
    @BindView(R.id.time_tv) TextView timeTv;
    private ScheduleModel scheduleModel;
    private GoogleApiClient mGoogleApiClient;
    private String placeId;
    private String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_schedule);
        ButterKnife.bind(this);
        scheduleModel = (ScheduleModel) getIntent().getSerializableExtra("scheduleModel");
        toolbarInit();
        uiInit();
        spinnerInit();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText(scheduleModel.getItem_title());
        toolbarRightBtn.setText("완료");
    }

    private void uiInit(){
        // placeId가 있으면 이미지, 위치명, 주소 입력 없으면 로딩끝
        if(!scheduleModel.getItem_placeid().equals("null")) {
            placeId = scheduleModel.getItem_placeid();
            getPlaceData();
        }
        else loadingImgPb.setVisibility(View.GONE);
        // lat, lng 얻기
        lat = scheduleModel.getItem_lat();
        lng = scheduleModel.getItem_long();
        // 제목없으면 타이틀 없애기
        if(scheduleModel.getItem_title().equals("")) tripTitle.setVisibility(View.GONE);
        else tripTitle.setText(scheduleModel.getItem_title());
        // 유형체크
        if(scheduleModel.getItem_check() == 1) checkCb.isChecked();
        if(scheduleModel.getCate_no() == 0) rb0.setChecked(true);
        if(scheduleModel.getCate_no() == 1) rb1.setChecked(true);
        if(scheduleModel.getCate_no() == 2) rb2.setChecked(true);
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

    @OnClick({R.id.toolbar_right_btn, R.id.place_info_line})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.toolbar_right_btn:
                updateSQLite(scheduleModel.getTrip_no(), scheduleModel.getSchedule_no(), "저장되었습니다.");
                U.getInstance().getBus().post("ChangeSelectDate");
                this.setResult(2, new Intent());
                finish();
                break;
            case R.id.place_info_line:
                onGooglePlaces();
                break;
        }
    }

    private void updateSQLite(int trip_no, int s_no, String str) {
        try {
            String sql = "update ScheduleList_Table set" +
                    " schedule_date = '"+U.getInstance().getDate(spinner.getSelectedItem().toString()).replace(".","-")+"', "+
                    " item_url = '"+openUrlTv.getText()+"', "+
                    " cate_no = "+getCategoryNum()+", "+
                    " item_lat = '"+lat+"', "+
                    " item_long = '"+lng+"', "+
                    " item_placeid = '"+placeId+"', "+
                    " item_title = '"+tripTitle.getText()+"', "+
                    " item_memo = '"+memo.getText()+"', "+
                    " item_check = "+getCheckNum()+", "+
                    " item_time = '"+timeTv.getText()+"' "+
                    " where trip_no = " + trip_no + " and schedule_no = " + s_no + " ;";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            U.getInstance().log(sql);
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCheckNum(){
        if(checkCb.isChecked()) return 1;
        else return 0;
    }

    private int getCategoryNum(){
        if(rb0.isChecked()) return 0;
        else if(rb1.isChecked()) return 1;
        else return 2;
    }

    private void onGooglePlaces(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, U.getInstance().getPLACE_AUTOCOMPLETE_REQUEST_CODE());
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override // 구글 플레이스에서 검색한 데이터 받아서 처리
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == U.getInstance().getPLACE_AUTOCOMPLETE_REQUEST_CODE()) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                placeId = place.getId();
                lat = "" + place.getLatLng().latitude;
                lng = "" + place.getLatLng().longitude;
                getPlaceData();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                U.getInstance().log("RESULT_ERROR 상태 : " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                U.getInstance().log("RESULT_CANCELED 상태");
            }
        }
    }

    public void spinnerInit() {
        Date start = null;
        Date end = null;
        try {
            start = U.getInstance().getDateFormat().parse(U.getInstance().getStartDate());
            end = U.getInstance().getDateFormat().parse(U.getInstance().getEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, 0);
        int n = 1;
        while (true){
            adapter.add(n + "일차(" + U.getInstance().getDateFormat().format(calendar.getTime()).replace("-",".") + ")");
            n++;
            calendar.add(Calendar.DATE, 1);
            if(calendar.getTime().after(end)) break;
        }
        adapter.notifyDataSetChanged();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener((parent, view1, position, id) -> {

        });
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

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(places -> {
                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                        final Place myPlace = places.get(0);
                        placeNameTv.setText(myPlace.getName());
                        PlaceAddrTv.setText(myPlace.getAddress());
                        toolbarTitleTv.setText(myPlace.getName());
                        tripTitle.setText(myPlace.getName());
                        placePhotosAsync();
                    } else {
                        U.getInstance().log("에러");
                    }
                    places.release();
                });
    }

    // 구글에서 사진을 비동기로 가져옴
    private void placePhotosAsync() {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
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