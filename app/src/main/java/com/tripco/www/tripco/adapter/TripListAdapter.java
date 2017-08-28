package com.tripco.www.tripco.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.holder.TripListViewHolder;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.ui.SetTripActivity;
import com.tripco.www.tripco.ui.TripActivity;
import com.tripco.www.tripco.util.U;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TripListAdapter extends RecyclerView.Adapter<TripListViewHolder> {
    private Context context;
    private ArrayList<TripModel> tripModels;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    public TripListAdapter(Context context, ArrayList<TripModel> tripModels) {
        this.context = context;
        this.tripModels = tripModels;
    }

    @Override
    public TripListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_trip_list_layout, parent, false);
        return new TripListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripListViewHolder holder, int position) {
        // 데이터 셋팅
        final TripModel tripModel = tripModels.get(position);
        // 제목
        holder.title.setText(tripModel.getTrip_title());
        // 누구
        // 파트너가 없다면
        if(tripModel.getPartner_id().equals("")) holder.who.setText("혼자");
        else { // 파트너가 있다면
            // 파트너아이디가 내 아이디라면
            if(tripModel.getPartner_id().equals(U.getInstance().getUserModel().getUser_id())){
                holder.who.setText(tripModel.getUser_id());
            } else {
                holder.who.setText(tripModel.getPartner_id());
            }
        }
        // 기간
        holder.when.setText(tripModel.getStart_date() + " ~ " + tripModel.getEnd_date());
        // 해시태그
        holder.tag.setText(tripModel.getHashtag());

        holder.itemView.setOnClickListener(view -> { // 짧게누르면 후보지리스트로
            // U에 있는 tripDataModel에 번호, 기간 2종류 저장
            U.getInstance().tripDataModel.setTripNo(tripModel.getTrip_no());
            // 스피너에서 쓰는 날짜 리스트도 U에 저장
            ArrayList<String> dateSpinnerList = new ArrayList<>();
            Date start = null;
            Date end   = null;
            try {
                start = dateFormat.parse(tripModel.getStart_date());
                end   = dateFormat.parse(tripModel.getEnd_date());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            int n = 1;
            while (true){
                String date = dateFormat.format(calendar.getTime());
                dateSpinnerList.add(n + "일차(" + date.replace("-",".") + ")");
                n++;
                calendar.add(Calendar.DATE, 1);
                if(calendar.getTime().after(end)) break;
            }
            U.getInstance().tripDataModel.setDateSpinnerList(dateSpinnerList);
            context.startActivity(new Intent(context, TripActivity.class));
        });

        holder.update.setOnClickListener(view -> { // 수정버튼 클릭
            Intent intent = new Intent(context, SetTripActivity.class);
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
    }

    @Override
    public int getItemCount() {
        return tripModels==null ? 0 : tripModels.size();
    }
}
