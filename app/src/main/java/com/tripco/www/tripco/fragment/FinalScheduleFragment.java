package com.tripco.www.tripco.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rey.material.widget.Spinner;
import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootFragment;
import com.tripco.www.tripco.adapter.FinScheduleListAdapter;
import com.tripco.www.tripco.adapter.MarkerListAdapter;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.ui.TripActivity;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FinalScheduleFragment extends RootFragment
        implements OnMapReadyCallback, TripActivity.onKeyBackPressedListener  {
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.map_rela) RelativeLayout mapRela;
    @BindView(R.id.map_rcv) RecyclerView mapRcv;
    @BindView(R.id.change_view_btn) ImageButton changeViewBtn;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private GoogleMap mMap = null;
    private Unbinder unbinder;
    private View view;
    private int tripNo;
    ArrayList<ScheduleModel> list = new ArrayList<>();
    ArrayList<LatLng> latLng = new ArrayList<>();

    public FinalScheduleFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_final_schedule, container, false);
        unbinder = ButterKnife.bind(this, view);
        U.getInstance().getBus().register(this);
        tripNo = U.getInstance().tripDataModel.getTripNo();
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mapRcv);
        spinnerInit();
        recViewInit();
        swipeRefreshInit();
        getServerData(); // 로그인 되어있다면 서버에서 데이터 가져옴
        return view;
    }

    private void getServerData(){ // 데이터 가져와서 ottoBus 에 position 날림
        if(U.getInstance().getBoolean("login")) {
            swipeContainer.setRefreshing(true);
            NetProcess.getInstance().netListItem(new ScheduleModel(tripNo, spinner.getSelectedItemPosition()));
        }
    }

    @Subscribe
    public void ottoBus(String str){
        if(str.contains("position")){ // 서버에서 데이터 가져오고 나서
            recViewInit();
        }
        if(str.equals("UpdateItemSuccess") || str.equals("DeleteItemSuccess")) { // 삭제, 수정
            if(U.getInstance().getBoolean("login")) getServerData();
            else recViewInit();
        }
        if(str.equals("checkItemSuccess")){
            NetProcess.getInstance().netListItem(new ScheduleModel(tripNo, spinner.getSelectedItemPosition()));
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

    @Override
    public void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void recViewInit(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        FinScheduleListAdapter adapter;
        if(U.getInstance().getBoolean("login")){
            list.clear();
            if(U.getInstance().getScheduleListModel() != null) {
                for (int i = 0; i < U.getInstance().getScheduleListModel().size(); i++) {
                    if (U.getInstance().getScheduleListModel().get(i).getItem_check() == 1) {
                        list.add(U.getInstance().getScheduleListModel().get(i));
                    }
                }
            }
            adapter = new FinScheduleListAdapter(getContext(), list, spinner.getSelectedItemPosition());
        } else {
            adapter = new FinScheduleListAdapter(getContext(), setScheduleModel(), spinner.getSelectedItemPosition());
        }
        recyclerView.setAdapter(adapter);
        swipeContainer.setRefreshing(false);
    }

    // 맵에 있는 리스트뷰
    private void recViewInit(ArrayList<ScheduleModel> list){
        mapRcv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        MarkerListAdapter adapter = new MarkerListAdapter(list);
        mapRcv.setAdapter(adapter);
    }

    private void spinnerInit(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, 0);
        for (int i = 0; i < U.getInstance().tripDataModel.getDateSpinnerList().size(); i++) {
            adapter.add(U.getInstance().tripDataModel.getDateSpinnerList().get(i));
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // 스피너에서 날짜를 선택하면
        spinner.setOnItemSelectedListener((parent, view1, position, id) -> {
            if(U.getInstance().getBoolean("login")){
                swipeContainer.setRefreshing(true);
                NetProcess.getInstance().netListItem(new ScheduleModel(tripNo, position));
                new Handler().postDelayed(() -> {
                    onMapMarker();
                    stopPD();
                }, 1000);
            } else {
                recViewInit();
                onMapMarker();
            }
        });
    }

    private ArrayList<ScheduleModel> setScheduleModel(){
        list.clear();
        String sql = "select * from ScheduleList_Table where trip_no=" +tripNo +
                " and schedule_date= '" + spinner.getSelectedItemPosition() +
                "' and item_check = 1 order by item_time;";
        Cursor csr = DBOpenHelper.dbOpenHelper.getWritableDatabase().rawQuery(sql, null);
        while (csr.moveToNext()) {
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
        }
        return list;
    }

    public void swipeRefreshInit(){
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            new Handler().postDelayed(() -> {
                recViewInit();
                swipeContainer.setRefreshing(false);
            }, 3000);
        });
    }

    @OnClick(R.id.change_view_btn) // 리스트 <-> 지도 전환 버튼
    public void onClickBtn(){
        if (mapRela.getVisibility() == View.GONE) {
            changeViewBtn.setImageResource(R.mipmap.ic_format_list_bulleted_white_24dp);
            onMapMarker();
            mapRela.setVisibility(View.VISIBLE);
        } else {
            if(U.getInstance().getBoolean("login")) {
                NetProcess.getInstance().netListItem(
                        new ScheduleModel(tripNo, spinner.getSelectedItemPosition()));
            }
            recViewInit();
            changeViewBtn.setImageResource(R.mipmap.ic_map_white_24dp);
            mapRela.setVisibility(View.GONE);
        }
    }

    private void onMapMarker() {
        latLng.clear();
        mMap.clear();
        int position = 0;
        ArrayList<ScheduleModel> list = new ArrayList<>();
        PolylineOptions rectOptions = new PolylineOptions();

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
                    rectOptions.add(latLng.get(position));
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
                    " and schedule_date='" + spinner.getSelectedItemPosition() + "'" +
                    " and item_check = 1 order by item_time;";
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
                    rectOptions.add(latLng.get(position));
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

        mMap.addPolyline(rectOptions);

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

    public View getMarkerView(int position){
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.custom_marker_layout, null);
        TextView markerIdxTv = view.findViewById(R.id.marker_index_tv);
        markerIdxTv.setText(String.valueOf(position+1));
        return view;
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

    // 구글맵 사용에 필요한 오버라이드 메소드들 ====================================================
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
        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            mapRcv.smoothScrollToPosition((int) marker.getZIndex());
            return true;
        });
    }
    //==========================================================================================
}
