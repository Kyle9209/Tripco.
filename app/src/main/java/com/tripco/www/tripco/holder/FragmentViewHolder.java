package com.tripco.www.tripco.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kkmnb on 2017-08-08.
 */

public class FragmentViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.img_iv) public ImageView image;
    @BindView(R.id.title_tv) public TextView title;
    @BindView(R.id.Check_cb) public CheckBox check;
    @BindView(R.id.move_btn) public Button move;

    public FragmentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
