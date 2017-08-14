package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.tripco.www.tripco.PhotoTask;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.holder.ScheduleListViewHolder;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.ui.CandidateInfoActivity;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

public class FinScheduleListAdapter extends RecyclerView.Adapter<ScheduleListViewHolder>
        implements GoogleApiClient.OnConnectionFailedListener {
    Context context;
    private ArrayList<ScheduleModel> scheduleModels;

    public FinScheduleListAdapter(Context context, ArrayList<ScheduleModel> scheduleModels) {
        this.context = context;
        this.scheduleModels = scheduleModels;
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
    }

    @Override
    public int getItemCount() {
        return scheduleModels==null ? 0 : scheduleModels.size();
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
