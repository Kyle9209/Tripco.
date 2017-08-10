package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.holder.MainViewHolder;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.ui.MakeTripActivity;
import com.tripco.www.tripco.ui.TripActivity;
import com.tripco.www.tripco.util.U;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
    private Context context;
    private ArrayList<TripModel> tripModels;
    private final static String ALONE = "혼자";
    private final static String BUS_NAME = "MAIN";

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
        holder.title.setText(tripModel.getTrip_title());
        if(tripModel.getPartner_id().equals(ALONE)) holder.who.setText(ALONE);
        holder.when.setText(tripModel.getStart_date() + " ~ " + tripModel.getEnd_date());
        holder.tag.setText(tripModel.getHashtag());
        holder.itemView.setOnClickListener(view -> { // 짧게누르면 후보지리스트로
            context.startActivity(new Intent(context, TripActivity.class));
        });
        holder.update.setOnClickListener(view -> { // 수정버튼 클릭
            Intent intent = new Intent(context, MakeTripActivity.class);
            // putextra 처리
            TripModel serializableTripModel = new TripModel(tripModel.getTrip_no(),
                    tripModel.getTrip_title(),
                    tripModel.getStart_date(),
                    tripModel.getEnd_date(),
                    tripModel.getUser_id(),
                    tripModel.getPartner_id(),
                    tripModel.getHashtag());
            intent.putExtra("serializableTripModel", serializableTripModel);
            context.startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(view -> { // 길게누르면 삭제로
            U.getInstance().showAlertDialog(context, "알림", "삭제하시겠습니까?",
                    "예", (dialogInterface, i) -> {
                        try {
                            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(
                                    "delete from Trip_Table where trip_no="+tripModel.getTrip_no()
                            );
                            U.getInstance().getBus().post(BUS_NAME);
                            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "DB Error", Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.dismiss();
                    },
                    "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tripModels==null ? 0 : tripModels.size();
    }
}
