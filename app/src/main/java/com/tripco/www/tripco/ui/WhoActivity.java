package com.tripco.www.tripco.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WhoActivity extends AppCompatActivity {
    @BindView(R.id.relativeLayout) RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.find_btn)
    public void onViewClicked(View view) {
        relativeLayout.setVisibility(View.VISIBLE);
        hideKeyboard(view);
    }

    @OnClick(R.id.complete)
    public void onclickComplete(){
        finish();
    }

    // 키보드 내리기
    private void hideKeyboard(View view){
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
