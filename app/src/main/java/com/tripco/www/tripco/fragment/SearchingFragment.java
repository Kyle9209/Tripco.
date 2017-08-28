package com.tripco.www.tripco.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.ScheduleModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.ui.SetScheduleActivity;
import com.tripco.www.tripco.ui.TripActivity;
import com.tripco.www.tripco.util.U;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SearchingFragment extends Fragment implements TripActivity.onKeyBackPressedListener {
    @BindView(R.id.url_et) EditText urlEt;
    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.webview_pb) ProgressBar webViewPb;
    @BindString(R.string.add_candidate) String addCandidateText;
    @BindString(R.string.url_search) String searchUrlText;
    @BindColor(R.color.colorAccent) int webViewPbColor;
    public final static int SET_SCHEDULE_REQUEST_CODE = 1000;
    public final static int SET_SCHEDULE_RESPONSE_CODE = 1001;
    ProgressDialog progressDialog;
    private Unbinder unbinder;
    private InputMethodManager imm;
    private int tripNo;

    public SearchingFragment() {} // 생성자

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searching, container, false);
        unbinder = ButterKnife.bind(this, view);
        U.getInstance().getBus().register(this);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        tripNo = U.getInstance().tripDataModel.getTripNo();
        webViewInit();
        editTextInit(); // 키패드 완료 버튼 처리
        return view;
    }

    @Subscribe
    public void ottoBus(String str){
        stopPD();
        if(str.equals("CreateItemSuccess")){
            U.getInstance().showAlertDialog(getContext(), "알림", addCandidateText + "\n후보지로 이동하시겠습니까?",
                    "예", (dialogInterface, i) -> U.getInstance().getBus().post("moveToCandidate"),
                    "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
        }
    }

    private void webViewInit() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webViewPb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webViewPb.setVisibility(View.GONE);
                urlEt.setText(url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                webViewPb.setProgress(newProgress);
            }

            // 메소드 내부에서 재정의하여 팝업 자체 상단에 보이는 url을 숨김==================================
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
            // 메소드 내부에서 재정의하여 팝업 자체 상단에 보이는 url을 숨김==================================
        });
        webViewPb.getProgressDrawable().setColorFilter(webViewPbColor, PorterDuff.Mode.SRC_IN);
    }

    private void editTextInit() {
        urlEt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        urlEt.setOnEditorActionListener((textView, i, keyEvent) -> {
            onClickSearchUrlBtn();
            return true;
        });
    }

    @OnClick(R.id.search_url_btn) // URL주소검색 (호출하는 곳이 따로 있어서 따로둠)
    public void onClickSearchUrlBtn() {
        webView.setVisibility(View.VISIBLE);
        String inputAddrStr = urlEt.getText().toString();
        if (!TextUtils.isEmpty(inputAddrStr)) {
            String httpAddress = checkHttps(inputAddrStr);
            webViewPb.setVisibility(View.VISIBLE);
            webView.loadUrl(httpAddress);
            imm.hideSoftInputFromWindow(urlEt.getWindowToken(), 0);
        } else {
            Toast.makeText(getContext(), searchUrlText, Toast.LENGTH_SHORT).show();
        }
    }

    // "https://" 동적 추가
    private String checkHttps(String inputUrl) {
        if (inputUrl.indexOf("https://") != -1) return inputUrl;
        else return "https://" + inputUrl;
    }

    @OnClick({R.id.short_save_btn, R.id.show_detail_btn})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.short_save_btn: // 즉시저장
                if (!TextUtils.isEmpty(urlEt.getText())) {
                    insertSQLite(0, 0, null, null, null, null, null);
                } else {
                    showDialog();
                }
                break;
            case R.id.show_detail_btn: // 상세페이지 열기 / 초기화
                if (!TextUtils.isEmpty(urlEt.getText())) {
                    imm.hideSoftInputFromWindow(urlEt.getWindowToken(), 0);
                    Intent intent = new Intent(getContext(), SetScheduleActivity.class);
                    startActivityForResult(intent, SET_SCHEDULE_REQUEST_CODE);
                } else {
                    showDialog();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_SCHEDULE_REQUEST_CODE) {
            if (resultCode == SET_SCHEDULE_RESPONSE_CODE) {
                ScheduleModel scheduleModel = (ScheduleModel) data.getSerializableExtra("data");

                insertSQLite(
                        scheduleModel.getSchedule_date(),
                        scheduleModel.getCate_no(),
                        scheduleModel.getItem_lat(),
                        scheduleModel.getItem_long(),
                        scheduleModel.getItem_placeid(),
                        scheduleModel.getItem_title(),
                        scheduleModel.getItem_memo()
                );
            }
        }
    }

    public void showDialog() {
        U.getInstance().showAlertDialog(getContext(), "알림", "URL을 입력한 뒤 이용해주세요. " +
                        "URL없이 저장은 후보지 > 위치검색을 통해 가능합니다.",
                "URL 입력", (dialogInterface, i) -> {
                    urlEt.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    dialogInterface.dismiss();
                },
                "위치 검색", (dialogInterface, i) -> {
                    //위치검색으로 이동
                    U.getInstance().getBus().post("moveToCandidateOnSearch");
                    dialogInterface.dismiss();
                });
    }

    private void insertSQLite(int datePosition, int cateNo, String lat, String lng,
                              String placeId, String title, String memo) {
        if(U.getInstance().getBoolean("login")){
            showPD();
            NetProcess.getInstance().netCreateItem(new ScheduleModel(
                    U.getInstance().getUserModel().getUser_id(),
                    tripNo,
                    datePosition,
                    urlEt.getText().toString(),
                    cateNo,
                    lat,
                    lng,
                    placeId,
                    title,
                    memo
            ));
        } else {
            try {
                String sql = "insert into ScheduleList_Table(" +
                        " trip_no, " +
                        " schedule_date, " +
                        " item_url, " +
                        " cate_no, " +
                        " item_lat, " +
                        " item_long, " +
                        " item_placeid, " +
                        " item_title, " +
                        " item_memo) " +
                        " values(" +
                        "'" + tripNo + "', " +
                        "'" + datePosition + "', " + // 초기값 0 (1일차)
                        "'" + urlEt.getText().toString() + "', " +
                        "'" + cateNo + "', " + // 초기값 cate_no = 0(관광)
                        "'" + lat + "', " + // 초기값 lat = null
                        "'" + lng + "', " + // 초기값 lng = null
                        "'" + placeId + "', " + // 초기값 placeId = null
                        "'" + title + "', " + // 초기값 title = null
                        "'" + memo + "');"; // 초기값 memo = null;
                DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);
                U.getInstance().showAlertDialog(getContext(), "알림", addCandidateText + "\n후보지로 이동하시겠습니까?",
                        "예", (dialogInterface, i) -> U.getInstance().getBus().post("moveToCandidate"),
                        "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 로딩중 알림창
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

    // 백키눌렀을때 웹뷰 뒤로가기
    public void onBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            TripActivity activity = (TripActivity) getActivity();
            activity.setOnKeyBackPressedListener(null);
            activity.onBackPressed();
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((TripActivity) activity).setOnKeyBackPressedListener(this);
    }

    @Override // 버터나이프, 오토버스
    public void onDestroyView() {
        unbinder.unbind();
        U.getInstance().getBus().unregister(this);
        super.onDestroyView();
    }
}
