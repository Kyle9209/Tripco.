package com.tripco.www.tripco.ui;

import android.content.Context;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.rey.material.app.BottomSheetDialog;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.util.U;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeTripActivity extends AppCompatActivity {
    @BindView(R.id.activity_title_tv) TextView titleTv;
    @BindView(R.id.title_et) EditText titleEt;
    @BindView(R.id.title_cnt_tv) TextView titleCntTv;
    @BindView(R.id.title_check) ImageView titleCheck;
    @BindView(R.id.calendar_btn) Button calendarBtn;
    @BindView(R.id.calendar_btn_line) LinearLayout calendarBtnLine;
    @BindView(R.id.calendar_check) ImageView calendarCheck;
    @BindView(R.id.who_check) ImageView whoCheck;
    @BindView(R.id.hashtag_cb1) CheckBox hashtagCb1;
    @BindView(R.id.hashtag_cb2) CheckBox hashtagCb2;
    @BindView(R.id.hashtag_cb3) CheckBox hashtagCb3;
    @BindView(R.id.hashtag_cb4) CheckBox hashtagCb4;
    @BindView(R.id.hashtag_cb5) CheckBox hashtagCb5;
    @BindView(R.id.hashtag_cb6) CheckBox hashtagCb6;
    @BindView(R.id.hashtag_cb7) CheckBox hashtagCb7;
    @BindView(R.id.hashtag_cb8) CheckBox hashtagCb8;
    @BindView(R.id.hashtag_cb9) CheckBox hashtagCb9;
    @BindView(R.id.hashtag_cb10) CheckBox hashtagCb10;
    @BindView(R.id.hashtag_cb11) CheckBox hashtagCb11;
    @BindView(R.id.hashtag_cb12) CheckBox hashtagCb12;
    @BindView(R.id.hashtag_cb13) CheckBox hashtagCb13;
    @BindView(R.id.hashtag_cb14) CheckBox hashtagCb14;
    @BindView(R.id.hashtag_cb15) CheckBox hashtagCb15;

    private int MAX_TITLE_CNT = 10; // 여행제목 최대 글자수 = 10
    private int trip_no;
    private String startDate, endDate;
    private String hashTags = "";
    private boolean dateSelectFlag; // 시작 날짜를 선택했는지 확인
    private boolean loginFlag = false; // 로그인되어있지 않다고 가정
    private boolean updateFlag = false; // 수정하기로 들어온건지 확인
    private BottomSheetDialog bottomSheetDialog;
    private InputMethodManager imm; // 키보드 객체
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private Date stringToStartDate, stringToEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_trip);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        titleCntCheck();

        TripModel sTripModel = (TripModel) getIntent().getSerializableExtra("serializableTripModel");
        if (sTripModel != null) {
            updateFlag = true;
            trip_no = sTripModel.getTrip_no();
            titleTv.setText("여행수정하기");
            titleEt.setText(sTripModel.getTrip_title());
            titleCheck.setVisibility(View.VISIBLE);
            startDate = sTripModel.getStart_date();
            endDate = sTripModel.getEnd_date();
            calendarBtn.setText(startDate + " ~ " + endDate);
            calendarCheck.setVisibility(View.VISIBLE);
            if (sTripModel.getPartner_no() != 0) {
                // 혼자가 아니라면 친구도 셋팅
            }
            if (sTripModel.getHashtag().contains(hashtagCb1.getText())) hashtagCb1.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb2.getText())) hashtagCb2.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb3.getText())) hashtagCb3.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb4.getText())) hashtagCb4.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb5.getText())) hashtagCb5.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb6.getText())) hashtagCb6.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb7.getText())) hashtagCb7.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb8.getText())) hashtagCb8.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb9.getText())) hashtagCb9.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb10.getText())) hashtagCb10.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb11.getText())) hashtagCb11.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb12.getText())) hashtagCb12.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb13.getText())) hashtagCb13.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb14.getText())) hashtagCb14.setChecked(true);
            if (sTripModel.getHashtag().contains(hashtagCb15.getText())) hashtagCb15.setChecked(true);
        }
    }

    @OnClick({R.id.calendar_btn, R.id.who_btn, R.id.complete_btn})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.calendar_btn:
                OnCalendar();
                break;
            case R.id.who_btn:
                OnFindWho();
                break;
            case R.id.complete_btn:
                if (checkData(true)) {
                    if (hashtagCb1.isChecked()) hashTags = hashTags + hashtagCb1.getText().toString();
                    if (hashtagCb2.isChecked()) hashTags = hashTags + hashtagCb2.getText().toString();
                    if (hashtagCb3.isChecked()) hashTags = hashTags + hashtagCb3.getText().toString();
                    if (hashtagCb4.isChecked()) hashTags = hashTags + hashtagCb4.getText().toString();
                    if (hashtagCb5.isChecked()) hashTags = hashTags + hashtagCb5.getText().toString();
                    if (hashtagCb6.isChecked()) hashTags = hashTags + hashtagCb6.getText().toString();
                    if (hashtagCb7.isChecked()) hashTags = hashTags + hashtagCb7.getText().toString();
                    if (hashtagCb8.isChecked()) hashTags = hashTags + hashtagCb8.getText().toString();
                    if (hashtagCb9.isChecked()) hashTags = hashTags + hashtagCb9.getText().toString();
                    if (hashtagCb10.isChecked()) hashTags = hashTags + hashtagCb10.getText().toString();
                    if (hashtagCb11.isChecked()) hashTags = hashTags + hashtagCb11.getText().toString();
                    if (hashtagCb12.isChecked()) hashTags = hashTags + hashtagCb12.getText().toString();
                    if (hashtagCb13.isChecked()) hashTags = hashTags + hashtagCb13.getText().toString();
                    if (hashtagCb14.isChecked()) hashTags = hashTags + hashtagCb14.getText().toString();
                    if (hashtagCb15.isChecked()) hashTags = hashTags + hashtagCb15.getText().toString();
                    if (loginFlag) { // 로그인되어있으니 서버로
                        finish();
                    } else { // 로그인되어있지않음 ->// 로컬디비로
                        if (updateFlag) SQLiteUpdate();
                        else SQLiteInsert();
                        finish();
                    }
                }
                break;
        }
    }

    private void SQLiteUpdate() {
        try {
            String sql = "update Trip_Table set" +
                    " trip_title = '" + titleEt.getText().toString() + "'," +
                    " start_date = '" + startDate + "'," +
                    " end_date = '" + endDate + "'," +
                    " hashtag = '" + hashTags + "'" +
                    " where trip_no = " + trip_no + ";";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void SQLiteInsert() {
        try {
            String sql = "insert into Trip_Table(" +
                    "trip_title, " +
                    "start_date, " +
                    "end_date, " +
                    "user_no, " +
                    "partner_no, " +
                    "hashtag) " +
                    "values(" +
                    "'" + titleEt.getText().toString() + "', " +
                    "'" + startDate + "', " +
                    "'" + endDate + "', " +
                    "'" + 0 + "', " +
                    "'" + 0 + "', " +
                    "'" + hashTags + "');";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkData(boolean check) {
        if (TextUtils.isEmpty(titleEt.getText().toString())) {
            titleEt.setError("제목을 입력해주세요.");
            titleEt.requestFocus();
            check = false;
        }

        if (calendarCheck.getVisibility() == View.INVISIBLE) {
            Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
            calendarBtnLine.setBackgroundColor(Color.RED);
            check = false;
        } else {
            calendarBtnLine.setBackgroundColor(Color.WHITE);
        }

        return check;
    }

    private void titleCntCheck() {
        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() <= MAX_TITLE_CNT)
                    titleCntTv.setText(editable.toString().length() + "/" + MAX_TITLE_CNT);
                if (TextUtils.isEmpty(titleEt.getText()))
                    titleCheck.setVisibility(View.INVISIBLE);
                else
                    titleCheck.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setBottomSheetDialog(int layout) {
        bottomSheetDialog = new BottomSheetDialog(this);

        bottomSheetDialog
                .contentView(layout)
                .heightParam(ViewGroup.LayoutParams.MATCH_PARENT)
                .inDuration(500)
                .cancelable(true)
                .show();
    }

    private void OnCalendar() {
        dateSelectFlag = false;
        setBottomSheetDialog(R.layout.bsd_calendar_layout);

        MaterialCalendarView calendar = bottomSheetDialog.findViewById(R.id.calendarView);
        TextView start = bottomSheetDialog.findViewById(R.id.start_day_tv);
        TextView end = bottomSheetDialog.findViewById(R.id.end_day_tv);

        calendar.setSelectedDate(new Date(System.currentTimeMillis())); // 현재날짜로 선택 초기화
        calendar.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDay();

            if (!dateSelectFlag) {
                start.setText(selectedDate);
                try {
                    stringToStartDate = dateFormat.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.state().edit()
                        .setMinimumDate(CalendarDay.from(stringToStartDate))
                        .commit();
                dateSelectFlag = true;
            } else {
                end.setText(selectedDate);
                try {
                    stringToEndDate = dateFormat.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.selectRange(CalendarDay.from(stringToStartDate), CalendarDay.from(stringToEndDate));
            }
        });

        // 완료버튼
        bottomSheetDialog.findViewById(R.id.complete_btn).setOnClickListener(view -> {
            if (TextUtils.isEmpty(start.getText()) || TextUtils.isEmpty(end.getText())) {
                Toast.makeText(MakeTripActivity.this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                bottomSheetDialog.dismiss();
                calendarBtnLine.setBackgroundColor(Color.WHITE);
                startDate = start.getText().toString();
                endDate = end.getText().toString();
                calendarBtn.setText(startDate + " ~ " + endDate);
                calendarCheck.setVisibility(View.VISIBLE);
            }
        });
    }

    private void OnFindWho() {
        setBottomSheetDialog(R.layout.bsd_who_layout);

        // 찾기버튼
        bottomSheetDialog.findViewById(R.id.find_btn).setOnClickListener(view -> {
            EditText find_et = bottomSheetDialog.findViewById(R.id.find_et);
            if (TextUtils.isEmpty(find_et.getText().toString())) {
                find_et.setError(getString(R.string.error_field_required));
                find_et.requestFocus();
            } else {
                if (loginFlag) {
                    // 로그인되어있음 -> 서버에서 친구데이터 맞는지 확인
                } else {
                    U.getInstance().showAlertDialog(this, "알림", "로그인하시겠습니까?",
                            "예", (dialogInterface, i) -> {
                                Toast.makeText(this, "아직 로그인 안돼요...", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            },
                            "아니오", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            });
                }
                bottomSheetDialog.findViewById(R.id.relativeLayout).setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        // 완료버튼
        bottomSheetDialog.findViewById(R.id.complete_btn).setOnClickListener(view -> {
            if (loginFlag) whoCheck.setVisibility(View.VISIBLE);
            bottomSheetDialog.dismiss();
        });
    }
}
