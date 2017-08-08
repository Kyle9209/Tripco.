package com.tripco.www.tripco.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CandidateLIstFragment extends Fragment implements OnMapReadyCallback {
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.change_view_btn) Button changeViewBtn;
    private GoogleMap mMap = null;
    private Unbinder unbinder;
    private View view;

    public CandidateLIstFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_candidate_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        uiInit();
        spinnerInit();

        mapView.getMapAsync(this);

        return view;
    }

    private void uiInit(){
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        SectionsPagerAdapter spAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(spAdapter);
        tabLayout.setupWithViewPager(mViewPager);
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

    @OnClick(R.id.change_view_btn) // 리스트 <-> 지도 전환 버튼
    public void onClickBtn(){
        if(mapView.getVisibility() == View.GONE) {
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
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /*switch (position){
                case 0:
                    return new CdlRootFragment();
                case 1:
                    return new CdlRootFragment();
                case 2:
                    return new CdlRootFragment();
                case 3:
                    return new CdlRootFragment();
            }*/
            return new CdlRootFragment();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "관광";
                case 1:
                    return "음식";
                case 2:
                    return "숙박";
                case 3:
                    return "교통";
            }
            return null;
        }
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
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
