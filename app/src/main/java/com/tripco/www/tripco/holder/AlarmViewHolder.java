package com.tripco.www.tripco.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tacademy on 2017-08-08.
 */

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.alarmContent) public TextView alarm_content;
    @BindView(R.id.alarmTime) public TextView alarm_time;
    @BindView(R.id.profilePhoto) public CircleImageView profile_photo;

    public AlarmViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
