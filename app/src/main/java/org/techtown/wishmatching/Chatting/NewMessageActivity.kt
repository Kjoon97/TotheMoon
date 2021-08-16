package org.techtown.wishmatching.Chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.provider.PicassoProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import org.techtown.wishmatching.R
import org.techtown.wishmatching.RealtimeDB.User

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"
        fetchUsers()
    }
    companion object{
        val USER_KEY = "USER_KEY"
    }
    private fun fetchUsers(){  //파베로부터 유저 데이터 가져옴
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    Log.d("NewMessage",it.toString())
                    //모든 DataSnapshot가져옴 { key = 44PwKUyZFSYPfSpZkrvHe7603Hn2, value = {uid=44PwKUyZFSYPfSpZkrvHe7603Hn2, profileImageUrl=https://firebasestorage.googleapis.., username=user04} }
                    val user = it.getValue(User::class.java)
                    if (user!=null){
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->  // 사용자 목록 중 한명 눌렀을 때

                    val userItem = item as UserItem
                    val intent= Intent(view.context, ChatLogActivity::class.java)
//                    intent.putExtra(USER_KEY, item.user.username)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)

                    finish()

                }
                recyclerview_newmessage.adapter =adapter
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.username

        PicassoProvider.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
    }// will be called in our list for each user object later on..
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}