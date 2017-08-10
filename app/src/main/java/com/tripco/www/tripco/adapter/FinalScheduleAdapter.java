package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.holder.FinalScheduleViewHolder;
import com.tripco.www.tripco.model.FinalScheduleModel;

import java.util.ArrayList;

/**
 * Created by Tacademy on 2017-08-09.
 */

public class FinalScheduleAdapter extends RecyclerView.Adapter<FinalScheduleViewHolder> {

    Context context;
    ArrayList<FinalScheduleModel> finalScheduleModels;

    public FinalScheduleAdapter(Context context, ArrayList<FinalScheduleModel> finalScheduleModels) {
        this.context = context;
        this.finalScheduleModels = finalScheduleModels;
    }

    @Override
    public FinalScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_final_layout, parent, false);
        return new FinalScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FinalScheduleViewHolder holder, int position) {
        // 데이터 셋팅
    }

    @Override
    public int getItemCount() {
        return finalScheduleModels==null ? 10 : finalScheduleModels.size();
    }

}
