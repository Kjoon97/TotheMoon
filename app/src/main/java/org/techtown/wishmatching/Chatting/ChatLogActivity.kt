package org.techtown.wishmatching.Chatting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.provider.PicassoProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.image_chat_from_row.view.*
import kotlinx.android.synthetic.main.image_chat_to_row.view.*
import org.techtown.wishmatching.Authentication
import org.techtown.wishmatching.MainActivity
import org.techtown.wishmatching.Mypage.DealSituation.MatchingMyPostInfo
import org.techtown.wishmatching.Mypage.DealSituation.MatchingPartnerPostInfo
import org.techtown.wishmatching.R
import org.techtown.wishmatching.RealtimeDB.ChatMessage
import org.techtown.wishmatching.RealtimeDB.ImageChatMessage
import org.techtown.wishmatching.RealtimeDB.User
import java.text.SimpleDateFormat
import java.util.*

//ㅇㅇㅇ
class ChatLogActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM=0  //request code
    var adapter = GroupAdapter<ViewHolder>()
    companion object {
        var toUser: User? = null
    }
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
//        ListenForImageMessages()

        send_button_chat_log.setOnClickListener {   // send버튼 눌렀을 때
            performSendMessage()
        }

        btn_my_wish.setOnClickListener {
            var intent = Intent(this,MatchingMyPostInfo::class.java)
            startActivity(intent)
//            val fromId = FirebaseAuth.getInstance().uid.toString() // 현재 사용자
//            val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")
//            var post_value = usersDb.child(toUser!!.uid).child("connections").child("match")
//            var post_value2 = usersDb.child(fromId!!).child("connections").child("match")
//            var matchPostId2 : Task<DataSnapshot> = post_value2.get()
//            var matchPostId : Task<DataSnapshot> = post_value.get()
//
//            var my_like_post:String = ""
//            var partner_like_post:String = ""
//
//            var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
//            firestore = FirebaseFirestore.getInstance()  //초기화
//            firestore!!.collection("Matching_Post")
//                .document("${fromId.toString()}"+"${toUser!!.uid.toString()}")
//                .get()
//                .addOnSuccessListener {
//                    my_like_post= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
////                    Toast.makeText(this,"$my_like_post",Toast.LENGTH_LONG).show()
//                    val intent = Intent(this, MyItemMoreInfoActivity::class.java)
//                    intent.putExtra("doc_id", my_like_post)
//                    startActivity(intent)
//                }


        }

        btn_partner_wish.setOnClickListener {
            var intent = Intent(this, MatchingPartnerPostInfo::class.java)
            startActivity(intent)
//            val fromId = FirebaseAuth.getInstance().uid.toString() // 현재 사용자
//            val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")
//            var post_value = usersDb.child(toUser!!.uid).child("connections").child("match")
//            var post_value2 = usersDb.child(fromId!!).child("connections").child("match")
//            var matchPostId2 : Task<DataSnapshot> = post_value2.get()
//            var matchPostId : Task<DataSnapshot> = post_value.get()
//
//            var my_like_post:String = ""
//            var partner_like_post:String = ""
//
//            var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
//            firestore = FirebaseFirestore.getInstance()  //초기화
//            firestore!!.collection("Matching_Post")
//                .document("${toUser!!.uid.toString()}"+"${fromId.toString()}")
//                .get()
//                .addOnSuccessListener {
//                    partner_like_post= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
////                    Toast.makeText(this,"$partner_like_post",Toast.LENGTH_LONG).show()
//                    val intent = Intent(this, MyItemMoreInfoActivity::class.java)
//                    intent.putExtra("doc_id", partner_like_post)
//                    startActivity(intent)
//                }

        }
        add_image_button_chat.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type ="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==PICK_IMAGE_FROM_ALBUM)
            if(resultCode == Activity.RESULT_OK){  //사진을 선택했을 때 이미지의 경로가 이쪽으로 넘어옴
                var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())//파일이름 입력해주는 코드 - 이름이 중복 설정되지않도록 파일명을 날짜로
                var imageFileName = "IMAGE_chat_"+timestamp+"_.png"
                var photoUri = data?.data
                val text = edittext_chat_log.text.toString()
                val fromId = FirebaseAuth.getInstance().uid
                val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                val toId = user?.uid

                var storage : FirebaseStorage? = null
                storage = FirebaseStorage.getInstance() //스토리지 초기화
                var storageRef =storage?.reference?.child("ImageChatMessage")?.child(imageFileName)

                if (fromId == null) return

                storageRef?.putFile(photoUri!!).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
                        var user_nickname :String = ""

                        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
                        var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
                        firestore = FirebaseFirestore.getInstance()  //초기화
                        firestore!!.collection("user")
                            .whereEqualTo("uid", Authentication.auth.currentUser!!.uid).limit(1)
                            .get()
                            .addOnSuccessListener { documents->
                                for(document in documents){
                                    user_nickname = document.data["nickname"].toString()

                                    val ImageChatMessage =
                                        toUser?.let {
                                            ImageChatMessage(reference.key!!, uri.toString(), fromId, toId!!, System.currentTimeMillis(),
                                                user_nickname,1)
                                        }
                                    reference.setValue(ImageChatMessage)
                                        .addOnSuccessListener {
                                            Log.d("ChatMessage", "이미지 채팅 메세지 저장:${reference.key}")
                                            edittext_chat_log.text.clear()
                                            recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
                                        }
                                    toReference.setValue(ImageChatMessage)
                                }
                            }

                    }
                }



//                val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
//                latestMessageFromRef.setValue(chatMessage)
//
//                val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
//                latestMessageToRef.setValue(chatMessage)
            }else{  //취소버튼 눌렀을 때 작동하는 부분
                finish()  //취소했을 때는 액티비티 그냥 취소
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_chattinglog,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.action_exit -> {
                var builder = AlertDialog.Builder(this)
                    builder.setTitle("채팅방 나가기")
                    builder.setMessage("채팅방을 나가면 대화 내역이 삭제됩니다. 나가겠습니까?")
                    builder.setPositiveButton("예") { dialog, which ->

                        val fromId= FirebaseAuth.getInstance().uid
                        val toId = toUser?.uid
                        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
                        val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")

                        ref.removeValue()
                        latestMessageFromRef.removeValue()

                        this.finish()
                        var intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton("취소",null)
                        .create()
                builder.show()


                true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }
//    private fun ListenForImageMessages() {
//        val fromId= FirebaseAuth.getInstance().uid
//        val toId = toUser?.uid
//        val ref = FirebaseDatabase.getInstance().getReference("/user-image-messages/$fromId/$toId")
////        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
////        val ref = FirebaseDatabase.getInstance().getReference("/messages")
//        ref.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val imageChatMessage = snapshot.getValue(ImageChatMessage::class.java)
//
//                if (imageChatMessage != null) {
//
//                    if (imageChatMessage.fromId == FirebaseAuth.getInstance().uid) {
//                        val currentUser = ChattingFragment.currentUser ?: return
//                        adapter.add(ImageChatFromItem(imageChatMessage.imageUrl,currentUser,imageChatMessage.timestamp,imageChatMessage.nickname)) // 채팅 내용 리사이클 뷰에 띄우기
//                        Log.d("ChatMessage", "보내는사람:${fromId}")
//
//                    } else {
//
//                        toUser?.let {
//                            ChatToItem.ImageChatToItem(
//                                imageChatMessage.imageUrl,
//                                it,
//                                imageChatMessage.timestamp,
//                                imageChatMessage.nickname
//                            )
//                        }?.let { adapter.add(it) }
//                        Log.d("ChatMessage", "받는 사람:${toId}")
//                    }
//                }
//                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

    private fun ListenForMessages() {
        val fromId= FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
//        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
//        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                    if(chatMessage?.flag == 1)
                    {
                        var imageChatMessage : ImageChatMessage? = p0.getValue(ImageChatMessage::class.java)
                        if (imageChatMessage?.fromId == FirebaseAuth.getInstance().uid) {
                            val currentUser = ChattingFragment.currentUser ?: return
                            if (imageChatMessage != null) {
                                adapter.add(ImageChatFromItem(imageChatMessage.imageUrl,currentUser,imageChatMessage.timestamp,imageChatMessage.nickname))
                            } // 채팅 내용 리사이클 뷰에 띄우기
                            Log.d("ChatMessage", "보내는사람:${fromId}")

                        } else {

                            if (imageChatMessage != null) {
                                toUser?.let {
                                    ChatToItem.ImageChatToItem(
                                        imageChatMessage.imageUrl,
                                        it,
                                        imageChatMessage.timestamp,
                                        imageChatMessage.nickname
                                    )
                                }?.let { adapter.add(it) }
                            }
                            Log.d("ChatMessage", "받는 사람:${toId}")
                        }
                    }
                    else {
                        if (chatMessage != null) {
                            if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                                val currentUser = ChattingFragment.currentUser ?: return
                                adapter.add(ChatFromItem(chatMessage.text,currentUser,chatMessage.timestamp,chatMessage.nickname)) // 채팅 내용 리사이클 뷰에 띄우기
                                Log.d("ChatMessage", "보내는사람:${fromId}")

                            } else {

                                toUser?.let { ChatToItem(chatMessage.text, it,chatMessage.timestamp,chatMessage.nickname) }?.let { adapter.add(it) }
                                Log.d("ChatMessage", "받는 사람:${toId}")
                            }
                        }

                    }


                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
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
//        val ref_image = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
////        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
////        val ref = FirebaseDatabase.getInstance().getReference("/messages")
//        ref_image.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val imageChatMessage = snapshot.getValue(ImageChatMessage::class.java)
//
//                if (imageChatMessage != null) {
//
//                    if (imageChatMessage.fromId == FirebaseAuth.getInstance().uid) {
//                        val currentUser = ChattingFragment.currentUser ?: return
//                        adapter.add(ImageChatFromItem(imageChatMessage.imageUrl,currentUser,imageChatMessage.timestamp,imageChatMessage.nickname)) // 채팅 내용 리사이클 뷰에 띄우기
//                        Log.d("ChatMessage", "보내는사람:${fromId}")
//
//                    } else {
//
//                        toUser?.let {
//                            ChatToItem.ImageChatToItem(
//                                imageChatMessage.imageUrl,
//                                it,
//                                imageChatMessage.timestamp,
//                                imageChatMessage.nickname
//                            )
//                        }?.let { adapter.add(it) }
//                        Log.d("ChatMessage", "받는 사람:${toId}")
//                    }
//                }
//                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }

    private fun performSendMessage() {  //보낸 메세지 파이어베이스 보내기
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        if (fromId == null) return
//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        var user_nickname :String = ""

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
        firestore = FirebaseFirestore.getInstance()  //초기화
        firestore!!.collection("user")
            .whereEqualTo("uid", Authentication.auth.currentUser!!.uid).limit(1)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    user_nickname = document.data["nickname"].toString()

                    val chatMessage =
                        toUser?.let {
                            ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis(),
                                user_nickname,0)
                        }
                    reference.setValue(chatMessage)
                        .addOnSuccessListener {
                            Log.d("ChatMessage", "채팅 메세지 저장:${reference.key}")
                            edittext_chat_log.text.clear()
                            recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
                        }
                    toReference.setValue(chatMessage)

                    val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                    latestMessageFromRef.setValue(chatMessage)

                    val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                    latestMessageToRef.setValue(chatMessage)
                }
            }



    }
}


class ChatFromItem(val text:String,val user: User,val time: Long,val nickname: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text =text  //채팅 입력->말풍선에 반영

        var time_hours = SimpleDateFormat("HH").format(time).toString()
        if(time_hours.toInt()>12 ){
            time_hours = (time_hours.toInt()-12).toString()
        }
        val time_minutes = SimpleDateFormat("mm").format(time).toString()
        val time_AP= SimpleDateFormat("aa").format(time).toString()
        val time_string = time_AP+" "+time_hours+":"+time_minutes.toString()
        var uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        PicassoProvider.get().load(uri).into(targetImageView)

        viewHolder.itemView.textview_from_chat_time.text = time_string.toString()
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
class ImageChatFromItem(val imageUrl:String,val user:User,val time: Long,val nickname: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        if(imageUrl != null && imageUrl != "") {
            PicassoProvider.get().load(imageUrl).into(viewHolder.itemView.imageview_from_row)
        }
        var time_hours = SimpleDateFormat("HH").format(time).toString()
        if(time_hours.toInt()>12 ){
            time_hours = (time_hours.toInt()-12).toString()
        }
        val time_minutes = SimpleDateFormat("mm").format(time).toString()
        val time_AP= SimpleDateFormat("aa").format(time).toString()
        val time_string = time_AP+" "+time_hours+":"+time_minutes.toString()
        var uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_imagechat_from_row
        PicassoProvider.get().load(uri).into(targetImageView)

        viewHolder.itemView.textview_from_imagechat_time.text = time_string.toString()

    }

    override fun getLayout(): Int {
        return R.layout.image_chat_from_row
    }

}

class ChatToItem(val text:String, val user:User,val time: Long,val nickname:String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text   // 채팅 입력->말풍선에 반영

        var time_hours = SimpleDateFormat("HH").format(time).toString()
        if(time_hours.toInt()>12 ){
            time_hours = (time_hours.toInt()-12).toString()
        }
        val time_minutes = SimpleDateFormat("mm").format(time).toString()
        val time_AP= SimpleDateFormat("aa").format(time).toString()
        val time_string = time_AP+" "+time_hours+":"+time_minutes
        var uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        PicassoProvider.get().load(uri).into(targetImageView)

        viewHolder.itemView.textview_to_chat_time.text = time_string.toString()
        viewHolder.itemView.textview_to_chat_nickname.text = nickname.toString()
//            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(time).toString()
    }
    class ImageChatToItem(val imageUrl: String,val user:User,val time: Long,val nickname: String): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            PicassoProvider.get().load(imageUrl).into(viewHolder.itemView.imageview_to_row)

            var time_hours = SimpleDateFormat("HH").format(time).toString()
            if(time_hours.toInt()>12 ){
                time_hours = (time_hours.toInt()-12).toString()
            }
            val time_minutes = SimpleDateFormat("mm").format(time).toString()
            val time_AP= SimpleDateFormat("aa").format(time).toString()
            val time_string = time_AP+" "+time_hours+":"+time_minutes.toString()
            var uri = user.profileImageUrl
            val targetImageView = viewHolder.itemView.imageview_imagechat_to_row
            PicassoProvider.get().load(uri).into(targetImageView)

            viewHolder.itemView.textview_to_imagechat_time.text = time_string.toString()
            viewHolder.itemView.textview_to_imagechat_nickname.text = nickname.toString()
        }

        override fun getLayout(): Int {
            return R.layout.image_chat_to_row
        }

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }


}