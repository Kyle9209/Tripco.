package com.tripco.www.tripco.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2017-08-09.
 */

public class FinalScheduleViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.time_tv) public TextView timeTv;
    @BindView(R.id.content_img) public TextView contentImg;
    @BindView(R.id.location_tv) public TextView locationtv;
    @BindView(R.id.final_checkBox) public CheckBox finalcheckBox;

    public FinalScheduleViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(itemView);
    }

}


