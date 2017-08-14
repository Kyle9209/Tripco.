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

public class SearchingFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, TripActivity.onKeyBackPressedListener {
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
    @BindView(R.id.google_places_btn) Button googlePlacesbtn;
    //@BindView(R.id.add_memo_btn) Button addMemoBtn;
    private Unbinder unbinder;
    private InputMethodManager inputMethodManager;
    private int index = 1;
    //private int memoIdx = 0;
    private int tripNo;
    private String startDate, endDate;
    private GoogleMap mMap = null;
    private View view;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private LatLng latlng = null;
    private Double lat = null;
    private Double lng = null;
    private String placeId = null;
    private String rbStr = U.getInstance().category0;
    //private String memo = null;
    private boolean mapFlag = false;
    final static String INIT_TITLE = "무엇에 대한 정보인가요?";

    public SearchingFragment() {} // 생성자

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AtoFModel atoFModel = (AtoFModel) getArguments().getSerializable("atoFModel");
        tripNo = atoFModel.getTrip_no();
        startDate = atoFModel.getStart_date();
        endDate = atoFModel.getEnd_date();
        view = inflater.inflate(R.layout.fragment_searching, container, false);
        unbinder = ButterKnife.bind(this, view);
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        webViewInit();
        spinnerInit();
        editTextInit(); // 키패드 완료 버튼 처리
        toolbarInit();
        mapView.getMapAsync(this);
        return view;
    }

    private void toolbarInit(){
        toolbarTitleTv.setText(INIT_TITLE);
        toolbarRightBtn.setText("저장");
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
            adapter.add(n + "일차(" + U.getInstance().getDateFormat().format(calendar.getTime()).replace("-",".") + ")");
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

    @OnClick({R.id.short_save_btn, R.id.show_detail_btn,
              R.id.toolbar_right_btn, R.id.google_places_btn/*, R.id.add_memo_btn*/})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.short_save_btn: // 즉시저장
                if(!TextUtils.isEmpty(urlEt.getText())) {
                    insertSQLite();
                } else {
                    showDialog();
                }
                break;
            case R.id.show_detail_btn: // 상세페이지 열기 / 초기화
                if(!TextUtils.isEmpty(urlEt.getText())) {
                    frontLayout.setVisibility(View.VISIBLE);
                    index = 1;
                    toolbarTitleTv.setText(INIT_TITLE);
                    line1.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                    previousBtn.setVisibility(View.INVISIBLE);
                    frontLayout.setClickable(true); // 뒷부분 터치이벤트 막기
                } else {
                    showDialog();
                }
                break;
            case R.id.toolbar_right_btn: // 상세페이지 저장 / 알림창 / 열려있는 뷰 닫기
                inputMethodManager.hideSoftInputFromWindow(urlEt.getWindowToken(), 0); // 키보드 내리기
                U.getInstance().showAlertDialog(getContext(), "알림", "저장하시겠습니까?",
                        "예",
                        (dialogInterface, i) -> {
                            if(rbsGroup.getCheckedRadioButtonId() > 0) {
                                RadioButton rb = rbsGroup.findViewById(rbsGroup.getCheckedRadioButtonId());
                                rbStr = rb.getText().toString();
                            }
                            if(latlng != null) {
                                lat = latlng.latitude;
                                lng = latlng.longitude;
                            }
//                            memo = memoEt.getText().toString();
//                            for (int j = 1; j <= memoIdx; j++) {
//                                EditText et = view.findViewById(memoEt.getId()+j);
//                                memo += "/" + et.getText().toString();
//                                U.getInstance().log("메모추가");
//                            }
                            insertSQLite();
                            dialogInterface.dismiss();
                        },
                        "아니오",
                        (dialogInterface, i) -> dialogInterface.dismiss());
                break;
            case R.id.google_places_btn: // 구글플레이스 검색창 열기
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
//            case R.id.add_memo_btn: // 메모추가
//                if(memoIdx < 4) {
//                    memoIdx++;
//                    EditText editText = new EditText(getContext());
//                    editText.setSingleLine();
//                    editText.setId(memoEt.getId()+memoIdx);
//                    editText.setHint("메모를 추가하세요.");
//                    line4.addView(editText);
//                    if(memoIdx == 4) addMemoBtn.setEnabled(false);
//                }
//                break;
        }
    }

    public void showDialog(){
        U.getInstance().showAlertDialog(getContext(), "알림", "URL을 입력한 뒤 이용해주세요." +
                        "URL없이 저장은 후보지 > 위치검색을 통해 가능합니다.",
                "URL입력", (dialogInterface, i) -> {
                    urlEt.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
                    dialogInterface.dismiss();
                },
                "위치 검색", (dialogInterface, i) -> {
                    //위치검색으로 이동
                    dialogInterface.dismiss();
                });
    }

    private void insertSQLite() {
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
                    "'" + U.getInstance().getDate(spinner.getSelectedItem().toString()).replace(".","-") + "', " +
                    "'" + urlEt.getText() + "', " +
                    "'" + getIntCateNo(rbStr) + "', " +
                    "'" + lat + "', " +
                    "'" + lng + "', " +
                    "'" + placeId + "', " +
                    "'" + titleEt.getText() + "', " +
                    "'" + memoEt.getText() + "');";
            DBOpenHelper.dbOpenHelper.getWritableDatabase().execSQL(sql);

            detailInit();
            Toast.makeText(getContext(), "후보지리스트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getIntCateNo(String cateStr){
        if(cateStr.equals(U.getInstance().category0)) return 0;
        if(cateStr.equals(U.getInstance().category1)) return 1;
        if(cateStr.equals(U.getInstance().category2)) return 2;
        return -1;
    }

    private void detailInit(){
        rbsGroup.clearCheck();
        titleEt.setText("");
        memoEt.setText("");
        spinner.setSelection(0);
        latlng = null;
        lat = null;
        lng = null;
        //memo = null;
        placeId = null;
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
                mapFlag = true;
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                googlePlacesbtn.setText(place.getName());
                placeId = place.getId();
                latlng = place.getLatLng();
                titleEt.setText(place.getName());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latlng).title(place.getName().toString()))
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
                        toolbarTitleTv.setText(INIT_TITLE);
                        line2.setVisibility(View.GONE);
                        mapView.setVisibility(View.GONE);
                        previousBtn.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        if(mapFlag) mapView.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.VISIBLE);
                        toolbarTitleTv.setText("위치를 알려주세요!");
                        line3.setVisibility(View.GONE);
                        break;
                    case 3:
                        line3.setVisibility(View.VISIBLE);
                        toolbarTitleTv.setText("언제 방문하면 좋을까요?");
                        mapView.setVisibility(View.GONE);
                        line4.setVisibility(View.GONE);
                        break;
                }
                break;
            case R.id.next_btn:
                previousBtn.setVisibility(View.VISIBLE);
                index++;
                switch (index){
                    case 2:
                        if(mapFlag) mapView.setVisibility(View.VISIBLE);
                        line1.setVisibility(View.GONE);
                        toolbarTitleTv.setText("위치를 알려주세요!");
                        line2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        line2.setVisibility(View.GONE);
                        mapView.setVisibility(View.GONE);
                        toolbarTitleTv.setText("언제 방문하면 좋을까요?");
                        line3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        line3.setVisibility(View.GONE);
                        toolbarTitleTv.setText("제목&메모를 입력해주세요!");
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
