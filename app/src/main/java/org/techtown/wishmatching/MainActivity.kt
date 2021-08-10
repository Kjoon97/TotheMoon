package org.techtown.wishmatching
// commit test gb
// commit test kjh
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.techtown.wishmatching.Database.PostDTO

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var storage : FirebaseStorage? = null
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
    private lateinit var database: DatabaseReference
    val storageReference = Firebase.storage.reference
    var mBackWait:Long = 0
    var dataList: ArrayList<PostDTO> = arrayListOf(
        PostDTO("https://firebasestorage.googleapis.com/v0/b/wishmatching-ed07a.appspot.com/o/Post%2FIMAGE_20210808_224047_.png?alt=media&token=7616bfae-af82-4d4b-957e-6c0d8f0477e0","","","",""),

    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = FirebaseStorage.getInstance() //스토리지 초기화
        auth = FirebaseAuth.getInstance()            //초기화
        firestore = FirebaseFirestore.getInstance()  //초기화

        val database = Firebase.database.reference
        val postReference = FirebaseDatabase.getInstance().getReference("post")

        auth = FirebaseAuth.getInstance()

        firestore!!.collection("post")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    dataList.add(PostDTO(document.data["imageUrl"].toString(),document.data["uid"].toString(),document.data["title"].toString(),document.data["content"].toString(),document.data["category"].toString()))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }


//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Get Post object and use the values to update the UI
//                val post = dataSnapshot.getValue<PostDTO>()
//                // ...
//                var snapshot : DataSnapshot = dataSnapshot.child("post")
//                var snapshotcount : Long = dataSnapshot.childrenCount
//
//                for(i in 0..snapshotcount)
//                {
//                    var PostDTO : PostDTO = snapshot.getValue<PostDTO>() as PostDTO
//                    dataList.add(PostDTO)
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//        postReference.addValueEventListener(postListener)

        intent.putExtra("DataList", dataList)
        configureBottomNavigation()

//        getSupportActionBar()?.setIcon(R.drawable.font_wishmatching_bold35)
//        getSupportActionBar()?.setDisplayUseLogoEnabled(true)
//        getSupportActionBar()?.setDisplayShowHomeEnabled(true)


    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        var mInflater = menuInflater
//        mInflater.inflate(R.menu.testmenu,menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.profilemenu_test-> startActivity(Intent(this,ProfileActivity::class.java))
//        }
//        return super.onOptionsItemSelected(item)
//    }

    // 네비게이션바 , 뷰페이지 어댑터 설정
    private fun configureBottomNavigation() {
        vp_ac_main_frag_pager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 3)

        tl_ac_main_bottom_menu.setupWithViewPager(vp_ac_main_frag_pager)

        val bottomNaviLayout: View =
            this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        tl_ac_main_bottom_menu.getTabAt(0)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(1)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_add_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(2)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_my_page_tab) as RelativeLayout
    }

//    override fun onStart() {
//        firestore!!.collection("post")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                    dataList.add(PostDTO(document.data["imageUrl"].toString(),document.data["uid"].toString(),document.data["title"].toString(),document.data["content"].toString(),document.data["category"].toString()))
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting documents: ", exception)
//            }
//        super.onStart()
//    }
override fun onBackPressed() {
    // 뒤로가기 버튼 클릭
    if(System.currentTimeMillis() - mBackWait >=2000 ) {
        mBackWait = System.currentTimeMillis()
        val mySnackbar = Snackbar.make(findViewById(R.id.home_layout),
            "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT)
        mySnackbar.setTextColor(Color.WHITE)
        mySnackbar.show()
    } else {
        ActivityCompat.finishAffinity(this)
        System.runFinalization()
        System.exit(0)
    }
}
}