package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.tripco.www.tripco.PhotoTask;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.holder.ScheduleListViewHolder;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.ui.CandidateInfoActivity;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

public class CanScheduleListAdapter extends RecyclerView.Adapter<ScheduleListViewHolder>
        implements GoogleApiClient.OnConnectionFailedListener {
    private Context context;
    private ArrayList<ScheduleModel> scheduleModels;

    public CanScheduleListAdapter(Context context, ArrayList<ScheduleModel> scheduleModel) {
        this.context = context;
        this.scheduleModels = scheduleModel;
    }

    @Override
    public ScheduleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_candi_list_layout, parent, false);
        return new ScheduleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleListViewHolder holder, int position) {
        final ScheduleModel scheduleModel = scheduleModels.get(position);
        if(scheduleModel.getItem_check() == 1) holder.check.isChecked();
        // 위치명 확인 > 널이면 제목확인 > 널이면 제목없음
        if(scheduleModel.getItem_placeid().equals("null")){
            holder.loadingImgPb.setVisibility(View.GONE);
            if(scheduleModel.getItem_title().equals("")) holder.title.setText("제목없음");
            else holder.title.setText(scheduleModel.getItem_title());
        } else {
            // placeid로 위치명 가져오기
            String placeId = scheduleModel.getItem_placeid();
            GoogleApiClient mGoogleApiClient;
            if(U.getInstance().getmGoogleApiClient() == null) {
                mGoogleApiClient = new GoogleApiClient
                        .Builder(context)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage((FragmentActivity) context, this)
                        .build();
                U.getInstance().setmGoogleApiClient(mGoogleApiClient);
            } else {
                mGoogleApiClient = U.getInstance().getmGoogleApiClient();
            }

            Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
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
        holder.image.setOnClickListener(view -> {
            Intent intent = new Intent(context, CandidateInfoActivity.class);
            intent.putExtra("scheduleModel", scheduleModel);
            context.startActivity(intent);
        });
        holder.openUrlBtn.setOnClickListener(view -> {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(scheduleModel.getItem_url())));
            } catch (Exception e) {
                Toast.makeText(context, "잘못된 URL페이지이거나 이동할 URL페이지가 없습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        if(scheduleModel.getItem_check() == 1) holder.check.setChecked(true);
        holder.check.setOnCheckedChangeListener((ompoundButton, b) -> {
            if(b) {
                updateSQLite(scheduleModel.getTrip_no(),
                        scheduleModel.getSchedule_no(),
                        1,
                        "최종일정에 추가되었습니다.");
            } else {
                updateSQLite(scheduleModel.getTrip_no(),
                        scheduleModel.getSchedule_no(),
                        0,
                        "최종일정에서 삭제되었습니다.");
            }
        });
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
                if (attributedPhoto != null) {
                    progressBar.setVisibility(View.GONE);
                    imageView.setImageBitmap(attributedPhoto.bitmap);
                }
            }
        }.execute(placeId);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
