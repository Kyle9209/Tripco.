package com.tripco.www.tripco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.frontLayout) LinearLayout frontLayout;
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
                progressBar.setProgress(newProgress);
            }
        });
        // 키보드 객체 획득
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        SectionsPagerAdapter spAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(spAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.searchBtn, R.id.saveBtn, R.id.DetailSaveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.searchBtn: // 주소검색
                String inputAddrStr = inputAddress.getText().toString();
                if(!TextUtils.isEmpty(inputAddrStr)){
                    String httpAddress = httpaddressCheck(inputAddrStr);
                    webView.loadUrl(httpAddress);
                    inputMethodManager.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0); // 키보드 내리기
                } else {
                    Toast.makeText(getContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.saveBtn: // 즉시저장
                Toast.makeText(getActivity(), "후보지리스트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.DetailSaveBtn: // 상세저장
                frontLayout.setVisibility(View.VISIBLE);
                frontLayout.setClickable(true); // 뒷부분 터치이벤트 막기
                break;
        }
    }

    @OnClick(R.id.completeBtn)
    public void onClickcompleteBtn(View view){
        frontLayout.setVisibility(View.GONE);
        Toast.makeText(getContext(), "후보지리스트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // "http://" 동적 추가
    private String httpaddressCheck(String inputUrl) {
        if(inputUrl.indexOf("http://") != -1) return inputUrl;
        else return "http://" + inputUrl;
    }

    public static class PlaceholderFragment extends Fragment{
        private static final String ARG_SECTION_NUMBER = "section_number";
        PlaceholderFragment con;

        public PlaceholderFragment() {
            con = this;
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.viewpager_detail, container, false);

            TextView tv = rootView.findViewById(R.id.testTxt);
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                tv.setText("유형선택");
            } else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                tv.setText("지역검색");
            } else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                tv.setText("날짜선택");
            } else if(getArguments().getInt(ARG_SECTION_NUMBER) == 4) {
                tv.setText("제목&메모");
            }

            return rootView;
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
