package com.tripco.www.tripco;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

public class RootFragment extends Fragment {
    ProgressDialog progressDialog;

    public void showPD(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("잠시만 기다려주세요.");
        }
        progressDialog.show();
    }

    public void stopPD(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
