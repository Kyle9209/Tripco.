package com.tripco.www.tripco.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.squareup.otto.Subscribe;
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        ft = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.searching:
                if (flag) { // flag = true 일 때 탐색창 초기화
                    ft.replace(R.id.content, new SearchingFragment()).commit();
                    flag = false;
                } // flag = false 일 때 탐색창 유지
                break;
            case R.id.candidate:
                ft.replace(R.id.content, new CandidateLIstFragment()).commit();
                flag = true;
                //showDialog(new CandidateLIstFragment());
                break;
            case R.id.final_schedule:
                ft.replace(R.id.content, new FinalScheduleFragment()).commit();
                flag = true;
                //showDialog(new FinalScheduleFragment());
                break;
        }
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);
        U.getInstance().getBus().register(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.candidate); // 초기 화면 -> 후보지리스트
    }

    @Subscribe
    public void ottoBus(String str){
        if(str.equals("CANDIDATE_SEARCHING")) {
            ft = getSupportFragmentManager().beginTransaction();
            flag = true;
            Fragment fragment =new CandidateLIstFragment();
            Bundle bundle = new Bundle(1);
            bundle.putInt("i", 1);
            fragment.setArguments(bundle);
            ft.replace(R.id.content, fragment).commit();
            navigation.getMenu().findItem(R.id.candidate).setChecked(true);
        }
        if(str.equals("moveToCandidate")) navigation.setSelectedItemId(R.id.candidate);
        if(str.equals("moveToSearching")) navigation.setSelectedItemId(R.id.searching);
    }

    @Override
    protected void onDestroy() {
        if(U.getInstance().getmGoogleApiClient() != null) {
            U.getInstance().getmGoogleApiClient().disconnect();
            U.getInstance().setmGoogleApiClient(null);
        }
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void showDialog(Fragment fragment) {
        if (navigation.getSelectedItemId() == R.id.searching) {
            U.getInstance().showAlertDialog(this, "주의!", "탐색페이지가 초기화됩니다.",
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

    // 백키눌렀을때 웹뷰 뒤로가기
    private onKeyBackPressedListener mOnKeyBackPressedListener;
    public interface onKeyBackPressedListener {
        void onBack();
    }
    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }
    @Override
    public void onBackPressed() {
        if (mOnKeyBackPressedListener != null) {
            mOnKeyBackPressedListener.onBack();
        } else {
            super.onBackPressed();
        }
    }
}
