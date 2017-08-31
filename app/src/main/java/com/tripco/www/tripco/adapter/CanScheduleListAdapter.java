package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.Handler;
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

public class CanScheduleListAdapter extends RecyclerView.Adapter<ScheduleListViewHolder> {
    private Context context;
    private ArrayList<ScheduleModel> scheduleModels;
    private int pos;

    public CanScheduleListAdapter(Context context, ArrayList<ScheduleModel> scheduleModel, int pos) {
        this.context = context;
        this.scheduleModels = scheduleModel;
        this.pos = pos;
    }

    @Override
    public ScheduleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_candi_list_layout, parent, false);
        return new ScheduleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleListViewHolder holder, int position) {
        final ScheduleModel scheduleModel = scheduleModels.get(position);
        if(scheduleModel == null){
            holder.checkIv.setVisibility(View.GONE);
            holder.loadingImgPb.setVisibility(View.GONE);
            holder.openUrlBtn.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
            holder.title.setText("새로운 여행 추가");
            holder.defaultImageIv.setImageResource(R.drawable.add_image);
            holder.itemView.setOnClickListener(view ->
                new Handler().postDelayed(() -> U.getInstance().getBus().post("moveToSearching"),200));
        } else {
            // 위치명 확인 > 널이면 제목확인 > 널이면 제목없음
            if (scheduleModel.getItem_placeid() == null || scheduleModel.getItem_placeid().equals("null")) {
                holder.loadingImgPb.setVisibility(View.GONE);
                if (scheduleModel.getItem_title() == null || scheduleModel.getItem_title().equals("null"))
                    holder.title.setText("제목없음");
                else
                    holder.title.setText(scheduleModel.getItem_title());
            } else {
                // placeid로 위치명 가져오기
                String placeId = scheduleModel.getItem_placeid();
                Places.GeoDataApi.getPlaceById(U.getInstance().getmGoogleApiClient(), placeId)
                        .setResultCallback(places -> {
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                final Place myPlace = places.get(0);
                                holder.title.setText(myPlace.getName());
                                placePhotosTask(holder.image, holder.loadingImgPb, placeId);
                            } else {
                                U.getInstance().log("에러");
                            }
                            places.release();
                        });
            }
            // 클릭하면 정보페이지로
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ScheduleInfoActivity.class);
                intent.putExtra("scheduleModel", scheduleModel);
                intent.putExtra("position", pos);
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
            // 체크버튼
            if(scheduleModel.getItem_check() == 1)
                holder.checkIv.setImageResource(R.mipmap.ic_check_circle_white_18dp);
            else
                holder.checkIv.setImageResource(R.mipmap.ic_check_circle_black_18dp);
            holder.checkIv.setOnClickListener(view -> {
                if(U.getInstance().getBoolean("login")) {
                    if(scheduleModels.get(position).getItem_check() == 1){
                        scheduleModels.get(position).setItem_check(0);
                        holder.checkIv.setImageResource(R.mipmap.ic_check_circle_black_18dp);
                    } else {
                        scheduleModels.get(position).setItem_check(1);
                        holder.checkIv.setImageResource(R.mipmap.ic_check_circle_white_18dp);
                    }
                    NetProcess.getInstance().netCheckItem(new ScheduleModel(
                            U.getInstance().getUserModel().getUser_id(),
                            U.getInstance().tripDataModel.getTripNo(),
                            scheduleModel.getSchedule_date(),
                            scheduleModel.get_id()
                    ));
                } else {
                    if(scheduleModels.get(position).getItem_check() == 1){
                        scheduleModels.get(position).setItem_check(0);
                        holder.checkIv.setImageResource(R.mipmap.ic_check_circle_black_18dp);
                        updateSQLite(scheduleModel.getTrip_no(), scheduleModel.getSchedule_no(),
                                0, "최종일정에서 삭제되었습니다.");
                    } else {
                        scheduleModels.get(position).setItem_check(1);
                        holder.checkIv.setImageResource(R.mipmap.ic_check_circle_white_18dp);
                        updateSQLite(scheduleModel.getTrip_no(), scheduleModel.getSchedule_no(),
                                1, "최종일정에 추가되었습니다.");
                    }
                }
            });
        }
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

    @Override
    public int getItemCount() {
        return scheduleModels ==null ? 0 : scheduleModels.size();
    }

    private void placePhotosTask(ImageView imageView, ProgressBar progressBar, String placeId) {
        new PhotoTask(imageView.getWidth(), imageView.getHeight()) {
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                progressBar.setVisibility(View.GONE);
                U.getInstance().log("여길 몇번들어오는가");
                if (attributedPhoto != null) imageView.setImageBitmap(attributedPhoto.bitmap);
            }
        }.execute(placeId);
    }
}
