package com.tripco.www.tripco.fragment;

import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.util.U;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CandidateLIstFragment extends Fragment {
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    Unbinder unbinder;

    public CandidateLIstFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidate_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        SectionsPagerAdapter spAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(spAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class PlaceholderFragment extends Fragment implements OnMapReadyCallback {
        private GoogleMap mMap;
        private static final String ARG_SECTION_NUMBER = "section_number";
        PlaceholderFragment con;

        public PlaceholderFragment() {
            con = this;
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_classification, container, false);
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            try {
                mapFragment.getMapAsync(con);
            } catch (Exception e) {
                e.printStackTrace();
                U.getInstance().log(" 지도 에러 >>>> " + e.toString());
            }
            return rootView;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
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
}
