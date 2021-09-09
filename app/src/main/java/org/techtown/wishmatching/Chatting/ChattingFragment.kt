package org.techtown.wishmatching.Chatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.provider.PicassoProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chatting.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import org.techtown.wishmatching.ListAdapter
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
        var partner_key : String? = null
    }
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var context = container?.context
        setHasOptionsMenu(true)
//        recyclerview_latest_message.adapter = adapter
        fetchCurrentUser()
        listenForLatestMessages()
        val v: View = inflater.inflate(R.layout.fragment_chatting, container, false)
        setHasOptionsMenu(true)

        return v


    }
    class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){   //최신 채팅 글 창
        var chatPartnerUser : User? = null
        override fun bind(viewHolder: ViewHolder, position: Int){
            viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

            val chatPartnerId: String
            if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                chatPartnerId = chatMessage.toId
            } else {
                chatPartnerId = chatMessage.fromId
            }
            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue(User::class.java)
                    viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username

                    val targetImageView = viewHolder.itemView.imageview_latest_message
                    PicassoProvider.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }
    }
    val latestMessagesMap = HashMap<String, ChatMessage>()

    public fun refreshRecyclerViewMessages(){
        adapter.clear()        // 원래 뜨던 메세지 클리어
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        val to_ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) { // 채팅 방 생성
                var partner_id = snapshot.getValue<ListAdapter.MatchInfo>()?.toId
                
                Log.d("tttt","${snapshot.childrenCount}")

                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage  //key는 메세지 키를 의미함
                refreshRecyclerViewMessages() //


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
                refreshRecyclerViewMessages()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("1123","cancelled")
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
        recyclerview_latest_message.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(activity,ChatLogActivity::class.java)

            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY,row.chatPartnerUser)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_chattingfragment,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
//            R.id.action_clear -> {
//
//                adapter.clear()
//
//
//                true
//            }


            else -> super.onOptionsItemSelected(item)
        }

    }
}