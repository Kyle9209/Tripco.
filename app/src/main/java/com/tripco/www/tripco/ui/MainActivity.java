package com.tripco.www.tripco.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.rey.material.widget.FloatingActionButton;
import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.BuildConfig;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.adapter.TripListAdapter;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.net.NetProcess;
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
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private Button loginBtn, joinBtn;
    private LinearLayout userInfo;
    private TextView userEmailTv, userNickTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        U.getInstance().getBus().register(this);
        DBOpenHelper.getInstance();

        if(!checkPlayService(this)) return;
        makeShortCut(this);
        getServerAddress();
        uiInit();
        swipeRefreshInit();
        loginCheck();
    }

    private void loginCheck(){
        if(U.getInstance().getBoolean("login")){
            swipeContainer.setRefreshing(true);
            NetProcess.getInstance().netLoginJoinSimple(new MemberModel(U.getInstance().getString("email")), "simple");
            userInfo.setVisibility(View.VISIBLE);
            loginBtn.setText("로그아웃");
            joinBtn.setText("프로필");
        } else {
            userInfo.setVisibility(View.GONE);
            loginBtn.setText("로그인");
            joinBtn.setText("회원가입");
            recViewInit();
        }
    }

    @Subscribe
    public void ottoBus(String BUS_NAME) {
        if(BUS_NAME.equals("loginSuccess")) loginCheck();
        if(BUS_NAME.equals("getUserInfo")) {
            userEmailTv.setText(U.getInstance().getMemberModel().getUser_id());
            userNickTv.setText(U.getInstance().getMemberModel().getUser_nick());
            NetProcess.getInstance().netListTrip(new MemberModel(U.getInstance().getMemberModel().getUser_id()));
        }
        if(BUS_NAME.equals("nickChange")){
            userNickTv.setText(U.getInstance().getMemberModel().getUser_nick());
        }
        if(BUS_NAME.equals("tripListInit")) recViewInit();
    }

    @Override // 디비 클로즈
    protected void onDestroy() {
        U.getInstance().getBus().unregister(this);
        DBOpenHelper.dbOpenHelper.close();
        super.onDestroy();
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
        if(U.getInstance().getBoolean("MAIN_SERVER_DOMAIN_CHECK")) return;
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
                U.getInstance().setBoolean("MAIN_SERVER_DOMAIN_CHECK", true);
                U.getInstance().log("주소값 가져오기 최초 1번실행");
            }
        });
    }

    @Override // 백키 눌렀을 때 좌측 네비게이션 접기
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

        if (id == R.id.nav_alarm) {
            startActivity(new Intent(this, AlarmActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 기본 UI 셋팅
    private void uiInit() {
        setSupportActionBar(toolbar);
        fab.setOnClickListener(view -> startActivity(new Intent(getBaseContext(), SetTripActivity.class)));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        loginBtn = navigationView.getHeaderView(0).findViewById(R.id.login_btn);
        joinBtn = navigationView.getHeaderView(0).findViewById(R.id.join_btn);
        userInfo = navigationView.getHeaderView(0).findViewById(R.id.user_info_line);
        userEmailTv = navigationView.getHeaderView(0).findViewById(R.id.user_email_tv);
        userNickTv = navigationView.getHeaderView(0).findViewById(R.id.user_nick_tv);
    }

    // RecyclerView 셋팅
    private void recViewInit(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        TripListAdapter adapter;
        if(U.getInstance().getBoolean("login")) {
            adapter = new TripListAdapter(this, U.getInstance().getList());
        } else {
            adapter = new TripListAdapter(this, ArrayListTripModel());
        }
        swipeContainer.setRefreshing(false);
        recyclerView.setAdapter(adapter);
    }

    // RecyclerView 리프레시 셋팅
    public void swipeRefreshInit(){
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            if(U.getInstance().getBoolean("login")) {
                NetProcess.getInstance().netListTrip(new MemberModel(U.getInstance().getMemberModel().getUser_id()));
            } else {
                recViewInit();
            }
        });
    }

    // 로컬디비에있는 리스트 데이터 셋팅
    public ArrayList<TripModel> ArrayListTripModel(){
        ArrayList<TripModel> list = new ArrayList<>();
            Cursor csr = DBOpenHelper.dbOpenHelper.getWritableDatabase()
                    .rawQuery("select * from Trip_Table order by trip_no desc;", null);
            while (csr.moveToNext()) {
                list.add(new TripModel(
                        csr.getInt(0),
                        csr.getString(1),
                        csr.getString(2),
                        csr.getString(3),
                        csr.getString(4),
                        csr.getString(5),
                        csr.getString(6)
                ));
        }
        return list;
    }

    // 로그인, 회원가입 버튼
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                if(U.getInstance().getBoolean("login")){
                    U.getInstance().showAlertDialog(this, "알림", "로그아웃 하시겠습니까?",
                            "예", (dialogInterface, i) -> {
                                U.getInstance().setBoolean("login", false);
                                recViewInit();
                                loginCheck();
                                dialogInterface.dismiss();
                            },
                            "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.join_btn:
                if(U.getInstance().getBoolean("login")){
                    startActivity(new Intent(this, MyPageActivity.class));
                } else {
                    startActivity(new Intent(this, JoinActivity.class));
                }
                break;
        }
    }
}
