package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeTripActivity extends AppCompatActivity {
    @BindView(R.id.title_et) EditText title_et;
    @BindView(R.id.title_check) ImageView titleCheck;
    @BindView(R.id.calendar_check) ImageView calenderCheck;
    @BindView(R.id.who_check) ImageView whoCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_trip);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.calendar, R.id.who, R.id.complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.calendar:
                startActivity(new Intent(this, CalendarActivity.class));
                titleCheck.setVisibility(View.VISIBLE);
                calenderCheck.setVisibility(View.VISIBLE);
                break;
            case R.id.who:
                startActivity(new Intent(this, WhoActivity.class));
                whoCheck.setVisibility(View.VISIBLE);
                break;
            case R.id.complete:
                if(TextUtils.isEmpty(title_et.getText().toString())){
                    title_et.setError(getString(R.string.error_field_required));
                    title_et.requestFocus();
                } else {
                    finish();
                }
                break;
        }
    }
}
