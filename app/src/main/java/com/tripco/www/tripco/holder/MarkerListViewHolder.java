package com.tripco.www.tripco.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kkmnb on 2017-08-17.
 */

public class MarkerListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title_tv) public TextView titleTv;
    @BindView(R.id.address_tv) public TextView addressTv;
    @BindView(R.id.check_cb) public CheckBox checkCb;

    public MarkerListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
