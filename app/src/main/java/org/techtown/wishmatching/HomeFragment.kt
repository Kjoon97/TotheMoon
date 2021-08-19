package org.techtown.wishmatching

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import org.techtown.wishmatching.Chatting.NewMessageActivity
import org.techtown.wishmatching.Database.PostDTO


class HomeFragment : Fragment() {
//    private lateinit var callback: OnBackPressedCallback
    var mBackWait:Long = 0
    private lateinit var listAdapter: ListAdapter
    var arrayList = ArrayList<PostDTO>()
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
//    var dataList: ArrayList<PostDTO> = arrayListOf(
//        PostDTO("https://firebasestorage.googleapis.com/v0/b/wishmatching-ed07a.appspot.com/o/Post%2FIMAGE_20210808_224047_.png?alt=media&token=7616bfae-af82-4d4b-957e-6c0d8f0477e0","","","",""),
//
//        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val database = Firebase.database
        val databaseReference = database.getReference("post")
        val v : View = inflater.inflate(R.layout.fragment_home,container,false)

//        var pullToRefresh : SwipeRefreshLayout = v.findViewById(R.id.pullToRefresh)
//
//        pullToRefresh.setOnRefreshListener {
//            Toast.makeText(MainActivity(),"swipe",Toast.LENGTH_LONG).show()
//            pullToRefresh.isRefreshing = false
//        }


        // Inflate the layout for this fragment

        setHasOptionsMenu(true)
        return v
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
                Authentication.auth.signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)


                true
            }
            R.id.friendlist-> {
                val intent = Intent(activity, NewMessageActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }




}