package com.tripco.www.tripco.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title_tv) public TextView title;
    @BindView(R.id.who_tv) public TextView who;
    @BindView(R.id.when_tv) public TextView when;
    @BindView(R.id.tag_tv) public  TextView tag;
    @BindView(R.id.update_btn) public Button update;

    public MainViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
