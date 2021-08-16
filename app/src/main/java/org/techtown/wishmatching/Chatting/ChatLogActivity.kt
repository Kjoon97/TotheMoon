package org.techtown.wishmatching.Chatting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.provider.PicassoProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import org.techtown.wishmatching.R
import org.techtown.wishmatching.RealtimeDB.ChatMessage
import org.techtown.wishmatching.RealtimeDB.User

class ChatLogActivity : AppCompatActivity() {

    var adapter = GroupAdapter<ViewHolder>()
    var toUser : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        //        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        if (toUser != null) {
            supportActionBar?.title = toUser?.username
        }
        //setupDummyData()

        ListenForMessages()

        send_button_chat_log.setOnClickListener {   // send버튼 눌렀을 때
            performSendMessage()
        }
    }

    private fun ListenForMessages() {
        val fromId= FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
//        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
//        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = ChattingFragment.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser)) // 채팅 내용 리사이클 뷰에 띄우기
                        Log.d("ChatMessage", "보내는사람:${fromId}")

                    } else {

                        toUser?.let { ChatToItem(chatMessage.text, it) }?.let { adapter.add(it) }
                        Log.d("ChatMessage", "받는 사람:${toId}")


//                        adapter.add(ChatToItem(chatMessage.text,toUser!!)) // 본문 강의 코드
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {  //보낸 메세지 파이어베이스 보내기
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid

//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()


        if (fromId == null) return

        val chatMessage =
            ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatMessage", "채팅 메세지 저장:${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)
    }
}


class ChatFromItem(val text:String,val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text =text  //채팅 입력->말풍선에 반영

        var uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        PicassoProvider.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text:String, val user:User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text   // 채팅 입력->말풍선에 반영

        var uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        PicassoProvider.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}