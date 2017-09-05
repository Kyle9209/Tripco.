package com.tripco.www.tripco.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tripco.www.tripco.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        finish();
    }
}
