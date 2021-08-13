package org.techtown.wishmatching.Chatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.provider.PicassoProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import org.techtown.wishmatching.R
import org.techtown.wishmatching.RealtimeDB.User


class ChattingFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    companion object{
        val USER_KEY = "USER_KEY"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val adapter = GroupAdapter<ViewHolder>()
        val v: View = inflater.inflate(R.layout.fragment_chatting, container, false)
        var recyclerview_newmassage = v.findViewById<RecyclerView>(R.id.recyclerview_newmassage)

//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        recyclerview_newmassage.adapter = adapter


        fun fetchUsers() {    //파베로부터 유저 데이터 가져옴
            val ref = FirebaseDatabase.getInstance().getReference("/users")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val adapter = GroupAdapter<ViewHolder>()

                    p0.children.forEach {
                        Log.d("NewMessage",it.toString())
                        val user = it.getValue(User::class.java)
                        if(user != null) {
                            adapter.add(UserItem(user))
                        }
                    }
                    adapter.setOnItemClickListener { item, view ->  // 사용자 목록 중 한명 눌렀을 때

                        val userItem = item as UserItem
                        val intent= Intent(view.context, ChatLogActivity::class.java)
//                    intent.putExtra(USER_KEY, item.user.username)
                        intent.putExtra(USER_KEY,userItem.user)
                        startActivity(intent)

                    }
                    recyclerview_newmassage.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        fetchUsers()


        // Inflate the layout for this fragment
        return v


    }



//class CustomAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//}
class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.username
        PicassoProvider.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}

//    override fun onResume() {
//        (activity as MainActivity).setActionBarTitle()
//        super.onResume()
//
//    }
}