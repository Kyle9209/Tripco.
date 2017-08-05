package com.tripco.www.tripco.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SimpleDialog dialog = new SimpleDialog(MyPageActivity.this);
                dialog.title("방법을 선택하세요.")
                        .positiveAction("Camera")
                        .positiveActionTextColor(Color.BLACK)
                        .positiveActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onCamera();
                                dialog.dismiss();
                            }
                        })
                        .negativeAction("Photo")
                        .negativeActionTextColor(Color.BLACK)
                        .negativeActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onPhoto();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

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
}
