package org.techtown.wishmatching

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseService"

    // FirebaseInstanceIdService는 이제 사라짐. 이제 이걸 사용함
    override fun onNewToken(p0: String) {
        Log.d(TAG, "new Token: $p0")

        // 토큰 값을 따로 저장해둔다.
        val pref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("token", p0).apply()
        editor.commit()

        Log.i("로그: ", "성공적으로 토큰을 저장함")
    }

    public override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived (remoteMessage)
        Log.d(TAG, "From: " + remoteMessage!!.from)

        // Notification 메시지를 수신할 경우는
        // remoteMessage.notification?.body!! 여기에 내용이 저장되어있다.
        // Log.d(TAG, "Notification Message Body: " + remoteMessage.notification?.body!!)
        var p1 = remoteMessage.notification?.body!!

        if(remoteMessage!=null){
            var value = remoteMessage.notification
            var title = value?.title.toString()
            var body = value?.body.toString()
            Log.i("바디: ", title)
            Log.i("타이틀: ", body)
            sendNotification(title,body)
        }

        else {
            Log.i("수신에러: ", "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
            Log.i("data값: ", remoteMessage.data.toString())
        }
    }


    public fun sendNotification(title: String, body: String) {
        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        // 일회용 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임한다.
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Activity Stack 을 경로만 남긴다. A-B-C-D-B => A-B
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT)

        // 알림 채널 이름
        val channelId = getString(R.string.firebase_notification_channel_id)

        // 알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림에 대한 UI 정보와 작업을 지정한다.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // 아이콘 설정
            .setContentTitle(title) // 제목
            .setContentText(body) // 메시지 내용
            .setSound(soundUri) // 알림 소리
            .setContentIntent(pendingIntent) // 알림 실행 시 Intent

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요하다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())
    }

//    fun showDataMessage(msgTitle: String?, msgContent: String?) {
//        Log.i("### data msgTitle : ", msgTitle!!)
//        Log.i("### data msgContent : ", msgContent!!)
//        val toastText = String.format("[Data 메시지] title: %s => content: %s", msgTitle, msgContent)
//        Looper.prepare()
//        Toast.makeText(applicationContext, toastText, Toast.LENGTH_LONG).show()
//        Looper.loop()
//    }
//
//    /**
//     * 수신받은 메시지를 Toast로 보여줌
//     * @param msgTitle
//     * @param msgContent
//     */
//    fun showNotificationMessage(msgTitle: String?, msgContent: String?) {
//        Log.i("### noti msgTitle : ", msgTitle!!)
//        Log.i("### noti msgContent : ", msgContent!!)
//        val toastText =
//            String.format("[Notification 메시지] title: %s => content: %s", msgTitle, msgContent)
//        Looper.prepare()
//        Toast.makeText(applicationContext, toastText, Toast.LENGTH_LONG).show()
//        Looper.loop()
//    }
//
//    /**
//     * 메시지 수신받는 메소드
//     * @param msg
//     */
//    override fun onMessageReceived(msg: RemoteMessage) {
//        Log.i("### msg : ", msg.toString())
//        if (msg.data.isEmpty()) {
//            showNotificationMessage(
//                msg.notification!!.title,
//                msg.notification!!.body
//            ) // Notification으로 받을 때
//        } else {
//            showDataMessage(msg.data["title"], msg.data["content"]) // Data로 받을 때
//        }
//    }

}