package com.tripco.www.tripco.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kkmnb on 2017-08-08.
 */

public class ScheduleListViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.img_iv) public ImageView image;
    @BindView(R.id.title_tv) public TextView title;
    @BindView(R.id.check_iv) public ImageView checkIv;
    @BindView(R.id.open_url_btn) public ImageButton openUrlBtn;
    @BindView(R.id.loading_img_pb) public ProgressBar loadingImgPb;
    @BindView(R.id.time_tv) public TextView timeTv;
    @BindView(R.id.default_image_iv) public ImageView defaultImageIv;

    public ScheduleListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
