package org.techtown.wishmatching

import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    fun showDataMessage(msgTitle: String?, msgContent: String?) {
        Log.i("### data msgTitle : ", msgTitle!!)
        Log.i("### data msgContent : ", msgContent!!)
        val toastText = String.format("[Data 메시지] title: %s => content: %s", msgTitle, msgContent)
        Looper.prepare()
        Toast.makeText(applicationContext, toastText, Toast.LENGTH_LONG).show()
        Looper.loop()
    }

    /**
     * 수신받은 메시지를 Toast로 보여줌
     * @param msgTitle
     * @param msgContent
     */
    fun showNotificationMessage(msgTitle: String?, msgContent: String?) {
        Log.i("### noti msgTitle : ", msgTitle!!)
        Log.i("### noti msgContent : ", msgContent!!)
        val toastText =
            String.format("[Notification 메시지] title: %s => content: %s", msgTitle, msgContent)
        Looper.prepare()
        Toast.makeText(applicationContext, toastText, Toast.LENGTH_LONG).show()
        Looper.loop()
    }

    /**
     * 메시지 수신받는 메소드
     * @param msg
     */
    override fun onMessageReceived(msg: RemoteMessage) {
        Log.i("### msg : ", msg.toString())
        if (msg.data.isEmpty()) {
            showNotificationMessage(
                msg.notification!!.title,
                msg.notification!!.body
            ) // Notification으로 받을 때
        } else {
            showDataMessage(msg.data["title"], msg.data["content"]) // Data로 받을 때
        }
    }
}