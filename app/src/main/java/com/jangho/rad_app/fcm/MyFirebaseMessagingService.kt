package com.jangho.rad_app.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jangho.rad_app.MainActivity
import com.jangho.rad_app.R
import java.text.SimpleDateFormat
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    /* 메세지 수신 메서드 */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val type = remoteMessage.data["type"]?.let { NotificationType.valueOf(it) } ?: kotlin.run {
            NotificationType.NORMAL
        }
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        push(title.toString(),message.toString())

        val prefPush = getSharedPreferences("checkAppPush", 0)
        val pushCheck = prefPush.getString("check", "").toString()
        //푸시 알림 false일시
        if (pushCheck.equals("false")) {

        } else {//푸시 알림 check true일시
            sendNotification(type, title, message)
        }
    }

    /* 알림 생성 메서드 */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ) {
        //알림 생성
        NotificationManagerCompat.from(this)
        .notify((System.currentTimeMillis()/100).toInt(),
        createNotification(type, title, message))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notificationType", " ${type.title} 타입 ")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, (System.currentTimeMillis() / 100).toInt(), intent, PendingIntent.FLAG_IMMUTABLE) //알림이 여러개 표시되도록 requestCode 를 추가

        val channelId = "라드채널ID" //알림 채널 ID
        val channelName = "라드채널이름" //알림 채널 이름
        val importance = NotificationManager.IMPORTANCE_HIGH //알림 중요도 설정
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "라드앱 noti입니다." //알림 채널 설명
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.rad_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true) //클릭 시 자동으로 삭제되도록 설정
            .setContentIntent(pendingIntent) //알림 눌렀을 때 실행할 Intent 설정

        return notificationBuilder.build()
    }

    private fun push(title :String, message: String) {
        val time = System.currentTimeMillis()
        val korea = SimpleDateFormat("yyyyMMddHHmmss")
        korea.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val koreaTime = korea.format(Date(time))
        val databaseReference = FirebaseDatabase.getInstance("https://rad-project-cade4-default-rtdb.firebaseio.com/")
        val messageReference = databaseReference.getReference("push").push()
        messageReference.child("title").setValue(title)
        messageReference.child("message").setValue(message)
        messageReference.child("time").setValue(koreaTime)
    }
}


