package org.techtown.wishmatching

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.list_item.*
import org.techtown.wishmatching.Database.PostDTO


class HomeFragment : Fragment() {
    private lateinit var listAdapter: ListAdapter
    var arrayList = ArrayList<PostDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val database = Firebase.database
        val databaseReference = database.getReference("post")




//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Get Post object and use the values to update the UI
//                val post = dataSnapshot.getValue<PostDTO>()
//                var snapshot : DataSnapshot = dataSnapshot.child("post")
//                var snapshotcount : Long = dataSnapshot.childrenCount
//
//                for(i in 0..snapshotcount)
//                {
//                    var PostDTO : PostDTO = snapshot.getValue<PostDTO>() as PostDTO
//                    arrayList.add(PostDTO)
//                }
//                listAdapter.notifyDataSetChanged()
//
//                if (post != null) {
//                    arrayList.add(post)
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//        databaseReference.addValueEventListener(postListener)


        // Inflate the layout for this fragment

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var list: ArrayList<PostDTO> =
            requireActivity().intent!!.extras!!.get("DataList") as ArrayList<PostDTO>
        Log.e("FirstFragment", "Data List: ${list}")



        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listAdapter = ListAdapter(list)
        listView.layoutManager = gridLayoutManager
        listView.adapter = listAdapter

//        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL,false)

    }
    // 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_homefragment, menu)
    }
    // 메뉴 버튼 클릭시 이벤트 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                val intent = Intent(activity, AddPostActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_test -> {
//                startActivity(Intent(activity,ProfileActivity::class.java))
                val user = Firebase.auth.currentUser
                user?.let {
                    // Name, email address, and profile photo Url
                    val name = user.displayName
                    val email = user.email
                    val photoUrl = user.photoUrl

                    // Check if user's email is verified
                    val emailVerified = user.isEmailVerified

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    val uid = user.uid
                }

                if (user != null) {
                    Toast.makeText(activity,"${user.uid}",Toast.LENGTH_LONG).show()
                }
                true
            }
            R.id.action_logout -> {
                activity?.let { MySharedPreferences.clearUser(it) }
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
//    override fun onPrepareOptionsMenu(menu: Menu){
//        super.onPrepareOptionsMenu(menu)
//        val item = menu.findItem(R.id.action_done)
//        item.isVisible = isEditing
//    }


}