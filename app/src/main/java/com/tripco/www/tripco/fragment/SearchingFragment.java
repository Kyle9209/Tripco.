package com.tripco.www.tripco.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.Spinner;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.db.DBOpenHelper;
import com.tripco.www.tripco.model.AtoFModel;
import com.tripco.www.tripco.ui.TripActivity;
import com.tripco.www.tripco.util.U;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SearchingFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        TripActivity.onKeyBackPressedListener {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.url_et) EditText urlEt;
    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.webview_pb) ProgressBar progressBar;
    @BindView(R.id.frontLayout) LinearLayout frontLayout;
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.previous_btn) Button previousBtn;
    @BindView(R.id.next_btn) Button nextBtn;
    @BindView(R.id.line1) LinearLayout line1;
    @BindView(R.id.line2) LinearLayout line2;
    @BindView(R.id.line3) LinearLayout line3;
    @BindView(R.id.line4) LinearLayout line4;
    @BindView(R.id.title_et) EditText titleEt;
    @BindView(R.id.memo_et) EditText memoEt;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.rbs_rg) RadioGroup rbsGroup;
    private Unbinder unbinder;
    private InputMethodManager inputMethodManager;
    private int index = 1;
    private int tripNo;
    private String startDate, endDate;
    private GoogleMap mMap = null;
    private View view;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private LatLng latlng = null;
    private String placeName = "";

    public SearchingFragment() {} // 생성자

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AtoFModel atoFModel = (AtoFModel) getArguments().getSerializable("atoFModel");
        tripNo = atoFModel.getTrip_no();
        startDate = atoFModel.getStart_date();
        endDate = atoFModel.getEnd_date();
        //U.getInstance().getBus().register(this);
        view = inflater.inflate(R.layout.fragment_searching, container, false);
        unbinder = ButterKnife.bind(this, view);
        // 키보드 객체 획득
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        webViewInit();
        spinnerInit();
        editTextInit(); // 키패드 완료 버튼 처리
        toolbarInit();
        mapView.getMapAsync(this);
        return view;
    }

//    @Subscribe
//    public void ottoBus(AtoFModel model) {
//        U.getInstance().log(model.getTrip_no() + model.getStart_date() + model.getEnd_date());
//        tripNo = model.getTrip_no();
//        startDate = model.getStart_date();
//        endDate = model.getEnd_date();
//    }

    private void toolbarInit(){
        toolbarTitleTv.setText("유형선택");
        toolbarRightBtn.setText("완료");
    }

    private void webViewInit(){
        // ProgressBar 설정 ============================================================================

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.INVISIBLE);
                urlEt.setText(url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        progressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        //==========================================================================================
    }

    private void spinnerInit() {
        Date start = null;
        Date end = null;
        try {
            start = U.getInstance().getDateFormat().parse(startDate);
            end = U.getInstance().getDateFormat().parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, 0);
        int n = 1;
        while (true){
            adapter.add(n + "일차(" + U.getInstance().getDateFormat().format(calendar.getTime()) + ")");
            n++;
            calendar.add(Calendar.DATE, 1);
            if(calendar.getTime().after(end)) break;
        }
        adapter.notifyDataSetChanged();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void editTextInit(){
        urlEt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        urlEt.setOnEditorActionListener((textView, i, keyEvent) -> {
            onClickSearchUrlBtn();
            return true;
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @OnClick(R.id.search_url_btn) // URL주소검색 (호출하는 곳이 따로 있어서 따로둠)
    public void onClickSearchUrlBtn(){
        String inputAddrStr = urlEt.getText().toString();
        if (!TextUtils.isEmpty(inputAddrStr)) {
            String httpAddress = checkHttps(inputAddrStr);
            webView.loadUrl(httpAddress);
            inputMethodManager.hideSoftInputFromWindow(urlEt.getWindowToken(), 0); // 키보드 내리기
        } else {
            Toast.makeText(getContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.save_btn, R.id.detail_save_btn, R.id.toolbar_right_btn, R.id.search_address_btn})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.save_btn: // 즉시저장
                Toast.makeText(getActivity(), "후보지리스트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.detail_save_btn: // 상세페이지 열기 / 초기화
                frontLayout.setVisibility(View.VISIBLE);
                index = 1;
                toolbarTitleTv.setText("유형선택");
                line1.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                previousBtn.setVisibility(View.INVISIBLE);
                frontLayout.setClickable(true); // 뒷부분 터치이벤트 막기
                break;
            case R.id.toolbar_right_btn: // 상세페이지 저장 / 알림창 / 열려있는 뷰 닫기
                inputMethodManager.hideSoftInputFromWindow(urlEt.getWindowToken(), 0); // 키보드 내리기
                U.getInstance().showAlertDialog(getContext(), "알림", "저장하시겠습니까?",
                        "예",
                        (dialogInterface, i) -> {
                            saveDetail();
                            dialogInterface.dismiss();
                        },
                        "아니오",
                        (dialogInterface, i) -> dialogInterface.dismiss());
                break;
            case R.id.search_address_btn: // 구글플레이스 검색창 열기
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
        }
    }

    private void saveDetail(){
        RadioButton rb = rbsGroup.findViewById(rbsGroup.getCheckedRadioButtonId());
        String[] itemDate = spinner.getSelectedItem().toString().split("\\(");
        U.getInstance().log("url : " + urlEt.getText() +
                "\n선택한라디오버튼 : " + rb.getText().toString() +
                "\n위도경도 : " + latlng.latitude + "/" + latlng.longitude +
                "\n위치명 : " + placeName +
                "\n날짜 : " + itemDate[1].substring(0, itemDate[1].length()-1) +
                "\n제목 : " + titleEt.getText() +
                "\n메모 : " + memoEt.getText() +
                "\ntrip_no : " + tripNo);
        try {
            String sql = "insert into ScheduleList_Table(" +
                    "trip_no, "+
                    "schedule_date, "+
                    " item_url, "+
                    " cate_no, "+
                    " item_lat, "+
                    " item_long, "+
                    " item_location, "+
                    " item_title, "+
                    " item_memo) "+
                    "values(" +
                    "'" + tripNo + "', " +
                    "'" + itemDate[1].substring(0, itemDate[1].length()-1) + "', " +
                    "'" + urlEt.getText() + "', " +
                    "'" + getIntCateNo(rb.getText().toString())+ "', " +
                    "'" + latlng.latitude + "', " +
                    "'" + latlng.longitude + "', " +
                    "'" + placeName + "', " +
                    "'" + titleEt.getText() + "', " +
                    "'" + memoEt.getText() + "');";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);

            DetailInit();
            Toast.makeText(getContext(), "후보지리스트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getIntCateNo(String cateStr){
        switch (cateStr){
            case "관광":
                return 0;
            case "식사":
                return 1;
            case "숙박":
                return 2;
        }
        return -1;
    }

    private void DetailInit(){
        rbsGroup.clearCheck();
        titleEt.setText("");
        memoEt.setText("");
        spinner.setSelection(0);
        line1.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);
        line3.setVisibility(View.GONE);
        line4.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        frontLayout.setVisibility(View.GONE);
    }

    @Override // 구글 플레이스에서 검색한 데이터 받아서 처리
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                latlng = place.getLatLng();
                placeName = place.getName().toString();
                titleEt.setText(placeName);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latlng).title(placeName))
                        .showInfoWindow();
                CameraPosition ani = new CameraPosition.Builder()
                        .target(latlng)
                        .zoom(16)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(ani));
                mapView.setVisibility(View.VISIBLE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                U.getInstance().log("RESULT_ERROR 상태 : " + status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                U.getInstance().log("RESULT_CANCELED 상태");
            }
        }
    }

    @OnClick({R.id.previous_btn, R.id.next_btn}) // 상세페이지 이전, 다음 버튼
    public void onClickPageMoveBtn(View view) {
        switch (view.getId()) {
            case R.id.previous_btn:
                nextBtn.setVisibility(View.VISIBLE);
                index--;
                switch (index){
                    case 1:
                        line1.setVisibility(View.VISIBLE);
                        toolbarTitleTv.setText("유형선택");
                        line2.setVisibility(View.GONE);
                        previousBtn.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        line2.setVisibility(View.VISIBLE);
                        toolbarTitleTv.setText("위치검색");
                        line3.setVisibility(View.GONE);
                        break;
                    case 3:
                        line3.setVisibility(View.VISIBLE);
                        toolbarTitleTv.setText("날짜선택");
                        line4.setVisibility(View.GONE);
                        break;
                }
                break;
            case R.id.next_btn:
                previousBtn.setVisibility(View.VISIBLE);
                index++;
                switch (index){
                    case 2:
                        line1.setVisibility(View.GONE);
                        toolbarTitleTv.setText("위치검색");
                        line2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        line2.setVisibility(View.GONE);
                        toolbarTitleTv.setText("날짜선택");
                        line3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        line3.setVisibility(View.GONE);
                        toolbarTitleTv.setText("제목&메모");
                        line4.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.INVISIBLE);
                        break;
                }
                break;
        }
    }

    // "https://" 동적 추가
    private String checkHttps(String inputUrl) {
        if (inputUrl.indexOf("https://") != -1) return inputUrl;
        else if (inputUrl.indexOf("http://") != -1) return inputUrl;
        else return "https://" + inputUrl;
    }

    // 백키눌렀을때 웹뷰 뒤로가기
    public void onBack() {
        if(frontLayout.getVisibility() == View.VISIBLE )
        {
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
            line4.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);
            frontLayout.setVisibility(View.GONE);
        }else
        {
            if(webView.canGoBack()){
                webView.goBack();
            }else{
                TripActivity activity = (TripActivity) getActivity();
                activity.setOnKeyBackPressedListener(null);
                activity.onBackPressed();
            }
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((TripActivity) activity).setOnKeyBackPressedListener(this);
    }

    // 구글맵 사용에 필요한 프레그먼트의 오버라이드 메소드들
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onDestroy() {
        //U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mapView != null) mapView.onCreate(savedInstanceState);
    }
    @Override // 버터나이프 사용에 필요한 프레그먼트의 오버라이드 메소드
    public void onDestroyView() {
        mapView.onDestroy();
        super.onDestroyView();
        unbinder.unbind();
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null){
                parent.removeView(view);
            }
        }
    }
    @Override // 구글플레이스 연결 실패 처리
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
