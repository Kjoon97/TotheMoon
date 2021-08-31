package org.techtown.wishmatching

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import org.techtown.wishmatching.LoginActivity.Companion.prefs
import org.techtown.wishmatching.RealtimeDB.ChatMessage


class ListAdapter (private var list: ArrayList<PostDTO>): RecyclerView.Adapter<ListAdapter.ListItemViewHolder>() {
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록

    // inner class로 ViewHolder 정의
    inner class ListItemViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {


        var photourl: ImageView = itemView!!.findViewById(R.id.item_photo)
        var btn_like: ImageView = itemView!!.findViewById(R.id.btn_like)
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

        var btn_like_state : Int
        holder.btn_like.setOnClickListener {
            if(holder.state_like==0)
            {
                holder.btn_like.setImageResource(R.drawable.btn_clicked_heart)
                holder.state_like=1
                var firestore = FirebaseFirestore.getInstance()  //초기화


                if (fromId != null) {
//                    usersDb.child(post_uid).child("connections").child("match").child(fromId).child("postId").setValue(doc_id)
                    usersDb.child(post_uid).child("connections").child("match").child(fromId).setValue(true)
                    firestore?.collection("Matching_Post")?.document("$fromId"+"$post_uid") // 내가 좋아요누른 게시물 데이터
                        ?.set(
                            MatchPostId("$doc_id")
                        )
                }

                currentUserConnectionDb.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {

                            val reference = FirebaseDatabase.getInstance()
                                    .getReference("/user-messages/$fromId/$post_uid").push()
                                val toReference = FirebaseDatabase.getInstance()
                                    .getReference("/user-messages/$post_uid/$fromId").push()
                                val chatMessage =
                                    ChatMessage(
                                        reference.key!!,
                                        "채팅방이 생성 되었습니다.",
                                        fromId.toString(),
                                        post_uid,
                                        System.currentTimeMillis(),
                                        user_nickname

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
                            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                                .setSmallIcon(R.mipmap.ic_launcher) // 아이콘 설정
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
//                val toId :String? = null
//                firestore!!.collection("post").get().addOnSuccessListener {
//                }
//                toId = firestore.document().d
//                val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
//                val toId = user?.uid
//                if(match_count == 0) {
//                    match_count=1
//                    val my_matchinfo = MatchInfo(fromId.toString(),post_uid.toString(),match_count)
//                    reference.setValue(my_matchinfo)
//                    val your_matchinfo = MatchInfo(fromId.toString(),post_uid.toString(),match_count)
//                    toReference.setValue(your_matchinfo)
//                }
//                else if(match_count == 1){
//                    match_count=2
//                    val my_matchinfo = MatchInfo(fromId.toString(),post_uid.toString(),match_count)
//                    reference.setValue(my_matchinfo)
//                    val your_matchinfo = MatchInfo(fromId.toString(),post_uid.toString(),match_count)
//                    toReference.setValue(your_matchinfo)
//                }


//                ttt.text = match_count.toString()
//                val my_matchinfo = MatchInfo(fromId.toString(),post_uid.toString(),0)
//                reference.setValue(my_matchinfo)
//                val your_matchinfo = MatchInfo(fromId.toString(),post_uid.toString(),0)
//                toReference.setValue(your_matchinfo)



//                                val reference = FirebaseDatabase.getInstance()
//                                    .getReference("/user-messages/$fromId/$post_uid").push()
//                                val toReference = FirebaseDatabase.getInstance()
//                                    .getReference("/user-messages/$post_uid/$fromId").push()
//                                val chatMessage =
//                                    ChatMessage(
//                                        reference.key!!,
//                                        "채팅방이 생성 되었습니다.",
//                                        fromId.toString(),
//                                        post_uid,
//                                        System.currentTimeMillis() / 1000
//                                    )
//                                reference.setValue(chatMessage)
//
//                                val latestMessageFromRef = FirebaseDatabase.getInstance()
//                                    .getReference("/latest-messages/$fromId/$post_uid")
//                                latestMessageFromRef.setValue(chatMessage)
//
//                                val latestMessageToRef = FirebaseDatabase.getInstance()
//                                    .getReference("/latest-messages/$post_uid/$fromId")
//                                latestMessageToRef.setValue(chatMessage)

                prefs.setString("$position","1")

            }
            else
            {
                holder.btn_like.setImageResource(R.drawable.btn_heart)
                holder.state_like=0

                usersDb.child(post_uid).child("connections").child("match").child(fromId).removeValue()
            }

        }

        holder.photourl.setOnClickListener {
            val intent = Intent(it.context, MoreInfoActivity::class.java)
            intent.putExtra("doc_id", doc_id)
            intent.putExtra("state", holder.state_like)
            intent.putExtra("post_id", post_uid)
            it.context.startActivity(intent)
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