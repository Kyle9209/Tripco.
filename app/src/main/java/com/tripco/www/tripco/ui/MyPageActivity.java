package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.RootActivity;
import com.tripco.www.tripco.model.MemberModel;
import com.tripco.www.tripco.net.NetProcess;
import com.tripco.www.tripco.util.U;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MyPageActivity extends RootActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.profile_civ) CircleImageView profile;
    @BindView(R.id.nickName_tv) TextView nickNameTv;
    @BindView(R.id.email_tv) TextView emailTv;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);
        U.getInstance().getBus().register(this);
        toolbarInit();

        String imgUrl = U.getInstance().getUserModel().getUser_image();
        if(imgUrl != null && !imgUrl.equals("default.jpg")) {
            Picasso.with(profile.getContext())
                    .load(imgUrl)
                    .error(R.drawable.default_my_page_profile)
                    .into(profile);
        }
        nickNameTv.setText(U.getInstance().getUserModel().getUser_nick());
        emailTv.setText(U.getInstance().getUserModel().getUser_id());
    }

    @Subscribe
    public void ottoBus(String str){
        if(str.equals("StopSuccess")){
            stopPD();
            U.getInstance().getBus().post("logout");
            finish();
        }
        if(str.equals("getUserInfo")){
            stopPD();
            Picasso.with(profile.getContext())
                    .load(file)
                    .error(R.drawable.default_my_page_profile)
                    .into(profile);
        }
    }

    @Override
    protected void onDestroy() {
        U.getInstance().getBus().unregister(this);
        super.onDestroy();
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("프로필");
        toolbarTitleTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_person_outline_white_24dp, 0, 0, 0);
        toolbarTitleTv.setCompoundDrawablePadding(20);
        toolbarRightBtn.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.profile_civ)
    public void onClickProfile(){
        U.getInstance().showAlertDialog(this, "알림", "방법을 선택하세요.",
                "카메라",
                (dialogInterface, i) -> {
                    onCamera();
                    dialogInterface.dismiss();
                },
                "사진",
                (dialogInterface, i) -> {
                    onPhoto();
                    dialogInterface.dismiss();
                });
    }

    //닉네임 값 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 2 && data != null)
        {
            String result = data.getStringExtra("resultSetting");
            nickNameTv.setText(result);
        }
    }

    public void onClickView(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.change_pwd_line:
                intent = new Intent(MyPageActivity.this, ModifyPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.change_nick_line:
                intent = new Intent(MyPageActivity.this, ModifyNickActivity.class);
                intent.putExtra("nickName", nickNameTv.getText().toString());
                startActivityForResult(intent, 2); //값을 가져오기로 위해서
                break;
            case R.id.stop_member_line:
                U.getInstance().showAlertDialog(this, "주의!",
                        "탈퇴하시면 이후 모든 정보를 복구할 수 없습니다. 계속하시곘습니까?",
                        "예", (dialogInterface, i) -> {
                            showPD();
                            NetProcess.getInstance().netStop(
                                    new MemberModel(U.getInstance().getUserModel().getUser_id()));
                        },
                        "아니오", (dialogInterface, i) -> dialogInterface.dismiss());
                break;
        }
    }

    // 마이페이지 사진 설정============================================================================
    public void onCamera() {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        RxPaparazzo.single(this)
                .crop(options)
                .usingCamera()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    // See response.resultCode() doc
                    if (response.resultCode() != RESULT_OK) {
                        return;
                    }
                    bind(response.data());
                });
    }

    public void onPhoto() {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        RxPaparazzo.single(this)
                .crop(options)
                .usingGallery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    // See response.resultCode() doc
                    if (response.resultCode() != RESULT_OK) {
                        return;
                    }
                    bind(response.data());
                });
    }

    void bind(FileData fileData) {
        //이미지를 서버로 전송 ->
        file = fileData.getFile();
        if (file != null && file.exists()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
            MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("user_image",file.getName(),requestBody);
            RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), U.getInstance().getUserModel().getUser_id());

            showPD();
            NetProcess.getInstance().netChangeImg(multipartBodyPart, requestBody2);
        } else {
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(profile.getContext(), R.drawable.default_my_page_profile);
            profile.setImageDrawable(drawable);
        }
    }
    //==============================================================================================
}
