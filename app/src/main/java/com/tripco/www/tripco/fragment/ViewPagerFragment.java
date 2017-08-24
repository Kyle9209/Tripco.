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
    private String selectDate;
    private int cateNo;
    String n = "1";
    public ViewPagerFragment() {}

    @Override // 뷰페이저가 생성되는 최초 1번 trip_no, cate_no 초기화
    public void onCreate(@Nullable Bundle savedInstanceState) {
        cateNo = getArguments().getInt("cateNo");
        tripNo = U.getInstance().getTripNo();
        super.onCreate(savedInstanceState);
    }

    @Override // 뷰페이저 다시 시작점
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        unbinder = ButterKnife.bind(this, view);
        U.getInstance().getBus().register(this);
        selectDate = U.getInstance().getSelectDate();
        swipeRefreshInit();
        return view;
    }

    @Override // 리사이클러뷰 초기화(일부러 늦게함)
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recViewInit();
    }

    @Subscribe
    public void ottoBus(String str){
        if(str.equals(U.getInstance().getSelectDate())){
            selectDate = U.getInstance().getSelectDate();
        }
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

    private void recViewInit(){
        recyclerView.setLayoutManager
                (new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        CanScheduleListAdapter adapter =
                new CanScheduleListAdapter(getContext(), setScheduleModel(), n);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<ScheduleModel> setScheduleModel(){
        ArrayList<ScheduleModel> list = new ArrayList<>();
        String sql = "select * from ScheduleList_Table where trip_no=" + tripNo +
                " and schedule_date= '" + selectDate + "' and cate_no = " + cateNo + ";";
        Cursor csr = DBOpenHelper.dbOpenHelper.getWritableDatabase().rawQuery(sql, null);
        list.add(null);
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
        //csr.close();
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
