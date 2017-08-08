package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.holder.AlarmViewHolder;
import com.tripco.www.tripco.model.AlarmModel;

import java.util.ArrayList;

/**
 * Created by Tacademy on 2017-08-08.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    Context context;
    ArrayList<AlarmModel> alarmModels;

    public AlarmAdapter(Context context, ArrayList<AlarmModel> alarmModels) {
        this.context = context;
        this.alarmModels = alarmModels;
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_alarm_layout, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        // 데이터 셋팅
    }

    @Override
    public int getItemCount() {
        return alarmModels==null ? 10 : alarmModels.size();
    }
}
