package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.holder.MarkerListViewHolder;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.ui.ScheduleInfoActivity;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

public class MarkerListAdapter extends RecyclerView.Adapter<MarkerListViewHolder>
        implements GoogleApiClient.OnConnectionFailedListener {
    private ArrayList<ScheduleModel> scheduleModels;
    private Context context = U.getInstance().getContext();

    public MarkerListAdapter(ArrayList<ScheduleModel> scheduleModel){
        this.scheduleModels = scheduleModel;
    }

    @Override
    public MarkerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_marker_layout, parent, false);
        return new MarkerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MarkerListViewHolder holder, int position) {
        final ScheduleModel scheduleModel = scheduleModels.get(position);
        holder.indexTv.setText(String.valueOf(position+1));
        holder.indexTv.setOnClickListener(view -> U.getInstance().getBus().post("moveToMarker_"+position));
        // 체크박스 상태 찍어주고 최종 < - > 후보지
        if(scheduleModel.getItem_check() == 1) {
            holder.checkCb.setImageResource(R.drawable.check_after_icon);
        } else {
            holder.checkCb.setImageResource(R.drawable.check_before_icon);
        }
        holder.checkCb.setOnClickListener(view -> {
            if(U.getInstance().getBoolean("login")){
                if(scheduleModels.get(position).getItem_check() == 1){
                    scheduleModels.get(position).setItem_check(0);
                    holder.checkCb.setImageResource(R.drawable.check_before_icon);
                } else {
                    scheduleModels.get(position).setItem_check(1);
                    holder.checkCb.setImageResource(R.drawable.check_after_icon);
                }
                NetProcess.getInstance().netCheckItem(new ScheduleModel(
                        U.getInstance().getUserModel().getUser_id(),
                        U.getInstance().tripDataModel.getTripNo(),
                        scheduleModel.getSchedule_date(),
                        scheduleModel.get_id()));
            } else {
                if(scheduleModels.get(position).getItem_check() == 1){
                    scheduleModels.get(position).setItem_check(0);
                    holder.checkCb.setImageResource(R.drawable.check_before_icon);
                    updateSQLite(scheduleModel.getTrip_no(), scheduleModel.getSchedule_no(),
                            0, "최종일정에서 삭제되었습니다.");
                } else {
                    scheduleModels.get(position).setItem_check(1);
                    holder.checkCb.setImageResource(R.drawable.check_after_icon);
                    updateSQLite(scheduleModel.getTrip_no(), scheduleModel.getSchedule_no(),
                            1, "최종일정에 추가되었습니다.");
                }
            }
        });
        // 유형체크해서 이미지 변경
        // placeId 확인  > 널이면 title 확인 > ""이면 제목없음
        if(scheduleModel.getItem_placeid().equals("null")){
            if(scheduleModel.getItem_title().equals("")) holder.titleTv.setText("제목없음");
            else holder.titleTv.setText(scheduleModel.getItem_title());
        } else {
            // placeid로 위치명, 사진 가져오기
            String placeId = scheduleModel.getItem_placeid();
            Places.GeoDataApi.getPlaceById(U.getInstance().getmGoogleApiClient(), placeId)
                    .setResultCallback(places -> {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            // 위치명
                            holder.titleTv.setText(myPlace.getName());
                            holder.addressTv.setText(myPlace.getAddress());
                        } else {
                            U.getInstance().log("에러");
                        }
                        places.release();
                    });
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ScheduleInfoActivity.class);
            intent.putExtra("scheduleModel", scheduleModel);
            context.startActivity(intent);
        });
    }

    private void updateSQLite(int trip_no, int s_no, int check, String str) {
        try {
            String sql = "update ScheduleList_Table set item_check = '" + check + "'" +
                    " where trip_no = " + trip_no + " and schedule_no = " + s_no + ";";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            U.getInstance().getBus().post("ViewPagerListUpdate");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return scheduleModels ==null ? 0 : scheduleModels.size();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
