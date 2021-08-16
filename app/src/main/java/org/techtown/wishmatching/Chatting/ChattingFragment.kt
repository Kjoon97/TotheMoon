package org.techtown.wishmatching.Chatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chatting.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import org.techtown.wishmatching.R
import org.techtown.wishmatching.RealtimeDB.ChatMessage
import org.techtown.wishmatching.RealtimeDB.User


class ChattingFragment : Fragment() {
    val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    companion object{
        var currentUser : User? = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
//        recyclerview_latest_message.adapter = adapter
        fetchCurrentUser()
        listenForLatestMessages()
        val v: View = inflater.inflate(R.layout.fragment_chatting, container, false)

        return v


    }
    class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){   //최신 채팅 글 창
        override fun bind(viewHolder: ViewHolder, position: Int){
            viewHolder.itemView.message_textview_latest_message.text = chatMessage.text
        }
        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }
    }
    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()        // 원래 뜨던 메세지 클리어
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) { // 채팅 방 생성
                val chatMessage = snapshot.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[snapshot.key!!] = chatMessage  //key는 메세지 키를 의미함
                refreshRecyclerViewMessages() // 로드
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { //채팅 변경시 바로 반영
                val chatMessage = snapshot.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

private fun fetchCurrentUser() {
    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            currentUser = snapshot.getValue(User::class.java)
        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview_latest_message.adapter = adapter
    }
}