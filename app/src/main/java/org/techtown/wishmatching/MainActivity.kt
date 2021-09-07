package org.techtown.wishmatching
// commit test gb
// commit test kjh

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_main.*
import org.techtown.wishmatching.Database.PostDTO


class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var prefs : PreferenceUtil
    }
    private lateinit var auth: FirebaseAuth
    var storage : FirebaseStorage? = null
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
    private lateinit var database: DatabaseReference
    val storageReference = Firebase.storage.reference
    var mBackWait:Long = 0
    private val fragmentManager = supportFragmentManager
    public lateinit var mcontext : Context
    var dataList: ArrayList<PostDTO> = arrayListOf()
    var infoList : ArrayList<String> = arrayListOf()
    var categoryList : Array<String> = arrayOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mcontext = this
        prefs = PreferenceUtil(applicationContext)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("ttttt", msg)
        })

        // 각 탭 마다 타이틀바 제목 변경
        view_pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                if(position == 0) {
                    val actionBar: ActionBar? = supportActionBar
                    if (actionBar != null) {
                        actionBar.setTitle("Home")
                    }
                }
                else if(position == 1) {
                    val actionBar: ActionBar? = supportActionBar
                    if (actionBar != null) {
                        actionBar.setTitle("Chat")
                    }
                }
                else if(position == 2) {
                    val actionBar: ActionBar? = supportActionBar
                    if (actionBar != null) {
                        actionBar.setTitle("My Page  ")
                    }
                }

            }

        })



        storage = FirebaseStorage.getInstance() //스토리지 초기화
//        auth = FirebaseAuth.getInstance()            //초기화
        firestore = FirebaseFirestore.getInstance()  //초기화

        val database = Firebase.database.reference
        val postReference = FirebaseDatabase.getInstance().getReference("post")

//        auth = FirebaseAuth.getInstance()
        firestore!!.collection("user")
            .whereEqualTo("uid",Authentication.auth.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    infoList.add(document.data["userCategory1"].toString())
                    infoList.add(document.data["userCategory2"].toString())
                    infoList.add(document.data["userCategory3"].toString())
                }
                if (infoList.isNullOrEmpty()){
                    firestore!!.collection("post")
                        .whereEqualTo("dealsituation", "doingDeal")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                                dataList.add(
                                    PostDTO(
                                        document.data["documentId"].toString(),
                                        document.data["imageUrl"].toString(),
                                        document.data["imageUrl2"].toString(),
                                        document.data["imageUrl3"].toString(),
                                        document.data["imageUrl4"].toString(),
                                        document.data["imageUrl5"].toString(),
                                        document.data["uid"].toString(),
                                        document.data["title"].toString(),
                                        document.data["content"].toString(),
                                        document.data["category"].toString(),
                                        document.data["dealsituation"].toString(),
                                        document.data["date"].toString()
                                    )
                                )
                                intent.putExtra("DataList", dataList)
                                configureBottomNavigation()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                        }
                }
                else if(infoList.get(0)=="") { //카테고리 설정 안되어있을 경우 전체 게시글 출력
                    firestore!!.collection("post")
                        .whereEqualTo("dealsituation", "doingDeal")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                                dataList.add(
                                    PostDTO(
                                        document.data["documentId"].toString(),
                                        document.data["imageUrl"].toString(),
                                        document.data["imageUrl2"].toString(),
                                        document.data["imageUrl3"].toString(),
                                        document.data["imageUrl4"].toString(),
                                        document.data["imageUrl5"].toString(),
                                        document.data["uid"].toString(),
                                        document.data["title"].toString(),
                                        document.data["content"].toString(),
                                        document.data["category"].toString(),
                                        document.data["dealsituation"].toString(),
                                        document.data["date"].toString()
                                    )
                                )
                                intent.putExtra("DataList", dataList)
                                configureBottomNavigation()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                        }
                }
                else if (infoList.get(1)==""){ //설정한 카테고리 1개일때
                    firestore!!.collection("post")
                        .whereEqualTo("dealsituation", "doingDeal")
                        .whereEqualTo("category", infoList[0])
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                if(document.data["category"].toString() == infoList[0]) {
                                    Log.d(TAG, "${document.id} => ${document.data}")
                                    dataList.add(
                                        PostDTO(
                                            document.data["documentId"].toString(),
                                            document.data["imageUrl"].toString(),
                                            document.data["imageUrl2"].toString(),
                                            document.data["imageUrl3"].toString(),
                                            document.data["imageUrl4"].toString(),
                                            document.data["imageUrl5"].toString(),
                                            document.data["uid"].toString(),
                                            document.data["title"].toString(),
                                            document.data["content"].toString(),
                                            document.data["category"].toString(),
                                            document.data["dealsituation"].toString(),
                                            document.data["date"].toString()
                                        )
                                    )
                                }
                            }
                            if(dataList.isEmpty()) {
                                firestore!!.collection("post")
                                    .whereEqualTo("dealsituation", "doingDeal")
                                    .get()
                                    .addOnSuccessListener { result ->
                                        for (document in result) {
                                                Log.d(TAG, "${document.id} => ${document.data}")
                                                dataList.add(
                                                    PostDTO(
                                                        document.data["documentId"].toString(),
                                                        document.data["imageUrl"].toString(),
                                                        document.data["imageUrl2"].toString(),
                                                        document.data["imageUrl3"].toString(),
                                                        document.data["imageUrl4"].toString(),
                                                        document.data["imageUrl5"].toString(),
                                                        document.data["uid"].toString(),
                                                        document.data["title"].toString(),
                                                        document.data["content"].toString(),
                                                        document.data["category"].toString(),
                                                        document.data["dealsituation"].toString(),
                                                        document.data["date"].toString()
                                                    )
                                                )

                                            intent.putExtra("DataList", dataList)
                                            configureBottomNavigation()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(TAG, "Error getting documents: ", exception)
                                    }
                                Toast.makeText(this,"선택한 카테고리의 상품이 없습니다.",Toast.LENGTH_SHORT).show()

                            }
                            intent.putExtra("DataList", dataList)
                            configureBottomNavigation()
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                        }
                }
                else if (infoList.get(2)==""){ //설정한 카테고리 2개일때
                    firestore!!.collection("post")
                        .whereEqualTo("dealsituation", "doingDeal")
                        /*.whereEqualTo("category", infoList[0])
                        .whereEqualTo("category", infoList[1])*/
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                                if (document.data["category"].toString() == infoList[0] || document.data["category"].toString() == infoList[1]) {
                                    dataList.add(
                                        PostDTO(
                                            document.data["documentId"].toString(),
                                            document.data["imageUrl"].toString(),
                                            document.data["imageUrl2"].toString(),
                                            document.data["imageUrl3"].toString(),
                                            document.data["imageUrl4"].toString(),
                                            document.data["imageUrl5"].toString(),
                                            document.data["uid"].toString(),
                                            document.data["title"].toString(),
                                            document.data["content"].toString(),
                                            document.data["category"].toString(),
                                            document.data["dealsituation"].toString(),
                                            document.data["date"].toString()
                                        )
                                    )
                                }
                            }
                            if(dataList.isEmpty()) {
                                firestore!!.collection("post")
                                    .whereEqualTo("dealsituation", "doingDeal")
                                    .get()
                                    .addOnSuccessListener { result ->
                                        for (document in result) {
                                            Log.d(TAG, "${document.id} => ${document.data}")
                                            dataList.add(
                                                PostDTO(
                                                    document.data["documentId"].toString(),
                                                    document.data["imageUrl"].toString(),
                                                    document.data["imageUrl2"].toString(),
                                                    document.data["imageUrl3"].toString(),
                                                    document.data["imageUrl4"].toString(),
                                                    document.data["imageUrl5"].toString(),
                                                    document.data["uid"].toString(),
                                                    document.data["title"].toString(),
                                                    document.data["content"].toString(),
                                                    document.data["category"].toString(),
                                                    document.data["dealsituation"].toString(),
                                                    document.data["date"].toString()
                                                )
                                            )

                                            intent.putExtra("DataList", dataList)
                                            configureBottomNavigation()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(TAG, "Error getting documents: ", exception)
                                    }
                                Toast.makeText(this,"선택한 카테고리의 상품이 없습니다.",Toast.LENGTH_SHORT).show()

                            }
                            intent.putExtra("DataList", dataList)
                            configureBottomNavigation()
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                        }
                }
                else { //설정한 카테고리 3개일때
                    firestore!!.collection("post")
                        .whereEqualTo("dealsituation", "doingDeal")
                        /*.whereEqualTo("category", infoList[0])
                        .whereEqualTo("category", infoList[1])*/
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                                if (document.data["category"].toString() == infoList[0] || document.data["category"].toString() == infoList[1]
                                    || document.data["category"].toString() == infoList[2]) {
                                    dataList.add(
                                        PostDTO(
                                            document.data["documentId"].toString(),
                                            document.data["imageUrl"].toString(),
                                            document.data["imageUrl2"].toString(),
                                            document.data["imageUrl3"].toString(),
                                            document.data["imageUrl4"].toString(),
                                            document.data["imageUrl5"].toString(),
                                            document.data["uid"].toString(),
                                            document.data["title"].toString(),
                                            document.data["content"].toString(),
                                            document.data["category"].toString(),
                                            document.data["dealsituation"].toString(),
                                            document.data["date"].toString()
                                        )
                                    )
                                }
                            }
                            if(dataList.isEmpty()) {
                                firestore!!.collection("post")
                                    .whereEqualTo("dealsituation", "doingDeal")
                                    .get()
                                    .addOnSuccessListener { result ->
                                        for (document in result) {
                                            Log.d(TAG, "${document.id} => ${document.data}")
                                            dataList.add(
                                                PostDTO(
                                                    document.data["documentId"].toString(),
                                                    document.data["imageUrl"].toString(),
                                                    document.data["imageUrl2"].toString(),
                                                    document.data["imageUrl3"].toString(),
                                                    document.data["imageUrl4"].toString(),
                                                    document.data["imageUrl5"].toString(),
                                                    document.data["uid"].toString(),
                                                    document.data["title"].toString(),
                                                    document.data["content"].toString(),
                                                    document.data["category"].toString(),
                                                    document.data["dealsituation"].toString(),
                                                    document.data["date"].toString()
                                                )
                                            )

                                            intent.putExtra("DataList", dataList)
                                            configureBottomNavigation()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(TAG, "Error getting documents: ", exception)
                                    }
                                Toast.makeText(this,"선택한 카테고리의 상품이 없습니다.",Toast.LENGTH_SHORT).show()

                            }
                            intent.putExtra("DataList", dataList)
                            configureBottomNavigation()
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                        }
                }
            }
    }



    // 네비게이션바 , 뷰페이지 어댑터 설정
    private fun configureBottomNavigation() {
        view_pager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 3)

        tl_ac_main_bottom_menu.setupWithViewPager(view_pager)

        val bottomNaviLayout: View =
            this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        tl_ac_main_bottom_menu.getTabAt(0)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(1)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_add_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(2)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_my_page_tab) as RelativeLayout
    }


    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        val homeFragment = fragmentManager.findFragmentByTag("home")
        val chatFragment = fragmentManager.findFragmentByTag("chat")
        val mypageFragment = fragmentManager.findFragmentByTag("mypage")
        if(view_pager.currentItem == 0) {
            if(System.currentTimeMillis() - mBackWait >=2000 ) {
                mBackWait = System.currentTimeMillis()
                val mySnackbar = Snackbar.make(findViewById(R.id.frag_home),
                    "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT)

                mySnackbar.setTextColor(Color.WHITE)
                mySnackbar.show()
            } else {
                ActivityCompat.finishAffinity(this)
                System.runFinalization()
                System.exit(0)
            }
        }
        else if(view_pager.currentItem == 1) {
            if(System.currentTimeMillis() - mBackWait >=2000 ) {
                mBackWait = System.currentTimeMillis()
                val mySnackbar = Snackbar.make(findViewById(R.id.frag_chat),
                    "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT)

                mySnackbar.setTextColor(Color.WHITE)
                mySnackbar.show()
            } else {
                ActivityCompat.finishAffinity(this)
                System.runFinalization()
                System.exit(0)
            }
        }
        else if(view_pager.currentItem == 2){
            if(System.currentTimeMillis() - mBackWait >=2000 ) {
                mBackWait = System.currentTimeMillis()
                val mySnackbar = Snackbar.make(findViewById(R.id.frag_mypage),
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
    public fun refresh() {
        MainFragmentStatePagerAdapter(supportFragmentManager, 3).notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 3) {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }



}