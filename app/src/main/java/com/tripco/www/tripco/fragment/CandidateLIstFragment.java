package com.tripco.www.tripco.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.Spinner;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.ui.TripActivity;
import com.tripco.www.tripco.util.U;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CandidateLIstFragment extends Fragment
        implements OnMapReadyCallback, TripActivity.onKeyBackPressedListener  {
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.change_view_btn) Button changeViewBtn;
    @BindString(R.string.add_candidate) String addCandidate;
    private GoogleMap mMap = null;
    private Unbinder unbinder;
    private View view;
    private int tripNo;
    private String startDate, endDate;

    public CandidateLIstFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tripNo = U.getInstance().getTripNo();
        startDate = U.getInstance().getStartDate();
        endDate = U.getInstance().getEndDate();
        U.getInstance().setSelectDate(startDate);
        if(getArguments() != null && getArguments().getInt("i", 0) == 1) onGooglePlaces();
        view = inflater.inflate(R.layout.fragment_candidate_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        uiInit();
        spinnerInit();
        return view;
    }

    private void uiInit(){
        SectionsPagerAdapter spAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(spAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void spinnerInit(){
        Date start = null;
        Date end = null;
        try {
            start = U.getInstance().getDateFormat().parse(startDate);
            end = U.getInstance().getDateFormat().parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, 0);
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

        U.getInstance().setSpinnerDate(spinner.getSelectedItem().toString());
        spinner.setOnItemSelectedListener((parent, view1, position, id) -> {
            String selectDate = U.getInstance().getDate(parent.getSelectedItem().toString()).replace(".","-");
            U.getInstance().setSelectDate(selectDate);
            U.getInstance().getBus().post(selectDate);
            U.getInstance().setSpinnerDate(parent.getSelectedItem().toString());
        });
    }

    @OnClick({R.id.change_view_btn, R.id.search_btn})
    public void onClickBtn(View view){
        switch (view.getId()) {
            case R.id.change_view_btn: // 리스트 <-> 지도 전환 버튼
                if (mapView.getVisibility() == View.GONE) {
                    changeViewBtn.setBackgroundResource(android.R.drawable.ic_dialog_dialer);
                    tabLayout.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.GONE);
                    mapView.setVisibility(View.VISIBLE);
                } else {
                    changeViewBtn.setBackgroundResource(android.R.drawable.ic_dialog_map);
                    tabLayout.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.GONE);
                }
                break;
            case R.id.search_btn: // googleplace 호출
                onGooglePlaces();
                break;
        }
    }

    private void onGooglePlaces(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
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
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                Double lat = place.getLatLng().latitude;
                Double lng = place.getLatLng().longitude;
                String placeId = place.getId();
                String placeName = place.getName().toString();
                insertSQLite(lat, lng, placeId, placeName);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                U.getInstance().log("RESULT_ERROR 상태 : " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                U.getInstance().log("RESULT_CANCELED 상태");
            }
        }
    }

    private void insertSQLite(Double lat, Double lng, String placeId, String placeName) {
        try {
            String sql = "insert into ScheduleList_Table(" +
                    " trip_no, " +
                    " schedule_date, " +
                    " item_url, " +
                    " cate_no, " +
                    " item_lat, " +
                    " item_long, " +
                    " item_placeid, " +
                    " item_title, " +
                    " item_memo) " +
                    " values(" +
                    "'" + tripNo + "', " +
                    "'" + U.getInstance().getDate(spinner.getSelectedItem().toString()).replace(".","-") + "', " +
                    "'', " + // item_url == ""
                    "0, " +  // cate_no == 0
                    "'" + lat + "', " +
                    "'" + lng + "', " +
                    "'" + placeId + "', " +
                    "'" + placeName + "', " +
                    "'');"; // memo == ""
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            Toast.makeText(getContext(), addCandidate, Toast.LENGTH_SHORT).show();
            U.getInstance().getBus().post("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return setFragment(new ViewPagerFragment(), 0);
                case 1:
                    return setFragment(new ViewPagerFragment(), 1);
                case 2:
                    return setFragment(new ViewPagerFragment(), 2);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return U.getInstance().category0;
                case 1:
                    return U.getInstance().category1;
                case 2:
                    return U.getInstance().category2;
            }
            return null;
        }
    }

    private Fragment setFragment(Fragment fragment, int cateNo){
        Bundle bundle = new Bundle(1);
        bundle.putInt("cateNo", cateNo);
        fragment.setArguments(bundle);
        return fragment;
    }

    // 백키눌렀을때 메인으로
    @Override
    public void onBack() {
        TripActivity activity = (TripActivity) getActivity();
        activity.setOnKeyBackPressedListener(null);
        activity.onBackPressed();

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((TripActivity) activity).setOnKeyBackPressedListener(this);
    }

    // 구글맵사용에 필요한 오버라이드 메소드들
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null){
                parent.removeView(view);
            }
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mapView != null) mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("시드니"));
        CameraPosition ani = new CameraPosition.Builder()
                .target(sydney)
                .zoom(10)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(ani));
    }
}
