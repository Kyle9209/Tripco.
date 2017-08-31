package com.tripco.www.tripco.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MarkerListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title_tv) public TextView titleTv;
    @BindView(R.id.address_tv) public TextView addressTv;
    @BindView(R.id.check_cb) public ImageView checkCb;
    @BindView(R.id.index_tv) public TextView indexTv;

    public MarkerListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
