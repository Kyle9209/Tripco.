package com.tripco.www.tripco.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.tripco.www.tripco.ui.MainActivity;
import com.tripco.www.tripco.R;
import com.tripco.www.tripco.model.ResPushModel;

/**
 * Created by kkmnb on 2017-07-07.
 * FCM 메시지 수진 : fcm 메시지가 도착하면 자동 호출
 * 메시지 우선순위가 high가 아니면 안드6.0 이상에서는 백그라운드시 즉각적으로 도달하지 않을 수 있음
 */

public class TripcoFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    // 메시지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("FCM", "From > " + remoteMessage.getFrom());
        if(remoteMessage.getData().size() > 0){
            ResPushModel res = new Gson().fromJson(remoteMessage.getData().get("data"), ResPushModel.class);
            Log.d("FCM", "Message data payload > " + res.getBody());
            showNotification(res);
        }
    }

    // 상단 알림바 푸시알림
    public void showNotification(ResPushModel res){
        Intent itt = new Intent(this, MainActivity.class);
        itt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, itt, PendingIntent.FLAG_ONE_SHOT);
        // 알림 구성
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(res.getTitle())
                .setContentText(res.getBody())
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        // 알림 작동
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // 0 : 노티피케이션 고유 번호 -> 눌러서 시작하면 해당 번호를 넣어서 알림 삭제
        notificationManager.notify(0, nb.build());
    }
}
