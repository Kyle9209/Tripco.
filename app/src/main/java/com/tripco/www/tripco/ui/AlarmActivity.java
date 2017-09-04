package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootActivity;
import com.tripco.www.tripco.adapter.AlarmAdapter;
import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.util.U;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmActivity extends RootActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        U.getInstance().getBus().register(this);
        toolbarInit();
        showPD();
        NetProcess.getInstance().netListNotice(new MemberModel(U.getInstance().getUserModel().getUser_id()));
    }

    @Subscribe
    public void ottoBus(String str) {
        if(str.equals("AlarmList")){
            recViewInit();
        }
    }

    @Override
    protected void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("알림");
        toolbarTitleTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_notifications_none_white_24dp,0,0,0);
        toolbarTitleTv.setCompoundDrawablePadding(20);
        toolbarRightBtn.setVisibility(View.INVISIBLE);
    }

    private void recViewInit(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AlarmAdapter adapter;
        if(U.getInstance().getBoolean("login"))
            adapter = new AlarmAdapter(this, U.getInstance().getAlarmListModel());
        else
            adapter = new AlarmAdapter(this, U.getInstance().getAlarmListModel());
        recyclerView.setAdapter(adapter);
        stopPD();
    }
}


