package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.adapter.AlarmAdapter;
import com.tripco.www.tripco.model.AlarmModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView
 * 1. LayoutManager : View의 형태를 정의하기 위해 (xml에 정의)
 * 2. ViewHolder :
 */

public class AlarmActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ArrayList<AlarmModel> alarmModels = null;
        AlarmAdapter adapter = new AlarmAdapter(this, alarmModels);
        recyclerView.setAdapter(adapter);
    }
}

