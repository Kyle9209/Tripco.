package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
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

    public AlarmAdapter(Context context, ArrayList<AlarmModel> alarmModels)  {
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
        final  AlarmModel alarmModel = alarmModels.get(position);
        // 표현 정보 셋팅
        holder.alarm_content.setText(alarmModel.getNotice_partner() + " 님이 " + alarmModel.getNotice_item()+" 을 업로드하였습니다.");
        // 상대방 프로필 사진 셋팅
        Picasso.with(holder.profile_photo.getContext())
                .load(alarmModel.getNotice_image())
                .error(R.mipmap.ic_launcher_round)
                .into(holder.profile_photo);

        holder.alarm_time.setText(alarmModel.getNotice_time());
    }

    @Override
    public int getItemCount() {
        return alarmModels==null ? 0 : alarmModels.size();
    }

}
