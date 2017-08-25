package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.Spinner;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.util.U;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetScheduleActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.previous_btn) Button previousBtn;
    @BindView(R.id.next_btn) Button nextBtn;
    @BindView(R.id.line1) LinearLayout line1;
    @BindView(R.id.line2) LinearLayout line2;
    @BindView(R.id.line3) LinearLayout line3;
    @BindView(R.id.line4) LinearLayout line4;
    @BindView(R.id.rbs_rg) RadioGroup rbsGroup;
    @BindView(R.id.google_places_btn) CardView googlePlacesBtn;
    @BindView(R.id.address_name_tv) TextView addrNameTv;
    @BindView(R.id.address_tv) TextView addressTv;
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.title_et) EditText titleEt;
    @BindView(R.id.memo_et) EditText memoEt;
    @BindView(R.id.map_line) LinearLayout mapLine;
    @BindString(R.string.first_title)  String FIRST_TITLE;
    @BindString(R.string.second_title) String SECOND_TITLE;
    @BindString(R.string.third_title)  String THIRD_TITLE;
    @BindString(R.string.fourth_title) String FOURTH_TITLE;
    private GoogleMap mMap = null;
    private boolean mapFlag = false;
    private String placeId = null;
    private LatLng latlng = null;
    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_schedule);
        ButterKnife.bind(this);
        toolbarInit();
        spinnerInit();
        mapInit();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText(FIRST_TITLE);
        toolbarRightBtn.setText("저장");
    }

    private void spinnerInit() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, 0);
        for (int i = 0; i < U.getInstance().tripDataModel.getDateList().size(); i++) {
            adapter.add(U.getInstance().tripDataModel.getDateSpinnerList().get(i));
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void mapInit(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @OnClick({R.id.toolbar_right_btn, R.id.google_places_btn})
    public void onClickBtns(View view){
        switch (view.getId()){
            case R.id.toolbar_right_btn:
                break;
            case R.id.google_places_btn: // 구글플레이스 검색창 열기
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                    startActivityForResult(intent, U.PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
        }
    }

    @OnClick({R.id.previous_btn, R.id.next_btn}) // 상세페이지 이전, 다음 버튼
    public void onClickPageMoveBtn(View view) {
        switch (view.getId()) {
            case R.id.previous_btn:
                nextBtn.setVisibility(View.VISIBLE);
                index--;
                switch (index){
                    case 1:
                        toolbarTitleTv.setText(FIRST_TITLE);
                        line1.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.GONE);
                        mapLine.setVisibility(View.GONE);
                        previousBtn.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        toolbarTitleTv.setText(SECOND_TITLE);
                        line2.setVisibility(View.VISIBLE);
                        line3.setVisibility(View.GONE);
                        if(mapFlag) mapLine.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        toolbarTitleTv.setText(THIRD_TITLE);
                        line3.setVisibility(View.VISIBLE);
                        line4.setVisibility(View.GONE);
                        mapLine.setVisibility(View.GONE);
                        break;
                }
                break;
            case R.id.next_btn:
                previousBtn.setVisibility(View.VISIBLE);
                index++;
                switch (index){
                    case 2:
                        toolbarTitleTv.setText(SECOND_TITLE);
                        line1.setVisibility(View.GONE);
                        line2.setVisibility(View.VISIBLE);
                        if(mapFlag) mapLine.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        toolbarTitleTv.setText(THIRD_TITLE);
                        line2.setVisibility(View.GONE);
                        line3.setVisibility(View.VISIBLE);
                        mapLine.setVisibility(View.GONE);
                        break;
                    case 4:
                        toolbarTitleTv.setText(FOURTH_TITLE);
                        line3.setVisibility(View.GONE);
                        line4.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.INVISIBLE);
                        break;
                }
                break;
        }
    }

    private int getIntCateNo(String cateStr){
        if(cateStr.equals(U.category0)) return 0;
        if(cateStr.equals(U.category1)) return 1;
        if(cateStr.equals(U.category2)) return 2;
        return -1;
    }

    @Override // 구글 플레이스에서 검색한 데이터 받아서 처리
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == U.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mapFlag = true;
                Place place = PlaceAutocomplete.getPlace(this, data);
                addrNameTv.setText(place.getName());
                addressTv.setVisibility(View.VISIBLE);
                addressTv.setText(place.getAddress());
                placeId = place.getId();
                latlng = place.getLatLng();
                titleEt.setText(place.getName());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latlng).title(place.getName().toString()))
                        .showInfoWindow();
                CameraPosition ani = new CameraPosition.Builder()
                        .target(latlng)
                        .zoom(16)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(ani));
                mapLine.setVisibility(View.VISIBLE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                U.getInstance().log("RESULT_ERROR 상태 : " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                U.getInstance().log("RESULT_CANCELED 상태");
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
