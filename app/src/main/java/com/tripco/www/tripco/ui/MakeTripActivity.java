package com.tripco.www.tripco.ui;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.rey.material.app.BottomSheetDialog;
import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootActivity;
import com.tripco.www.tripco.adapter.HashtagAdapter;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.util.U;

import java.text.ParseException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeTripActivity extends RootActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.title_et) EditText titleEt;
    @BindView(R.id.clear_title_btn) Button clearTitleBtn;
    @BindView(R.id.title_cnt_tv) TextView titleCntTv;
    @BindView(R.id.calendar_tv) TextView calendarTv;
    @BindView(R.id.who_tv) TextView whoTv;
    @BindView(R.id.hashtag_gv) GridView hashtagGv;
    @BindView(R.id.delete_btn) Button deleteBtn;
    private int MAX_TITLE_CNT = 10; // 여행제목 최대 글자수 = 10
    private int trip_no;
    private String startDate, endDate;
    private String hashTags = "";
    private final static String ALONE = "혼자";
    private boolean dateSelectFlag; // 시작 날짜를 선택했는지 확인
    private boolean updateFlag = false; // 수정하기로 들어온건지 확인
    private BottomSheetDialog bottomSheetDialog;
    private InputMethodManager imm; // 키보드 객체
    private Date stringToStartDate, stringToEndDate;
    EditText find_et;
    TripModel sTripModel = null;
    Button findWhoFinishBtn;
    String partner = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_trip);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ButterKnife.bind(this);
        U.getInstance().getBus().register(this);
        toolbarInit();
        hashtagGv.setAdapter(new HashtagAdapter(this));
        checkTitleCnt();
        getExtraData();
    }

    @Subscribe
    public void ottoBus(String str){
        if(str.equals("findPartnerSuccess")){
            stopPD();
            bottomSheetDialog.findViewById(R.id.cardview).setVisibility(View.VISIBLE);
            findWhoFinishBtn.setEnabled(true);
            findWhoFinishBtn.setTextColor(Color.WHITE);
            TextView partnerIdTv = bottomSheetDialog.findViewById(R.id.partner_id_tv);
            partnerIdTv.setText(U.getInstance().partnerModel.getUser_id());
            TextView partnerNickTv = bottomSheetDialog.findViewById(R.id.partner_nick_tv);
            partnerNickTv.setText(U.getInstance().partnerModel.getUser_nick());

        }
        if(str.equals("findPartnerFailed")){
            stopPD();
        }
        if(str.equals("makeTripSuccess")) {
            stopPD();
            finish();
        }
        if(str.equals("makeTripFailed")) stopPD();
    }

    @Override
    protected void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void getExtraData() {
        sTripModel = (TripModel) getIntent().getSerializableExtra("serializableTripModel");
        if (sTripModel != null) {
            updateFlag = true;
            toolbarTitleTv.setText("여행 수정");
            deleteBtn.setVisibility(View.VISIBLE);
            trip_no = sTripModel.getTrip_no();
            titleEt.setText(sTripModel.getTrip_title());
            startDate = sTripModel.getStart_date();
            endDate = sTripModel.getEnd_date();
            calendarTv.setText(startDate + " ~ " + endDate);
            if (!sTripModel.getPartner_id().equals(ALONE)) {
                // 혼자가 아니라면 친구도 셋팅
            }
            // 리스트 셋팅시간 때문에 0.3초 딜레이
            new Handler().postDelayed(() -> {
                for (int i = 0; i < U.getInstance().tags.length; i++) {
                    CheckBox checkBox = hashtagGv.getChildAt(i).findViewById(R.id.check_cb);
                    if (sTripModel.getHashtag().contains(checkBox.getText())) {
                        checkBox.setChecked(true);
                    }
                }
            }, 300);
        }
    }

    private void toolbarInit() {
        toolbarTitleTv.setText("여행 만들기");
        toolbarRightBtn.setText("완료");
    }

    @OnClick({R.id.calendar_btn_line, R.id.who_btn_line, R.id.toolbar_right_btn,
            R.id.clear_title_btn, R.id.delete_btn, R.id.discon_partner_btn})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.calendar_btn_line:
                imm.hideSoftInputFromWindow(titleEt.getWindowToken(), 0);
                onCalendar();
                break;
            case R.id.who_btn_line:
                if(U.getInstance().getBoolean("login")){
                    imm.hideSoftInputFromWindow(titleEt.getWindowToken(), 0);
                    onFindWho();
                } else {
                    U.getInstance().showAlertDialog(this, "알림", "로그인하시겠습니까?",
                            "예", (dialogInterface, i) -> {
                                startActivity(new Intent(this, LoginActivity.class));
                                dialogInterface.dismiss();
                            },
                            "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
                }
                break;
            case R.id.toolbar_right_btn:
                if (checkData(true)) {
                    for (int i = 0; i < U.getInstance().tags.length; i++) {
                        CheckBox checkBox = hashtagGv.getChildAt(i).findViewById(R.id.check_cb);
                        if (checkBox.isChecked()) {
                            hashTags += checkBox.getText();
                        }
                    }
                    if (U.getInstance().getBoolean("login")) { // 로그인되어있으니 서버로
                        showPD();
                        NetProcess.getInstance().netMakeTrip(
                                new TripModel(titleEt.getText().toString(),
                                        startDate,
                                        endDate,
                                        U.getInstance().getMemberModel().getUser_id(),
                                        partner,
                                        hashTags)
                        );
                    } else { // 로그인되어있지않음 ->// 로컬디비로
                        if (updateFlag) updateSQLite();
                        else insertSQLite();
                        finish();
                    }
                }
                break;
            case R.id.clear_title_btn:
                titleEt.setText("");
                break;
            case R.id.delete_btn:
                U.getInstance().showAlertDialog(this, "알림", "본인의 여행 계획 및 저장된 데이터들이 모두 삭제됩니다. 계속 하시겠습니다?",
                        "계속", (dialogInterface, i) -> {
                            try {
                                DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(
                                        "delete from Trip_Table where trip_no="+trip_no
                                );

                                U.getInstance().getBus().post("MAIN");
                                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "DB Error", Toast.LENGTH_SHORT).show();
                            }
                            dialogInterface.dismiss();
                            finish();
                        },
                        "취소", (dialogInterface, i) -> dialogInterface.dismiss());
                break;
            case R.id.discon_partner_btn:
                U.getInstance().showAlertDialog(this, "알림", "파트너와의 연결을 끊으시겠습니까?",
                        "예", (dialogInterface, i) -> {
                            partner = "";
                            whoTv.setText("누구와?(선택)");
                            Toast.makeText(this, "연결을 끊었습니다.", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        },
                        "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
                break;
        }
    }

    private void updateSQLite() {
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

    private void insertSQLite() {
        try {
            String sql = "insert into Trip_Table(" +
                    "trip_title, " +
                    "start_date, " +
                    "end_date, " +
                    "user_id, " +
                    "partner_id, " +
                    "hashtag) " +
                    "values(" +
                    "'" + titleEt.getText().toString() + "', " +
                    "'" + startDate + "', " +
                    "'" + endDate + "', " +
                    "'', " +
                    "'', " +
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

        if (calendarTv.getText().equals("언제?*")) {
            Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
            check = false;
        }

        return check;
    }

    private void checkTitleCnt() {
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
                    clearTitleBtn.setVisibility(View.INVISIBLE);
                else
                    clearTitleBtn.setVisibility(View.VISIBLE);
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

    private void onCalendar() {
        dateSelectFlag = false;
        setBottomSheetDialog(R.layout.bsd_calendar_layout);

        MaterialCalendarView calendar = bottomSheetDialog.findViewById(R.id.calendarView);
        TextView toolbarTitleTv = bottomSheetDialog.findViewById(R.id.toolbar_title_tv);
        Button toolbarRightBtn = bottomSheetDialog.findViewById(R.id.toolbar_right_btn);
        TextView text = bottomSheetDialog.findViewById(R.id.set_text_tv);
        toolbarTitleTv.setText("언제?");
        toolbarRightBtn.setText("완료");

        calendar.setSelectedDate(new Date(System.currentTimeMillis())); // 현재날짜로 선택 초기화
        calendar.setOnDateChangedListener((widget, date, selected) -> {
            String month;
            if(date.getMonth()<10) month = "0" + (date.getMonth() + 1);
            else month = "" + (date.getMonth() + 1);
            String day;
            if(date.getDay()<10) day = "0" + date.getDay();
            else day = "" + date.getDay();

            String selectedDate = date.getYear() + "-" + month + "-" + day;

            if (!dateSelectFlag) {
                text.setText("여행종료일을 설정하세요.");
                try {
                    startDate = selectedDate;
                    stringToStartDate = U.getInstance().getDateFormat().parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.state().edit()
                        .setMinimumDate(CalendarDay.from(stringToStartDate))
                        .commit();
                dateSelectFlag = true;
            } else {
                try {
                    endDate = selectedDate;
                    stringToEndDate = U.getInstance().getDateFormat().parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.selectRange(CalendarDay.from(stringToStartDate), CalendarDay.from(stringToEndDate));
            }
        });

        // 완료버튼
        toolbarRightBtn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
                Toast.makeText(MakeTripActivity.this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                bottomSheetDialog.dismiss();
                calendarTv.setText(startDate + " ~ " + endDate);
            }
        });
    }

    private void onFindWho() {
        setBottomSheetDialog(R.layout.bsd_who_layout);

        TextView toolbarTitleTv = bottomSheetDialog.findViewById(R.id.toolbar_title_tv);
        findWhoFinishBtn = bottomSheetDialog.findViewById(R.id.toolbar_right_btn);
        toolbarTitleTv.setText("누구와?");
        findWhoFinishBtn.setText("완료");
        findWhoFinishBtn.setEnabled(false);
        findWhoFinishBtn.setTextColor(Color.LTGRAY);

        // 찾기버튼
        bottomSheetDialog.findViewById(R.id.find_btn).setOnClickListener(view -> {
            find_et = bottomSheetDialog.findViewById(R.id.find_et);
            if (TextUtils.isEmpty(find_et.getText().toString())) {
                find_et.setError("입력해주세요.");
                find_et.requestFocus();
            } else {
                if (U.getInstance().getBoolean("login")) {
                    // 로그인되어있음 -> 서버에서 친구데이터 맞는지 확인
                    showPD();
                    NetProcess.getInstance().netPartner(
                            new TripModel(U.getInstance().getMemberModel().getUser_id(),
                                    find_et.getText().toString())
                    );
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        // 완료버튼
        findWhoFinishBtn.setOnClickListener(view -> {
            partner = U.getInstance().partnerModel.getUser_id();
            whoTv.setText(partner);
            bottomSheetDialog.dismiss();
        });
    }
}
