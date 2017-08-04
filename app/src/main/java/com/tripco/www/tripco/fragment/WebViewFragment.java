package com.tripco.www.tripco.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Spinner;
import com.tripco.www.tripco.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WebViewFragment extends Fragment implements OnMapReadyCallback {
    @BindView(R.id.url_et) EditText url_et;
    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.webview_pb) ProgressBar progressBar;
    @BindView(R.id.frontLayout) LinearLayout frontLayout;
    @BindView(R.id.days_spin) Spinner spinner;
    @BindView(R.id.before_btn) Button before_btn;
    @BindView(R.id.next_btn) Button next_btn;
    @BindView(R.id.line1) LinearLayout line1;
    @BindView(R.id.line2) LinearLayout line2;
    @BindView(R.id.line3) LinearLayout line3;
    @BindView(R.id.line4) LinearLayout line4;
    @BindView(R.id.title_tv) TextView title_tv;
    @BindView(R.id.map) MapView mapView;
    private Unbinder unbinder;
    private InputMethodManager inputMethodManager;
    private int index = 1;
    private GoogleMap mMap = null;

    public WebViewFragment() {} // 생성자

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        unbinder = ButterKnife.bind(this, view);

        // 키보드 객체 획득
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        webViewInit();
        spinnerInit();
        editTextInit(); // 키패드 완료 버튼 처리

        mapView.getMapAsync(this);

        return view;
    }

    private void webViewInit(){
        webView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 허용
        webView.setWebViewClient(new WebViewClient() { // 클릭시 새창 X
            @Override // 페이지 로딩이 끝나면 주소창 새로고침
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                url_et.setText(url);
                progressBar.setProgress(0);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        progressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
    }

    private void spinnerInit(){
        ArrayList<String> items = new ArrayList<String>(Arrays.asList("1일차", "2일차"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, items);
        adapter.add("3일차");
        adapter.notifyDataSetChanged();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void editTextInit(){
        url_et.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        url_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                onClickSearchUrlBtn();
                return true;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @OnClick(R.id.search_url_btn) // 주소검색
    public void onClickSearchUrlBtn(){
        String inputAddrStr = url_et.getText().toString();
        if (!TextUtils.isEmpty(inputAddrStr)) {
            String httpAddress = httpaddressCheck(inputAddrStr);
            webView.loadUrl(httpAddress);
            inputMethodManager.hideSoftInputFromWindow(url_et.getWindowToken(), 0); // 키보드 내리기
        } else {
            Toast.makeText(getContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({ R.id.save_btn, R.id.detail_save_btn, R.id.complete_btn, R.id.search_map_btn})
    public void onClickBtns(View view) {
        switch (view.getId()) {
            case R.id.save_btn: // 즉시저장
                Toast.makeText(getActivity(), "후보지리스트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.detail_save_btn: // 상세페이지 열기 / 초기화
                frontLayout.setVisibility(View.VISIBLE);
                index = 1;
                title_tv.setText("유형선택");
                line1.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);
                before_btn.setVisibility(View.INVISIBLE);
                frontLayout.setClickable(true); // 뒷부분 터치이벤트 막기
                break;
            case R.id.complete_btn: // 상세페이지 저장 / 알림창 / 열려있는 뷰 닫기
                final SimpleDialog dialog = new SimpleDialog(getContext());
                dialog.title("저장하시겠습니까?")
                        .positiveAction("예")
                        .positiveActionTextColor(Color.BLACK)
                        .positiveActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                line1.setVisibility(View.GONE);
                                line2.setVisibility(View.GONE);
                                line3.setVisibility(View.GONE);
                                line4.setVisibility(View.GONE);
                                mapView.setVisibility(View.GONE);
                                frontLayout.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "후보지리스트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .negativeAction("아니오")
                        .negativeActionTextColor(Color.BLACK)
                        .negativeActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.search_map_btn: // 구글맵띄우기
                mapView.setVisibility(View.VISIBLE);
                inputMethodManager.hideSoftInputFromWindow(url_et.getWindowToken(), 0);
                break;
        }
    }

    @OnClick({R.id.before_btn, R.id.next_btn}) // 상세페이지 이전, 다음 버튼
    public void onClickPageMoveBtn(View view) {
        switch (view.getId()) {
            case R.id.before_btn:
                next_btn.setVisibility(View.VISIBLE);
                index--;
                switch (index){
                    case 1:
                        line1.setVisibility(View.VISIBLE);
                        title_tv.setText("유형선택");
                        line2.setVisibility(View.GONE);
                        before_btn.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        line2.setVisibility(View.VISIBLE);
                        title_tv.setText("위치검색");
                        line3.setVisibility(View.GONE);
                        break;
                    case 3:
                        line3.setVisibility(View.VISIBLE);
                        title_tv.setText("날짜선택");
                        line4.setVisibility(View.GONE);
                        break;
                }
                break;
            case R.id.next_btn:
                before_btn.setVisibility(View.VISIBLE);
                index++;
                switch (index){
                    case 2:
                        line1.setVisibility(View.GONE);
                        title_tv.setText("위치검색");
                        line2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        line2.setVisibility(View.GONE);
                        title_tv.setText("날짜선택");
                        line3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        line3.setVisibility(View.GONE);
                        title_tv.setText("제목&메모");
                        line4.setVisibility(View.VISIBLE);
                        next_btn.setVisibility(View.INVISIBLE);
                        break;
                }
                break;
        }
    }

    // "http://" 동적 추가
    private String httpaddressCheck(String inputUrl) {
        if (inputUrl.indexOf("http://") != -1) return inputUrl;
        else return "http://" + inputUrl;
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
        super.onDestroy();
        //mapView.onLowMemory();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mapView != null) mapView.onCreate(savedInstanceState);
    }
    // 버터나이프 사용에 필요한 프레그먼트의 오버라이드 메소드
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
