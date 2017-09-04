package com.tripco.www.tripco.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.tripco.www.tripco.PhotoTask;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.holder.ScheduleListViewHolder;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.ui.ScheduleInfoActivity;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

public class FinScheduleListAdapter extends RecyclerView.Adapter<ScheduleListViewHolder> {
    private Context context;
    private ArrayList<ScheduleModel> scheduleModels;
    private int pos;

    public FinScheduleListAdapter(Context context, ArrayList<ScheduleModel> scheduleModels, int pos) {
        this.context = context;
        this.scheduleModels = scheduleModels;
        this.pos = pos;
    }

    @Override
    public ScheduleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_final_list_layout, parent, false);
        return new ScheduleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleListViewHolder holder, int position) {
        // 데이터 셋팅
        final ScheduleModel scheduleModel = scheduleModels.get(position);
        // placeId 확인  > 널이면 title 확인 > ""이면 제목없음
        if(scheduleModel.getItem_placeid() == null || scheduleModel.getItem_placeid().equals("null")){
            holder.loadingImgPb.setVisibility(View.GONE);
            if (scheduleModel.getItem_title() == null || scheduleModel.getItem_title().equals("null"))
                holder.title.setText("제목없음");
            else
                holder.title.setText(scheduleModel.getItem_title());
        } else {
            // placeid로 위치명, 사진 가져오기
            String placeId = scheduleModel.getItem_placeid();
            Places.GeoDataApi.getPlaceById(U.getInstance().getmGoogleApiClient(), placeId)
                    .setResultCallback(places -> {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            // 위치명
                            holder.title.setText(myPlace.getName());
                            // 비동기로 사진가져오기
                            placePhotosTask(holder.image, holder.loadingImgPb, placeId);
                        } else {
                            U.getInstance().log("에러");
                        }
                        places.release();
                    });
        }
        // 이미지 클릭하면 정보페이지로
        holder.image.setOnClickListener(view -> {
            Intent intent = new Intent(context, ScheduleInfoActivity.class);
            intent.putExtra("scheduleModel", scheduleModel);
            intent.putExtra("position,", pos);
            intent.putExtra("fin", true);
            context.startActivity(intent);
        });
        // url로 웹브라우저창 띄우기
        holder.openUrlBtn.setOnClickListener(view -> {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(scheduleModel.getItem_url())));
            } catch (Exception e) {
                Toast.makeText(context, "잘못된 URL페이지이거나 이동할 URL페이지가 없습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        // 체크풀면 최종일정에서 삭제
        holder.checkIv.setOnClickListener(view -> {
            if(U.getInstance().getBoolean("login")) {
                NetProcess.getInstance().netCheckItem(new ScheduleModel(
                        U.getInstance().getUserModel().getUser_id(),
                        U.getInstance().tripDataModel.getTripNo(),
                        scheduleModel.getSchedule_date(),
                        scheduleModel.get_id()
                ));
            } else {
                updateSQLite(scheduleModel.getTrip_no(),
                        scheduleModel.getSchedule_no(),
                        0,
                        "최종일정에서 삭제되었습니다.");
                U.getInstance().getBus().post("DeleteItemSuccess");
            }
        });
        // 시간 가져오기
        if(scheduleModel.getItem_time() == null || scheduleModel.getItem_time().equals("null"))
            holder.timeTv.setText("00:00");
        else
            holder.timeTv.setText(scheduleModel.getItem_time());
        // 시간 클릭하면 시계뜸
        holder.timeTv.setOnClickListener(view ->
                new TimePickerDialog(context, (timePicker, i, i1) -> {
                    String hour = null;
                    String minute = null;
                    if(i<10) hour = "0" + i;
                    else hour = "" + i;
                    if(i1<10) minute = "0" + i1;
                    else minute = "" + i1;
                    holder.timeTv.setText(hour + ":" + minute);
                    if(U.getInstance().getBoolean("login")) {
                        NetProcess.getInstance().netTimeFinal(new ScheduleModel(
                                U.getInstance().tripDataModel.getTripNo(),
                                scheduleModel.getSchedule_date(),
                                scheduleModel.get_id(),
                                holder.timeTv.getText().toString()
                        ));
                    } else {
                        updateSQLite(scheduleModel.getTrip_no(),
                                scheduleModel.getSchedule_no(),
                                holder.timeTv.getText().toString(),
                                "시간이 변경되었습니다."
                        );
                        U.getInstance().getBus().post("DeleteItemSuccess");
                    }
                }, 0, 0, false).show()
        );
    }

    private void updateSQLite(int trip_no, int s_no, int check, String str) {
        try {
            String sql = "update ScheduleList_Table set" +
                    " item_check = '" + check + "'" +
                    " where trip_no = " + trip_no + " and schedule_no = " + s_no + " ;";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSQLite(int trip_no, int s_no, String time, String str) {
        try {
            String sql = "update ScheduleList_Table set" +
                    " item_time = '" + time + "'" +
                    " where trip_no = " + trip_no + " and schedule_no = " + s_no + " ;";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return scheduleModels==null ? 0 : scheduleModels.size();
    }

    private void placePhotosTask(ImageView imageView, ProgressBar progressBar, String placeId) {
        new PhotoTask(imageView.getWidth(), imageView.getHeight()) {
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                progressBar.setVisibility(View.GONE);
                if (attributedPhoto != null) imageView.setImageBitmap(attributedPhoto.bitmap);
            }
        }.execute(placeId);
    }
}
