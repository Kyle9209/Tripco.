package com.tripco.www.tripco.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.rey.material.app.BottomSheetDialog;
import com.tripco.www.tripco.R;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeTripActivity extends AppCompatActivity {
    @BindView(R.id.title_et) EditText titleEt;
    @BindView(R.id.title_check) ImageView titleCheck;
    @BindView(R.id.calendar_btn) Button calendarBtn;
    @BindView(R.id.calendar_check) ImageView calendarCheck;
    @BindView(R.id.who_check) ImageView whoCheck;
    @BindView(R.id.hashtag_cb1) CheckBox hashTagCb1;
    @BindView(R.id.hashtag_cb2) CheckBox hashTagCb2;
    @BindView(R.id.hashtag_cb3) CheckBox hashTagCb3;
    @BindView(R.id.hashtag_cb4) CheckBox hashTagCb4;
    private String hashTags = "";
    private BottomSheetDialog bottomSheetDialog;
    private boolean selectFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_trip);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.calendar_btn, R.id.who_btn, R.id.complete_btn})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.calendar_btn:
                calendarOn();
                break;
            case R.id.who_btn:
                findWhoOn();
                break;
            case R.id.complete_btn:
                boolean check = true;

                if (TextUtils.isEmpty(titleEt.getText().toString())) {
                    titleEt.setError(getString(R.string.error_field_required));
                    titleEt.requestFocus();
                    check = false;
                }

                if (calendarCheck.getVisibility() == View.INVISIBLE) {
                    Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    calendarBtn.setBackgroundColor(Color.RED);
                    check = false;
                }

                if (check) {
                    if(hashTagCb1.isChecked()) hashTags = hashTags + hashTagCb1.getText().toString();
                    if(hashTagCb2.isChecked()) hashTags = hashTags + hashTagCb2.getText().toString();
                    if(hashTagCb3.isChecked()) hashTags = hashTags + hashTagCb3.getText().toString();
                    if(hashTagCb4.isChecked()) hashTags = hashTags + hashTagCb4.getText().toString();
                    finish();
                }
                break;
        }
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

    private void calendarOn() {
        selectFlag = false;
        setBottomSheetDialog(R.layout.bsd_calendar_layout);

        MaterialCalendarView calendar = bottomSheetDialog.findViewById(R.id.calendarView);
        TextView start = bottomSheetDialog.findViewById(R.id.start_day_tv);
        TextView end = bottomSheetDialog.findViewById(R.id.end_day_tv);

        calendar.setSelectedDate(new Date(System.currentTimeMillis())); // 현재날짜로 선택 초기화
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String selectedDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDay();

                if (!selectFlag) {
                    start.setText(selectedDate);
                    selectFlag = true;
                } else {
                    end.setText(selectedDate);
                }
                Toast.makeText(MakeTripActivity.this, selectedDate, Toast.LENGTH_SHORT).show();
            }
        });

        // 완료버튼
        bottomSheetDialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                calendarBtn.setBackgroundColor(Color.TRANSPARENT);
                calendarBtn.setText(start.getText() + " ~ " + end.getText());
                calendarCheck.setVisibility(View.VISIBLE);
            }
        });
    }

    private void findWhoOn() {
        setBottomSheetDialog(R.layout.bsd_who_layout);

        // 찾기버튼
        bottomSheetDialog.findViewById(R.id.find_btn).setOnClickListener(new View.OnClickListener() {
            @Override // 찾기버튼
            public void onClick(View view) {
                EditText find_et = bottomSheetDialog.findViewById(R.id.find_et);
                if (TextUtils.isEmpty(find_et.getText().toString())) {
                    find_et.setError(getString(R.string.error_field_required));
                    find_et.requestFocus();
                } else {
                    bottomSheetDialog.findViewById(R.id.relativeLayout).setVisibility(View.VISIBLE);
                    hideKeyboard(view);
                }
            }
        });

        // 완료버튼
        bottomSheetDialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                whoCheck.setVisibility(View.VISIBLE);
            }
        });
    }

    // 키보드 내리기
    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
