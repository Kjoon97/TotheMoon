package org.techtown.wishmatching

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.techtown.wishmatching.Database.MatchPostId
import org.techtown.wishmatching.Database.PostDTO
import org.techtown.wishmatching.MainActivity.Companion.prefs
import org.techtown.wishmatching.Mypage.DealSituation.MyItemMoreInfoActivity
import org.techtown.wishmatching.RealtimeDB.ChatMessage


class ListAdapter (private var list: ArrayList<PostDTO>): RecyclerView.Adapter<ListAdapter.ListItemViewHolder>() {
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록

    // inner class로 ViewHolder 정의
    inner class ListItemViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {


        var photourl: ImageView = itemView!!.findViewById(R.id.item_photo)
        var btn_like: ImageView = itemView!!.findViewById(R.id.btn_like)
        var card : CardView = itemView!!.findViewById(R.id.item_card)
        var state_like: Int = 0


        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: ArrayList<PostDTO>, position: Int) {
//            photourl.setImageURI(data.)
//            photourl.setImageResource(data.size)

        }
    }

    // ViewHolder에게 item을 보여줄 View로 쓰일 item_data_list.xml를 넘기면서 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return ListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        val database = Firebase.database
        var dataSnapshot: DataSnapshot? = null
        if (dataSnapshot != null) {
            return dataSnapshot.childrenCount as Int
        }
        else
            return list.size
    }

    // ViewHolder의 bind 메소드를 호출한다.
    override fun onBindViewHolder(holder: ListAdapter.ListItemViewHolder, position: Int) {
        var doc_id : String = list.get(position).documentId     //물품 Id
        var content : String = list.get(position).content.toString()
        var title : String = list.get(position).title.toString()
        var imageUrl : String = list.get(position).imageUrl.toString()
        var post_uid : String = list.get(position).uid.toString() // 게시글 올린 사람
        var category : String = list.get(position).category.toString()
        val fromId = FirebaseAuth.getInstance().uid // 현재 사용자
        var context : Context = holder.itemView.context
        var user_nickname = ""
        firestore = FirebaseFirestore.getInstance()  //초기화
        firestore!!.collection("user")
            .whereEqualTo("uid", Authentication.auth.currentUser!!.uid).limit(1)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    user_nickname = document.data["nickname"].toString()


                }
            }

        val reference = FirebaseDatabase.getInstance().getReference("/matching-users/$fromId/$post_uid").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/matching-users/$post_uid/$fromId").push()
        val reference_read = FirebaseDatabase.getInstance().getReference("/matching-users/$fromId/$post_uid")
        val toReference_read = FirebaseDatabase.getInstance().getReference("/matching-users/$post_uid/$fromId")
        val latest_ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$post_uid") // 메시지 기능을 위한 레퍼런스
        val latest_to_ref = FirebaseDatabase.getInstance().getReference("/user-messages/$post_uid/$fromId") // 메시지 기능을 위한 레퍼런스

        val reference2 = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$post_uid").push()

        val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")
        val currentUserConnectionDb = usersDb.child(fromId!!).child("connections").child("match").child(post_uid)

        var database: DatabaseReference
        database = Firebase.database.reference

//        if(prefs.getString("$position","0").toInt()==1) {
//            holder.btn_like.setImageResource(R.drawable.btn_clicked_heart)
//            holder.state_like=1
//        }
//        else {
//            holder.btn_like.setImageResource(R.drawable.btn_heart)
//            holder.state_like=0
//        }
//        if(post_uid.toString() == fromId.toString()){
//            holder.btn_like.visibility =View.INVISIBLE
//        }
        if(prefs.getString("$doc_id",0)==1) {
            holder.state_like=1
            holder.btn_like.setImageResource(R.drawable.btn_clicked_heart)
        }
        else {
            holder.state_like=0
            holder.btn_like.setImageResource(R.drawable.btn_heart)
        }




        var btn_like_state : Int
        // 좋아요 버튼 클릭시 작동
        holder.btn_like.setOnClickListener {
            if(post_uid.toString() == fromId.toString()) {
                Toast.makeText(holder.itemView.context,"자신의 게시글엔 좋아요를 할 수 없습니다.",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else {
                if (holder.state_like == 0) {
                    prefs.setString("$doc_id", 1)
                    holder.btn_like.setImageResource(R.drawable.btn_clicked_heart)
                    holder.state_like = 1
                    var firestore = FirebaseFirestore.getInstance()  //초기화


                    if (fromId != null) {
//                    usersDb.child(post_uid).child("connections").child("match").child(fromId).child("postId").setValue(doc_id)
                        usersDb.child(post_uid).child("connections").child("match").child(fromId)
                            .setValue(true)
                        firestore?.collection("Matching_Post")
                            ?.document("$fromId" + "$post_uid") // 내가 좋아요누른 게시물 데이터
                            ?.set(
                                MatchPostId("$fromId")
                            )
                        firestore?.collection("Matching_Post_id")
                            ?.document("$fromId" + "$post_uid" + "$doc_id") // 내가 좋아요누른 게시물 데이터
                            ?.set(
                                MatchPostId("$fromId")
                            )
                    }

                    currentUserConnectionDb.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {

                                val reference = FirebaseDatabase.getInstance()
                                    .getReference("/user-messages/$fromId/$post_uid").push()
                                val toReference = FirebaseDatabase.getInstance()
                                    .getReference("/user-messages/$post_uid/$fromId").push()
                                val chatMessage =
                                    ChatMessage(
                                        reference.key!!,
                                        "매칭이 성사되었습니다.",
                                        fromId.toString(),
                                        post_uid,
                                        System.currentTimeMillis(),
                                        user_nickname,
                                        0

                                    )
                                reference.setValue(chatMessage)
                                toReference.setValue(chatMessage)

                                val latestMessageFromRef = FirebaseDatabase.getInstance()
                                    .getReference("/latest-messages/$fromId/$post_uid")
                                latestMessageFromRef.setValue(chatMessage)

                                val latestMessageToRef = FirebaseDatabase.getInstance()
                                    .getReference("/latest-messages/$post_uid/$fromId")
                                latestMessageToRef.setValue(chatMessage)

                                val channel_name = "match_channel"
                                val channelId = "MATCH_ID"
                                val channel_description = "test"

                                val notificationBuilder =
                                    NotificationCompat.Builder(context, channelId)
                                        .setSmallIcon(R.drawable.logo_wm) // 아이콘 설정
                                        .setContentTitle("매칭이 성사되었습니다.") // 제목
                                        .setContentText("채팅방이 생성되었습니다.") // 메시지 내용
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setAutoCancel(true)

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                                    val channel = NotificationChannel(
                                        channelId,
                                        channel_name,
                                        importance
                                    ).apply {
                                        description = channel_description
                                    }
                                    // Register the channel with the system
                                    val notificationManager: NotificationManager =
                                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                    notificationManager.createNotificationChannel(channel)
                                }

                                with(NotificationManagerCompat.from(context)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(8154, notificationBuilder.build())
                                }


                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }


                    })
                } else {
                    prefs.setString("$doc_id", 0)
                    holder.btn_like.setImageResource(R.drawable.btn_heart)
                    holder.state_like = 0

                    usersDb.child(post_uid).child("connections").child("match").child(fromId)
                        .removeValue()
                }
                // 좋아요 버튼 취소시 작동
            }
        }

        holder.card.setOnClickListener {
            if(post_uid.toString() == fromId.toString()){
                val intent = Intent(it.context, MyItemMoreInfoActivity::class.java)
                intent.putExtra("doc_id", doc_id)
                intent.putExtra("state","doing")
                intent.putExtra("post_id", post_uid)
                it.context.startActivity(intent)
            }
            else{
                val intent = Intent(it.context, MoreInfoActivity::class.java)
                intent.putExtra("doc_id", doc_id)
                intent.putExtra("post_id", post_uid)
                it.context.startActivity(intent)
            }
        }



        Log.d("ListAdapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        Glide.with(holder.itemView)
            .load(list.get(position).imageUrl)
            .into(holder.photourl)

    }
    class MatchInfo(val fromId: String , val toId: String , val matching: Int) {
        constructor(): this("","",1)

    }





}