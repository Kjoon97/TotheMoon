package org.techtown.wishmatching

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.provider.PicassoProvider
import kotlinx.android.synthetic.main.activity_more_info.*
import org.techtown.wishmatching.Database.MatchPostId
import org.techtown.wishmatching.MainActivity.Companion.prefs
import org.techtown.wishmatching.RealtimeDB.ChatMessage

class MoreInfoActivity : AppCompatActivity() {
    var firestore : FirebaseFirestore? = null
    var storage : FirebaseStorage? = null
    var imageList : ArrayList<MoreInfoImageList> = arrayListOf()

    var user_nickname : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")
        val currentUser = Authentication.auth.currentUser!!.uid


        val intent = intent
        val goodsId = intent.getStringExtra("doc_id")      //물품 아이디를 인텐트를 통해 받아옴
        val post_id = intent.getStringExtra("post_id")



        val currentUserConnectionDb = usersDb.child(currentUser!!).child("connections").child("match").child(post_id!!)

        if(prefs.getString("$goodsId",0) == 1){
            btn_moreInfo_like.setImageResource(R.drawable.btn_clicked_heart)
        } else{
            btn_moreInfo_like.setImageResource(R.drawable.btn_heart)
        }


        firestore!!.collection("post")  //물품 아이디를 바탕으로 post쿼리 조회
            .whereEqualTo("documentId", goodsId)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    tv_moreInfo_category.text = document.data["category"].toString()    //카테고리
                    tv_moreInfo_name.text = document.data["title"].toString()           //물품 이름
                    tv_moreInfo_description.text = document.data["content"].toString()  //물품 설명
                    tv_moreInfo_dateValue.text = document.data["date"].toString()
                    if(document.data["imageUrl"].toString() == "null"  ){
                        img_moreInfo_img1.visibility= View.INVISIBLE
                        img_moreInfo_img2.visibility= View.INVISIBLE
                        img_moreInfo_img3.visibility= View.INVISIBLE
                        img_moreInfo_img4.visibility= View.INVISIBLE
                        img_moreInfo_img5.visibility= View.INVISIBLE


                    }
                    else if(document.data["imageUrl2"].toString() == "null"  ){
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_moreInfo_img1)     //물품 이미지
                        img_moreInfo_img2.visibility= View.INVISIBLE
                        img_moreInfo_img3.visibility= View.INVISIBLE
                        img_moreInfo_img4.visibility= View.INVISIBLE
                        img_moreInfo_img5.visibility= View.INVISIBLE

                    }
                    else if(document.data["imageUrl3"].toString() == "null"  ){
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_moreInfo_img1)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl2"].toString())
                            .into(img_moreInfo_img2)     //물품 이미지
                        img_moreInfo_img3.visibility= View.INVISIBLE
                        img_moreInfo_img4.visibility= View.INVISIBLE
                        img_moreInfo_img5.visibility= View.INVISIBLE
                    }
                    else if(document.data["imageUrl4"].toString() == "null"  ){
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_moreInfo_img1)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl2"].toString())
                            .into(img_moreInfo_img2)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl3"].toString())
                            .into(img_moreInfo_img3)     //물품 이미지
                        img_moreInfo_img4.visibility= View.INVISIBLE
                        img_moreInfo_img5.visibility= View.INVISIBLE
                    }
                    else if(document.data["imageUrl5"].toString() == "null"  ){
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_moreInfo_img1)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl2"].toString())
                            .into(img_moreInfo_img2)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl3"].toString())
                            .into(img_moreInfo_img3)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl4"].toString())
                            .into(img_moreInfo_img4)     //물품 이미지
                        img_moreInfo_img5.visibility= View.INVISIBLE
                    }
                    else {
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_moreInfo_img1)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl2"].toString())
                            .into(img_moreInfo_img2)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl3"].toString())
                            .into(img_moreInfo_img3)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl4"].toString())
                            .into(img_moreInfo_img4)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl5"].toString())
                            .into(img_moreInfo_img5)     //물품 이미지
                    }
                    firestore!!.collection("user")  //동네 정보를 받아와야하기 때문에 user db 쿼리 조회(post db의 uid를 바탕으로)
                        .whereEqualTo("uid", document.data["uid"].toString())
                        .get()
                        .addOnSuccessListener { documents->
                            for(document in documents){
                                tv_moreInfo_areaValue.text = document.data["area"].toString()   //동네네
                           }
                        }
                }
            }

            firestore!!.collection("user")
                .whereEqualTo("uid", currentUser!!).limit(1)
                .get()
                .addOnSuccessListener { documents->
                    for(document in documents){
                        user_nickname = document.data["nickname"].toString()
                    }
                }

        btn_moreInfo_left.setOnClickListener {
            vlf_moreInfo_imglist.showPrevious()
        }
        btn_moreInfo_right.setOnClickListener {
            vlf_moreInfo_imglist.showNext()
        }

        //만약 좋아요를 이미 누른 상태이면 좋아요 색이 빨간색이 되어야함
        btn_moreInfo_like.setOnClickListener {
            //좋아요 버튼 이벤트 처리
            val fromId = FirebaseAuth.getInstance().uid // 현재 사용자

            if(prefs.getString("$goodsId",0) == 0 ){
                btn_moreInfo_like.setImageResource(R.drawable.btn_clicked_heart)
                prefs.setString("$goodsId",1)

                if (currentUser != null) {
                    usersDb.child(post_id).child("connections").child("match").child(currentUser).setValue(true)
                    firestore?.collection("Matching_Post")?.document("$fromId"+"$post_id") // 내가 좋아요누른 게시물 데이터
                        ?.set(
                            MatchPostId("$goodsId")
                        )
                }

                currentUserConnectionDb.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            val reference = FirebaseDatabase.getInstance()
                                .getReference("/user-messages/$currentUser/$post_id").push()
                            val toReference = FirebaseDatabase.getInstance()
                                .getReference("/user-messages/$post_id/$currentUser").push()
                            val chatMessage =
                                ChatMessage(
                                    reference.key!!,
                                    "매칭이 성사되었습니다.",
                                    currentUser.toString(),
                                    post_id,
                                    System.currentTimeMillis(),
                                    user_nickname!!,
                                    0

                                )
                            reference.setValue(chatMessage)
                            toReference.setValue(chatMessage)

                            val latestMessageFromRef = FirebaseDatabase.getInstance()
                                .getReference("/latest-messages/$currentUser/$post_id")
                            latestMessageFromRef.setValue(chatMessage)

                            val latestMessageToRef = FirebaseDatabase.getInstance()
                                .getReference("/latest-messages/$post_id/$currentUser")
                            latestMessageToRef.setValue(chatMessage)

                            val channel_name = "match_channel"
                            val channelId = "MATCH_ID"
                            val channel_description = "test"
                            val notificationBuilder = NotificationCompat.Builder(this@MoreInfoActivity, channelId)
                                .setSmallIcon(R.drawable.logo_wm) // 아이콘 설정
                                .setContentTitle("매칭이 성사되었습니다.") // 제목
                                .setContentText("채팅방이 생성되었습니다.") // 메시지 내용
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                val importance = NotificationManager.IMPORTANCE_DEFAULT
                                val channel = NotificationChannel(channelId, channel_name, importance).apply {
                                    description = channel_description
                                }
                                // Register the channel with the system
                                val notificationManager: NotificationManager =
                                    this@MoreInfoActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                notificationManager.createNotificationChannel(channel)
                            }

                            with(NotificationManagerCompat.from(this@MoreInfoActivity)) {
                                // notificationId is a unique int for each notification that you must define
                                notify(8154, notificationBuilder.build())
                            }



                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }


                })


            } else{
                prefs.setString("$goodsId",0)
                btn_moreInfo_like.setImageResource(R.drawable.btn_heart)
                usersDb.child(post_id).child("connections").child("match").child(currentUser).removeValue()
            }




        }
    }
}