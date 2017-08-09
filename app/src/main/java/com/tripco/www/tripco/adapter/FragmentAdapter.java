package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.holder.FragmentViewHolder;
import com.tripco.www.tripco.model.ListModel;
import com.tripco.www.tripco.ui.CandidateInfoActivity;

import java.util.ArrayList;

/**
 * Created by kkmnb on 2017-08-08.
 */

public class FragmentAdapter extends RecyclerView.Adapter<FragmentViewHolder> {
    Context context;
    ArrayList<ListModel> listModels;

    public FragmentAdapter(Context context, ArrayList<ListModel> listModel) {
        this.context = context;
        this.listModels = listModel;
    }

    @Override
    public FragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_grid_list_layout, parent, false);
        return new FragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FragmentViewHolder holder, int position) {
        holder.image.setOnClickListener(view -> {
            context.startActivity((new Intent(context, CandidateInfoActivity.class)));
        });
    }

    @Override
    public int getItemCount() {
        return listModels==null ? 10 : listModels.size();
    }
}
