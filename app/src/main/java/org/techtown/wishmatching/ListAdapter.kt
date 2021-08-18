package org.techtown.wishmatching

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.list_item.view.*
import org.techtown.wishmatching.Database.PostDTO
import org.techtown.wishmatching.RealtimeDB.ChatMessage


class ListAdapter (private var list: ArrayList<PostDTO>): RecyclerView.Adapter<ListAdapter.ListItemViewHolder>() {


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
        var doc_id : String = list.get(position).documentId
        var content : String = list.get(position).content.toString()
        var title : String = list.get(position).title.toString()
        var imageUrl : String = list.get(position).imageUrl.toString()
        var post_uid : String = list.get(position).uid.toString() // 게시글 올린 사람
        var category : String = list.get(position).category.toString()
        val fromId = FirebaseAuth.getInstance().uid // 현재 사용자

        var match_count = 0

        val reference = FirebaseDatabase.getInstance().getReference("/matching-users/$fromId/$post_uid").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/matching-users/$post_uid/$fromId").push()
        val reference_read = FirebaseDatabase.getInstance().getReference("/matching-users/$fromId/$post_uid")
        val toReference_read = FirebaseDatabase.getInstance().getReference("/matching-users/$post_uid/$fromId")
        val latest_ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$post_uid") // 메시지 기능을 위한 레퍼런스
        val latest_to_ref = FirebaseDatabase.getInstance().getReference("/user-messages/$post_uid/$fromId") // 메시지 기능을 위한 레퍼런스

        val reference2 = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$post_uid").push()


        var database: DatabaseReference
        database = Firebase.database.reference


        holder.btn_like.setOnClickListener {
            if(holder.state_like==0)
            {
                holder.btn_like.setImageResource(R.drawable.btn_clicked_heart)
                holder.state_like=1
                var firestore = FirebaseFirestore.getInstance()  //초기화
                var ttt = holder.itemView.ttt

                val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")
                val currentUserConnectionDb = usersDb.child(fromId!!).child("connections").child("yeps").child(post_uid)

                if (fromId != null) {
                    usersDb.child(post_uid).child("connections").child("yeps").child(fromId).setValue(true)
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
                                        System.currentTimeMillis() / 1000
                                    )
                                reference.setValue(chatMessage)
                                toReference.setValue(chatMessage)

                                val latestMessageFromRef = FirebaseDatabase.getInstance()
                                    .getReference("/latest-messages/$fromId/$post_uid")
                                latestMessageFromRef.setValue(chatMessage)

                                val latestMessageToRef = FirebaseDatabase.getInstance()
                                    .getReference("/latest-messages/$post_uid/$fromId")
                                latestMessageToRef.setValue(chatMessage)
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



            }
            else
            {
                holder.btn_like.setImageResource(R.drawable.btn_heart)
                holder.state_like=0

                val matchinfo = MatchInfo(fromId.toString(),post_uid.toString(),1)
                reference.removeValue()
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