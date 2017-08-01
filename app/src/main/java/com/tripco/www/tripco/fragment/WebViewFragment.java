package com.tripco.www.tripco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tripco.www.tripco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WebViewFragment extends Fragment {
    @BindView(R.id.inputAddress) EditText inputAddress;
    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.webViewPb) ProgressBar progressBar;
    private Unbinder unbinder;
    private InputMethodManager inputMethodManager;

    public WebViewFragment() {} // 생성자

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        webView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 허용
        webView.setWebViewClient(new WebViewClient(){ // 클릭시 새창 X
            @Override // 페이지 로딩이 끝나면 주소창 새로고침
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                inputAddress.setText(url);
                progressBar.setProgress(0);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); // 키보드 객체 획득
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.searchBtn, R.id.saveBtn, R.id.settingBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.searchBtn:
                String inputAddrStr = inputAddress.getText().toString();
                if(!TextUtils.isEmpty(inputAddrStr)){
                    String httpAddress = httpaddressCheck(inputAddrStr);
                    webView.loadUrl(httpAddress);
                    inputMethodManager.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0); // 키보드 내리기
                } else {
                    Toast.makeText(getContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.saveBtn:
                Toast.makeText(getActivity(), "후보지리스트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settingBtn:
                break;
        }
    }

    // "http://" 로 시작안하면 추가해줌
    private String httpaddressCheck(String inputUrl) {
        if(inputUrl.indexOf("http://") != -1) return inputUrl;
        else return "http://" + inputUrl;
    }
}
