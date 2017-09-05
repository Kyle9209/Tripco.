package com.tripco.www.tripco.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.Spinner;
import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootFragment;
import com.tripco.www.tripco.adapter.MarkerListAdapter;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.ui.TripActivity;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CandidateLIstFragment extends RootFragment
        implements OnMapReadyCallback, TripActivity.onKeyBackPressedListener{
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.map_rela) RelativeLayout mapRela;
    @BindView(R.id.change_view_btn) ImageButton changeViewBtn;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindString(R.string.add_candidate) String addCandidateText;
    private GoogleMap mMap = null;
    private Unbinder unbinder;
    private View view;
    private int tripNo;
    ArrayList<LatLng> latLng = new ArrayList<>();

    public CandidateLIstFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_candidate_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        U.getInstance().getBus().register(this);
        tripNo = U.getInstance().tripDataModel.getTripNo();
        uiInit();
        spinnerInit();
        if(getArguments() != null && getArguments().getInt("i", 0) == 1) onGooglePlaces(); // 탐색->후보지->검색
        return view;
    }

    @Subscribe
    public void ottoBus(String str){
        stopPD();
        if(str.equals("CreateItemSuccess")){
            Toast.makeText(getContext(), addCandidateText, Toast.LENGTH_SHORT).show();
            NetProcess.getInstance().netListItem(new ScheduleModel(tripNo, 0));
            mViewPager.setCurrentItem(0);
            spinner.setSelection(0);
        }
        if(str.equals("UpdateItemSuccess") || str.equals("DeleteItemSuccess")){
            NetProcess.getInstance().netListItem(
                    new ScheduleModel(tripNo, spinner.getSelectedItemPosition()));
        }
        if(str.contains("moveToMarker")) {
            int position = Integer.parseInt(str.split("_")[1]);
            CameraPosition ani = new CameraPosition.Builder()
                    .target(latLng.get(position))
                    .zoom(7)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(ani));
        }
    }

    private void uiInit(){
        SectionsPagerAdapter spAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(spAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
    }

    private void spinnerInit(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, 0);
        for (int i = 0; i < U.getInstance().tripDataModel.getDateSpinnerList().size(); i++) {
            adapter.add(U.getInstance().tripDataModel.getDateSpinnerList().get(i));
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // 로그인 되어있으면 초기화 - 1일차 데이터 가져옴
        if(U.getInstance().getBoolean("login")) {
            NetProcess.getInstance().netListItem(new ScheduleModel(tripNo, 0));
        }
        // 스피너에서 날짜를 선택하면
        spinner.setOnItemSelectedListener((parent, view1, position, id) -> {
            // 뷰페이저에 날짜를 바꿧다고 알려줌
            if (U.getInstance().getBoolean("login")) {
                U.getInstance().getBus().post("refreshTrue");
                NetProcess.getInstance().netListItem(new ScheduleModel(tripNo, position));
                showPD();
                new Handler().postDelayed(() -> {
                    onMapMarker();
                    stopPD();
                }, 1000);
            } else {
                U.getInstance().getBus().post("position" + position);
                onMapMarker();
            }
        });
    }

    private void onMapMarker() {
        latLng.clear();
        mMap.clear();
        int position = 0;
        ArrayList<ScheduleModel> list = new ArrayList<>();

        if (U.getInstance().getBoolean("login")) {
            ArrayList<ScheduleModel> scheduleModels = U.getInstance().getScheduleListModel();
            for(int i=0; i<scheduleModels.size(); i++){
                if(scheduleModels.get(i).getItem_placeid() != null
                        && !scheduleModels.get(i).getItem_placeid().equals("null")){
                    list.add(scheduleModels.get(i));
                    latLng.add(new LatLng(
                            Double.parseDouble(scheduleModels.get(i).getItem_lat()),
                            Double.parseDouble(scheduleModels.get(i).getItem_long())
                    ));
                    switch (scheduleModels.get(i).getCate_no()) {
                        case 0:
                            addMarkerOnMap(
                                    latLng.get(position),
                                    scheduleModels.get(i).getItem_title(),
                                    position);
                            break;
                        case 1:
                            addMarkerOnMap(
                                    latLng.get(position),
                                    scheduleModels.get(i).getItem_title(),
                                    position);
                            break;
                        case 2:
                            addMarkerOnMap(
                                    latLng.get(position),
                                    scheduleModels.get(i).getItem_title(),
                                    position);
                            break;
                    }
                    position++;
                }
            }
        } else {
            String sql = "select * from ScheduleList_Table where trip_no=" + tripNo +
                    " and schedule_date='" + spinner.getSelectedItemPosition() + "';";
            Cursor csr = DBOpenHelper.dbOpenHelper.getWritableDatabase().rawQuery(sql, null);
            while (csr.moveToNext()) {
                if (!csr.getString(7).equals("null") && csr.getString(7) != null) {
                    list.add(new ScheduleModel(
                            csr.getInt(0),
                            csr.getInt(1),
                            csr.getInt(2),
                            csr.getString(3),
                            csr.getInt(4),
                            csr.getString(5),
                            csr.getString(6),
                            csr.getString(7),
                            csr.getString(8),
                            csr.getString(9),
                            csr.getInt(10),
                            csr.getString(11)
                    ));
                    latLng.add(new LatLng(
                            Double.parseDouble(csr.getString(5)),
                            Double.parseDouble(csr.getString(6))
                    ));
                    switch (csr.getInt(4)) {
                        case 0:
                            addMarkerOnMap(
                                    latLng.get(position),
                                    csr.getString(8),
                                    position);
                            break;
                        case 1:
                            addMarkerOnMap(
                                    latLng.get(position),
                                    csr.getString(8),
                                    position);
                            break;
                        case 2:
                            addMarkerOnMap(
                                    latLng.get(position),
                                    csr.getString(8),
                                    position);
                            break;
                    }
                    position++;
                }
            }
        }

        // 리스트뷰 초기화
        recViewInit(list);

        // 첫번째 마커로 이동
        if (latLng.size() > 0 && latLng.get(0) != null) {
            CameraPosition ani = new CameraPosition.Builder()
                    .target(latLng.get(0))
                    .zoom(7)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(ani));
        }
    }

    private void addMarkerOnMap(LatLng latLng, String title, int position){
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), getMarkerView(position))))
                .zIndex(position));
    }

    public View getMarkerView(int position){
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.custom_marker_layout, null);
        TextView markerIdxTv = view.findViewById(R.id.marker_index_tv);
        markerIdxTv.setText(String.valueOf(position+1));
        return view;
    }

    // 맵에 있는 리스트뷰
    private void recViewInit(ArrayList<ScheduleModel> list){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        MarkerListAdapter adapter = new MarkerListAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    @OnClick({R.id.change_view_btn, R.id.search_btn})
    public void onClickBtn(View view){
        switch (view.getId()) {
            case R.id.change_view_btn: // 리스트 <-> 지도 전환 버튼
                if (mapRela.getVisibility() == View.GONE) {
                    changeViewBtn.setImageResource(R.mipmap.ic_format_list_bulleted_white_24dp);
                    onMapMarker();
                    mapRela.setVisibility(View.VISIBLE);
                } else {
                    if(U.getInstance().getBoolean("login")) {
                        NetProcess.getInstance().netListItem(
                                new ScheduleModel(tripNo, spinner.getSelectedItemPosition()));
                    }
                    changeViewBtn.setImageResource(R.mipmap.ic_map_white_24dp);
                    mapRela.setVisibility(View.GONE);
                }
                break;
            case R.id.search_btn: // googleplace 호출
                //onGooglePlaces();
                break;
        }
    }

    private void onGooglePlaces(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
            startActivityForResult(intent, U.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override // 구글 플레이스에서 검색한 데이터 받아서 처리
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == U.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
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
        if(U.getInstance().getBoolean("login")){
            showPD();
            NetProcess.getInstance().netCrUpDeItem(new ScheduleModel(
                    U.getInstance().getUserModel().getUser_id(),
                    tripNo,
                    0,
                    "null",
                    0,
                    String.valueOf(lat),
                    String.valueOf(lng),
                    placeId,
                    placeName,
                    "메모없음"
            ), "create");
        } else {
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
                        "0, " + // position == 1 (1일차)
                        "'null', " + // item_url = null
                        "0, " +  // cate_no == 0 (관광)
                        "'" + lat + "', " +
                        "'" + lng + "', " +
                        "'" + placeId + "', " +
                        "'" + placeName + "', " +
                        "'메모없음');"; // memo = 메모없음
                DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
                Toast.makeText(getContext(), addCandidateText, Toast.LENGTH_SHORT).show();
                mViewPager.setCurrentItem(0);
                spinner.setSelection(0);
                U.getInstance().getBus().post("AddCandidateSuccess");
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
                    return U.category0;
                case 1:
                    return U.category1;
                case 2:
                    return U.category2;
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

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
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
        U.getInstance().getBus().unregister(this);
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
        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            recyclerView.smoothScrollToPosition((int) marker.getZIndex());
            return true;
        });
    }
}
