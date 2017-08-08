package com.tripco.www.tripco.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.rey.material.app.SimpleDialog;
import com.squareup.picasso.Picasso;
import com.tripco.www.tripco.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyPageActivity extends AppCompatActivity {
    @BindView(R.id.circleImageView) CircleImageView profile;
    @BindView(R.id.nickNameTitle) TextView nickNameTitle;
    @BindView(R.id.button2) Button button2;
    @BindView(R.id.emailTitle) TextView emailTitle;
    @BindView(R.id.button3) Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);

        profile.setOnClickListener(view -> {
            final SimpleDialog dialog = new SimpleDialog(MyPageActivity.this);
            dialog.title("방법을 선택하세요.")
                    .positiveAction("Camera")
                    .positiveActionTextColor(Color.BLACK)
                    .positiveActionClickListener(view1 -> {
                        onCamera();
                        dialog.dismiss();
                    })
                    .negativeAction("Photo")
                    .negativeActionTextColor(Color.BLACK)
                    .negativeActionClickListener(view12 -> {
                        onPhoto();
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    //닉네임 변경 페이지로 이동
    public void onClickNickname(View v) {
        Intent intent = new Intent(MyPageActivity.this, NicknameActivity.class);
        startActivityForResult(intent, 2); //값을 가져오기로 위해서
    }

    //닉네임 값 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 2 && data !=null)
        {
            String result = data.getStringExtra("resultSetting");
            nickNameTitle.setText(result);
        }else
            Toast.makeText(this, "오류가 났다", Toast.LENGTH_SHORT).show();
    }

    //비밀번호 변경 페이지로 이동
    public void onClickPW(View v) {
        Intent intent = new Intent(MyPageActivity.this, ModifyPasswordActivity.class);
        startActivity(intent);
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
