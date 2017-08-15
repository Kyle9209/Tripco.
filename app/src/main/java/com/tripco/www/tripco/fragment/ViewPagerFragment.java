package com.tripco.www.tripco.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.adapter.CanScheduleListAdapter;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.FtoFModel;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ViewPagerFragment extends Fragment {
    @BindView(R.id.grid_list_rv) RecyclerView recyclerView;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private Unbinder unbinder;
    private View view;
    private int tripNo;
    private String scheduleDate;
    private int cateNo;
    String n;
    public ViewPagerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FtoFModel ftoFModel = (FtoFModel) getArguments().getSerializable("ftoFModel");
        tripNo = ftoFModel.getTrip_no();
        scheduleDate = ftoFModel.getSchedule_date();
        cateNo = ftoFModel.getCate_no();
        n = getArguments().getString("n");
        view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        unbinder = ButterKnife.bind(this, view);
        swipeRefreshInit();
        U.getInstance().getBus().register(this);
        return view;
    }

    @Subscribe
    public void ottoBus(String str){
        scheduleDate = str;
        recViewInit();
    }

    public void swipeRefreshInit(){
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            new Handler().postDelayed(() -> {
                recViewInit();
                swipeContainer.setRefreshing(false);
            }, 3000);
            //recyclerView.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recViewInit();
    }

    private void recViewInit(){
        recyclerView.setLayoutManager
                (new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        CanScheduleListAdapter adapter =
                new CanScheduleListAdapter(getContext(), setScheduleModel(), n);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<ScheduleModel> setScheduleModel(){
        ArrayList<ScheduleModel> list = new ArrayList<>();
        String sql = "select * from ScheduleList_Table where trip_no=" +tripNo +
                " and schedule_date= '" + scheduleDate + "' and cate_no = " +cateNo + ";";
        Cursor csr = DBOpenHelper.dbOpenHelper.getWritableDatabase().rawQuery(sql, null);
        while (csr.moveToNext()) {
            list.add(new ScheduleModel(
                    csr.getInt(0),
                    csr.getInt(1),
                    csr.getString(2),
                    csr.getString(3),
                    csr.getInt(4),
                    csr.getString(5),
                    csr.getString(6),
                    csr.getString(7),
                    csr.getString(8),
                    csr.getString(9),
                    csr.getInt(10),
                    csr.getString(11)
            ));
        }
        csr.close();
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        U.getInstance().getBus().unregister(this);
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null){
                parent.removeView(view);
            }
        }
    }
}
