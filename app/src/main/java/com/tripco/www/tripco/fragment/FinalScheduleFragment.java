package com.tripco.www.tripco.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.Spinner;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.adapter.FinalScheduleAdapter;
import com.tripco.www.tripco.model.FinalScheduleModel;
import com.tripco.www.tripco.ui.TripActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FinalScheduleFragment extends Fragment
        implements OnMapReadyCallback,
        TripActivity.onKeyBackPressedListener  {
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.change_view_btn) Button changeViewBtn;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private GoogleMap mMap = null;
    private Unbinder unbinder;
    private View view;

    public FinalScheduleFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_final_schedule, container, false);
        unbinder = ButterKnife.bind(this, view);
        spinnerInit();
        recViewInit();
        swipeRefreshInit();
        return view;
    }

    private void recViewInit(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<FinalScheduleModel> finalScheduleModels = null;
        FinalScheduleAdapter adapter = new FinalScheduleAdapter(getContext(), finalScheduleModels);
        recyclerView.setAdapter(adapter);
    }

    private void spinnerInit(){
        ArrayList<String> items = new ArrayList<>(Arrays.asList("1일차(09.01)", "2일차(09.02)"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, items);
        adapter.add("3일차(09.03)");
        adapter.notifyDataSetChanged();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void swipeRefreshInit(){
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeContainer.setOnRefreshListener(() -> {
            recyclerView.getAdapter().notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
        });
    }

    @OnClick(R.id.change_view_btn) // 리스트 <-> 지도 전환 버튼
    public void onClickBtn(){
        if(mapView.getVisibility() == View.GONE) {
            changeViewBtn.setBackgroundResource(android.R.drawable.ic_dialog_dialer);
            mapView.setVisibility(View.VISIBLE);
        } else {
            changeViewBtn.setBackgroundResource(android.R.drawable.ic_dialog_map);
            mapView.setVisibility(View.GONE);
        }
    }

    // 백키눌렀을때 웹뷰 뒤로가기
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
