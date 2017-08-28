package com.tripco.www.tripco.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.Spinner;
import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootFragment;
import com.tripco.www.tripco.adapter.FinScheduleListAdapter;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.ScheduleModel;
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
    @BindView(R.id.change_view_btn) ImageButton changeViewBtn;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private GoogleMap mMap = null;
    private Unbinder unbinder;
    private View view;
    private int tripNo;
    int position = 0;

    public FinalScheduleFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_final_schedule, container, false);
        unbinder = ButterKnife.bind(this, view);
        U.getInstance().getBus().register(this);
        tripNo = U.getInstance().tripDataModel.getTripNo();
        spinnerInit();
        recViewInit();
        swipeRefreshInit();
        return view;
    }

    @Subscribe
    public void ottoBus(String str){
        if(str.equals("ChangeSelectDate")) recViewInit();
        if(str.equals("DELETE_CHECK")) recViewInit();
    }

    @Override
    public void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void recViewInit(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<ScheduleModel> scheduleModels = setScheduleModel();
        FinScheduleListAdapter adapter;
        if(U.getInstance().getBoolean("login")){
            adapter = new FinScheduleListAdapter(getContext(), null, position);
        } else {
            adapter = new FinScheduleListAdapter(getContext(), scheduleModels, position);
        }
        recyclerView.setAdapter(adapter);
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
            this.position = position;
            recViewInit();
        });
    }

    private ArrayList<ScheduleModel> setScheduleModel(){
        ArrayList<ScheduleModel> list = new ArrayList<>();
        String sql = "select * from ScheduleList_Table where trip_no=" +tripNo +
                " and schedule_date= '" + position + "' and item_check = 1 order by item_time;";
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
        if(mapView.getVisibility() == View.GONE) {
            changeViewBtn.setImageResource(R.drawable.list_icon);
            mapView.setVisibility(View.VISIBLE);
        } else {
            changeViewBtn.setImageResource(R.drawable.map_icon);
            mapView.setVisibility(View.GONE);
        }
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

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    //==========================================================================================
}
