package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.rey.material.widget.FloatingActionButton;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.adapter.MainAdapter;
import com.tripco.www.tripco.model.TripModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.recylerview) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        uiInit();
        recViewInit();
    }

    @Override // 좌측 네비게이션
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override // 좌측 네비게이션
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            startActivity(new Intent(this, MyPageActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 기본 UI 셋팅
    private void uiInit() {
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 여행만들기
                startActivity(new Intent(getBaseContext(), MakeTripActivity.class));
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    // RecyclerView 셋팅
    private void recViewInit(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MainAdapter adapter = new MainAdapter(this, ArrayListTripModel());
        recyclerView.setAdapter(adapter);
    }

    // 리스트 데이터 셋팅
    public ArrayList<TripModel> ArrayListTripModel(){
        ArrayList<TripModel> list = new ArrayList<>();
        list.add(new TripModel("여행가자", "너랑", "2017-09-01", "2017-09-02", "#힐링여행"));
        list.add(new TripModel("놀러가자", "누구랑", "2017-09-03", "2017-09-04", "#파산여행"));
        return list;
    }

    // 로그인, 회원가입 버튼
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                break;
            case R.id.join_btn:
                startActivity(new Intent(getBaseContext(), JoinMemberActivity.class));
                break;
        }
    }
}
