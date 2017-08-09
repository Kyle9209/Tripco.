package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootActivity;
import com.tripco.www.tripco.fragment.CandidateLIstFragment;
import com.tripco.www.tripco.fragment.FinalScheduleFragment;
import com.tripco.www.tripco.fragment.SearchingFragment;
import com.tripco.www.tripco.util.U;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripActivity extends RootActivity {
    @BindView(R.id.navigation) BottomNavigationView navigation;
    private FragmentTransaction ft;
    private boolean flag = true; // 탐색창 유지 플래그

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.searching:
                        if(flag) { // flag = true 일 때 탐색창 초기화
                            ft.replace(R.id.content, new SearchingFragment()).commit();
                            flag = false;
                        }
                        // flag = false 일 때 탐색창 유지
                        break;
                    case R.id.candidate_list:
                        showDialog(new CandidateLIstFragment());
                        break;
                    case R.id.final_schedule:
                        showDialog(new FinalScheduleFragment());
                        break;
                }
                return true;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.searching); // 초기 화면 -> 탐색
    }

    private void showDialog(Fragment fragment){
        if(navigation.getSelectedItemId() == R.id.searching) {
            U.getInstance().showAlertDialog(this, "주의!", "탐색페이지초기화",
                    "예",
                    (dialogInterface, i) -> {
                        flag = true;
                        ft.replace(R.id.content, fragment).commit();
                        dialogInterface.dismiss();
                    },
                    "아니오",
                    (dialogInterface, i) -> {
                        flag = false;
                        navigation.setSelectedItemId(R.id.searching);
                        dialogInterface.dismiss();
                    });
        } else {
            ft.replace(R.id.content, fragment).commit();
        }
    }
}
