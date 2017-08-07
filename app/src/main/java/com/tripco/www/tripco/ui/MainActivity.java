package com.tripco.www.tripco.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.rey.material.widget.FloatingActionButton;
import com.tripco.www.tripco.BuildConfig;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.adapter.MainAdapter;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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

        if(!checkPlayService(this)) return;
        makeShortCut(this);
        getServerAddress();
        uiInit();
        recViewInit();
    }

    // 바로가기 셋팅
    public void makeShortCut(Context context){
        // 바로가기가 이전에 만들어져있으면 바로끝
        if(U.getInstance().getBoolean("makeShortcut")) return;
        // 바로가기가 아직 없다면
        U.getInstance().setBoolean("makeShortcut", true);
        // 바로 가기 처리
        // 1차 재료
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClassName(context, getClass().getName());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        // 2차 래핑
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher_round));
        intent.putExtra("duplicate", false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        // 방송
        sendBroadcast(intent);
    }

    // Google play Service 버전 체크
    public boolean checkPlayService(Activity context){
        Integer resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if(resultCode == ConnectionResult.SUCCESS) return true;

        // 마켓으로 이동할수있게 유도함
        Dialog dialog =
                GoogleApiAvailability.getInstance().getErrorDialog(context, resultCode, 0, dialogInterface -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id="+"com.google.android.gms"));
                    context.startActivity(intent);
                    context.finish();
                });

        if(dialog != null) {
            dialog.setOnCancelListener(dialogInterface -> context.finish());
            dialog.show();
        }

        // 문제있음
        return false;
    }

    // 서버 주소 셋팅
    public void getServerAddress(){
        final FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        config.setConfigSettings(configSettings);

        long cacheExpiration = 3600;

        if (config.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        config.fetch(cacheExpiration).addOnCompleteListener(task -> {
            if( task.isSuccessful() ){
                config.activateFetched();
                U.getInstance().setString("MAIN_SERVER_DOMAIN", config.getString("MAIN_SERVER_DOMAIN"));
            }
        });
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        fab.setOnClickListener(view -> {
            // 여행만들기
            startActivity(new Intent(getBaseContext(), MakeTripActivity.class));
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
