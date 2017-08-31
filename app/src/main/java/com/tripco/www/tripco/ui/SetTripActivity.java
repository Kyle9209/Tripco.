package com.tripco.www.tripco.ui;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.rey.material.app.BottomSheetDialog;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootActivity;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.TripModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.util.U;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetTripActivity extends RootActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.title_et) EditText titleEt;
    @BindView(R.id.clear_title_btn) Button clearTitleBtn;
    @BindView(R.id.title_cnt_tv) TextView titleCntTv;
    @BindView(R.id.calendar_tv) TextView calendarTv;
    @BindView(R.id.who_tv) TextView whoTv;
    @BindView(R.id.who_btn_line) LinearLayout whoBtnLine;
    @BindView(R.id.hashTag_gl) GridLayout hashTagGl;
    @BindView(R.id.delete_btn) Button deleteBtn;
    @BindView(R.id.discon_partner_btn) TextView disconPartnerBtn;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private int MAX_TITLE_CNT = 10; // 여행제목 최대 글자수 = 10
    private int trip_no;
    private String startDateString, endDateString;
    private Date startDate, endDate;
    private boolean dateSelectFlag; // 시작 날짜를 선택했는지 확인
    private boolean updateFlag = false; // 수정하기로 들어온건지 확인
    private BottomSheetDialog bottomSheetDialog; // 달력, 파트너찾기창
    private InputMethodManager imm; // 키보드 객체
    private EditText find_et;
    private Button findWhoFinishBtn;
    private String hashTags = "";
    private String partner = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_trip);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ButterKnife.bind(this);
        U.getInstance().getBus().register(this);
        toolbarInit();
        uiInit();
        getExtraData();
    }

    @Subscribe
    public void ottoBus(String str){
        // 찾기, 생성, 수정, 삭제 서버통신 실패
        if(str.equals("responseFailed")) stopPD();
        // 생성, 수정, 삭제 서버통신 성공
        if(str.equals("responseSuccess")) { stopPD(); finish(); }
        // 찾기 성공
        if(str.equals("findPartnerSuccess")){
            stopPD();
            bottomSheetDialog.findViewById(R.id.cardview).setVisibility(View.VISIBLE);
            findWhoFinishBtn.setEnabled(true);
            findWhoFinishBtn.setTextColor(Color.WHITE);
            // 파트너 이미지
            CircleImageView partnerProfileCiv = bottomSheetDialog.findViewById(R.id.partner_profile_civ);
            if(!U.getInstance().getPartnerModel().getUser_image().equals("default.jpg")) {
                Picasso.with(partnerProfileCiv.getContext())
                        .load(U.getInstance().getPartnerModel().getUser_image())
                        .error(R.drawable.default_my_page_profile)
                        .into(partnerProfileCiv);
            }
            // 파트너 이메일
            TextView partnerIdTv = bottomSheetDialog.findViewById(R.id.partner_id_tv);
            partnerIdTv.setText(U.getInstance().getPartnerModel().getUser_id());
            // 파트너 닉네임
            TextView partnerNickTv = bottomSheetDialog.findViewById(R.id.partner_nick_tv);
            partnerNickTv.setText(U.getInstance().getPartnerModel().getUser_nick());
        }
    }

    @Override
    protected void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void toolbarInit() {
        toolbarTitleTv.setText("여행 만들기");
        toolbarRightBtn.setText("완료");
    }

    private void uiInit(){
        titleEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        titleEt.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                imm.hideSoftInputFromWindow(titleEt.getWindowToken(), 0);
                return true;
            }
            return false;
        });
        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() <= MAX_TITLE_CNT)
                    titleCntTv.setText(editable.toString().length() + "/" + MAX_TITLE_CNT);
                if (TextUtils.isEmpty(titleEt.getText())) clearTitleBtn.setVisibility(View.INVISIBLE);
                else clearTitleBtn.setVisibility(View.VISIBLE);
            }
        });

        for (int i = 0; i < U.getInstance().tags.length; i++) {
            CheckBox checkBox = new CheckBox(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(10,10,10,10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                checkBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            checkBox.setLayoutParams(params);
            checkBox.setButtonDrawable(null);
            checkBox.setTextSize(18);
            checkBox.setMinWidth(350);
            checkBox.setPadding(5,5,5,5);
            checkBox.setBackgroundResource(R.drawable.hashtag);
            checkBox.setText(U.getInstance().tags[i]);
            checkBox.setTag(U.getInstance().tags[i]);
            hashTagGl.addView(checkBox);
        }
    }

    // 수정모드로 들어왔을때 기존 데이터 셋팅
    private void getExtraData() {
        TripModel sTripModel = (TripModel) getIntent().getSerializableExtra("serializableTripModel");
        if (sTripModel != null) {
            updateFlag = true;
            toolbarTitleTv.setText("여행 수정");
            deleteBtn.setVisibility(View.VISIBLE);
            trip_no = sTripModel.getTrip_no();
            titleEt.setText(sTripModel.getTrip_title());
            titleEt.setSelection(sTripModel.getTrip_title().length());
            startDateString = sTripModel.getStart_date();
            endDateString = sTripModel.getEnd_date();
            calendarTv.setText(startDateString + " ~ " + endDateString);
            if (U.getInstance().getBoolean("login")) { // 로그인되어있으면
                if(!sTripModel.getPartner_id().equals("")) { // 로그인 & 파트너아이디가 있다면
                    whoBtnLine.setClickable(false);
                    whoTv.setTextColor(Color.LTGRAY);
                    partner = sTripModel.getPartner_id();
                    // 파트너아이디가 내 아이디라면 유저아이디를 셋팅
                    if(partner.equals(U.getInstance().getUserModel().getUser_id())){
                        whoTv.setText(sTripModel.getUser_id());
                    } else {
                        whoTv.setText(partner);
                    }
                    //disconPartnerBtn.setVisibility(View.VISIBLE);
                }
            }
            for (int i = 0; i < U.getInstance().tags.length; i++) {
                CheckBox checkBox = (CheckBox) hashTagGl.getChildAt(i);
                if (sTripModel.getHashtag().contains(checkBox.getText())) checkBox.setChecked(true);
            }
        }
    }

    @OnClick({R.id.calendar_btn_line, R.id.who_btn_line, R.id.toolbar_right_btn,
            R.id.clear_title_btn, R.id.delete_btn, R.id.discon_partner_btn})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.clear_title_btn:
                titleEt.setText("");
                break;
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
                        CheckBox checkBox = (CheckBox) hashTagGl.getChildAt(i);
                        if (checkBox.isChecked()) hashTags += checkBox.getText();
                    }
                    if (U.getInstance().getBoolean("login")) {
                        // 로그인되어있으니 서버로
                        showPD();
                        if(updateFlag) setNetProcess("수정");
                        else setNetProcess("생성");
                    } else {
                        // 로그인되어있지않음 ->// 로컬디비로
                        if (updateFlag) updateSQLite(); // 수정으로 들어오면
                        else insertSQLite(); // 그냥 생성
                        U.getInstance().getBus().post("tripListInit");
                        finish();
                    }
                }
                break;
            case R.id.delete_btn:
                U.getInstance().showAlertDialog(this, "알림",
                        "본인의 여행 계획 및 저장된 데이터들이 모두 삭제됩니다. 계속 하시겠습니까?",
                        "계속", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            if(U.getInstance().getBoolean("login")){
                                showPD();
                                setNetProcess("삭제");
                            } else {
                                try {
                                    DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(
                                            "delete from Trip_Table where trip_no=" + trip_no);
                                    Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                U.getInstance().getBus().post("tripListInit");
                                finish();
                            }
                        },
                        "취소", (dialogInterface, i) -> dialogInterface.dismiss());
                break;
            case R.id.discon_partner_btn:
                U.getInstance().showAlertDialog(this, "알림", "파트너와의 연결을 끊으시겠습니까?",
                        "예", (dialogInterface, i) -> {
                            partner = "";
                            whoTv.setText("누구와?(선택)");
                            disconPartnerBtn.setVisibility(View.GONE);
                            Toast.makeText(this, "연결을 끊었습니다.", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        },
                        "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
                break;
        }
    }

    // 여행 생성, 수정, 삭제 서버 통신
    private void setNetProcess(String msg){
        if(msg.equals("생성")) {
            NetProcess.getInstance().netCrUpDeTrip(
                    new TripModel(titleEt.getText().toString(),
                            startDateString,
                            endDateString,
                            U.getInstance().getUserModel().getUser_id(),
                            partner,
                            hashTags), msg
            );
        } else if(msg.equals("수정")){
            NetProcess.getInstance().netCrUpDeTrip(
                    new TripModel(trip_no,
                            titleEt.getText().toString(),
                            startDateString,
                            endDateString,
                            hashTags), msg
            );
        } else if(msg.equals("삭제")){
            NetProcess.getInstance().netCrUpDeTrip(new TripModel(trip_no), msg);
        }
    }

    // 여행 수정 로컬디비
    private void updateSQLite() {
        try {
            String sql = "update Trip_Table set" +
                    " trip_title = '" + titleEt.getText().toString() + "'," +
                    " start_date = '" + startDateString + "'," +
                    " end_date = '" + endDateString + "'," +
                    " hashtag = '" + hashTags + "'" +
                    " where trip_no = " + trip_no + ";";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 여행 생성 로컬디비
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
                    "'" + startDateString + "', " +
                    "'" + endDateString + "', " +
                    "'', " +
                    "'', " +
                    "'" + hashTags + "');";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 제목, 기간 정했는지 확인
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

    // 달력화면
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
                    startDateString = selectedDate;
                    startDate = dateFormat.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.add(Calendar.MONTH, 1);
                calendar.state().edit()
                        .setMinimumDate(CalendarDay.from(startDate))
                        .setMaximumDate(CalendarDay.from(cal))
                        .commit();
                dateSelectFlag = true;
            } else {
                try {
                    endDateString = selectedDate;
                    endDate = dateFormat.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.selectRange(CalendarDay.from(startDate), CalendarDay.from(endDate));
            }
        });

        // 완료버튼
        toolbarRightBtn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(startDateString) || TextUtils.isEmpty(endDateString)) {
                Toast.makeText(SetTripActivity.this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                bottomSheetDialog.dismiss();
                calendarTv.setText(startDateString + " ~ " + endDateString);
            }
        });
    }

    // 찾기화면
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
                            new TripModel(U.getInstance().getUserModel().getUser_id(),
                                    find_et.getText().toString())
                    );
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        // 완료버튼
        findWhoFinishBtn.setOnClickListener(view -> {
            partner = U.getInstance().getPartnerModel().getUser_id();
            whoTv.setText(partner);
            disconPartnerBtn.setVisibility(View.VISIBLE);
            bottomSheetDialog.dismiss();
        });
    }

    // 달력, 찾기 화면을 띄우는 다이얼로그
    private void setBottomSheetDialog(int layout) {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog
                .contentView(layout)
                .heightParam(ViewGroup.LayoutParams.MATCH_PARENT)
                .inDuration(500)
                .cancelable(true)
                .show();
    }
}
