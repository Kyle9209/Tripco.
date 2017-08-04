package com.tripco.www.tripco.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WhoActivity extends AppCompatActivity {
    @BindView(R.id.relativeLayout) RelativeLayout relativeLayout;
    @BindView(R.id.find_et) EditText find_et;

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
        if(TextUtils.isEmpty(find_et.getText().toString())){
            find_et.setError(getString(R.string.error_field_required));
            find_et.requestFocus();
        } else {
            finish();
        }
    }

    // 키보드 내리기
    private void hideKeyboard(View view){
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
