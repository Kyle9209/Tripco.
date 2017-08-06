package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.holder.MainViewHolder;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.ui.MakeTripActivity;
import com.tripco.www.tripco.ui.TripActivity;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
    Context context;
    ArrayList<TripModel> tripModels;

    public MainAdapter(Context context, ArrayList<TripModel> tripModels) {
        this.context = context;
        this.tripModels = tripModels;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_trip_list_layout, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        // 데이터 셋팅
        final TripModel tripModel = tripModels.get(position);
        holder.title.setText(tripModel.getTitle());
        holder.who.setText(tripModel.getWho());
        holder.when.setText(tripModel.getStart() + " ~ " + tripModel.getEnd());
        holder.tag.setText(tripModel.getTag());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, tripModel.getTitle(), Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, TripActivity.class));
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MakeTripActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripModels==null ? 0 : tripModels.size();
    }
}
