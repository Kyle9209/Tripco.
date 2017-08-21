package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.squareup.picasso.Picasso;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.util.U;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyPageActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_title_tv) TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_btn) Button toolbarRightBtn;
    @BindView(R.id.profile_civ) CircleImageView profile;
    @BindView(R.id.nickName_tv) TextView nickNameTv;
    @BindView(R.id.email_tv) TextView emailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);
        toolbarInit();

        nickNameTv.setText(U.getInstance().getMemberModel().getUser_nick());
        emailTv.setText(U.getInstance().getMemberModel().getUser_id());
    }

    private void toolbarInit(){
        toolbarTitleTv.setText("프로필");
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
        File file = fileData.getFile();
        if (file != null && file.exists()) {
            Picasso.with(profile.getContext())
                    .load(file)
                    .error(R.mipmap.ic_launcher_round)
                    .into(profile);
        } else {
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(profile.getContext(), R.mipmap.ic_launcher_round);
            profile.setImageDrawable(drawable);
        }
    }
    //==============================================================================================
}
