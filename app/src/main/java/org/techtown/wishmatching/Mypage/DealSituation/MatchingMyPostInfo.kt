package org.techtown.wishmatching.Mypage.DealSituation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_deal_situ.*
import kotlinx.android.synthetic.main.activity_matching_my_post_info.*
import kotlinx.android.synthetic.main.doingdeal_row.*
import kotlinx.android.synthetic.main.doingdeal_row.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.techtown.wishmatching.Authentication
import org.techtown.wishmatching.Database.PostDTO
import org.techtown.wishmatching.R

class MatchingMyPostInfo : AppCompatActivity() {
    var refresh_arrayList = ArrayList<PostDTO>()
    var firestore : FirebaseFirestore? = null
    var storage : FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_my_post_info)
        supportActionBar?.title = "나의 물품"
        storage = FirebaseStorage.getInstance() //스토리지 초기화
        firestore = FirebaseFirestore.getInstance()
        var data:MutableList<PostDTO> = mutableListOf()


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

        firestore
            ?.collection("post")!!
            .whereEqualTo("uid", Authentication.auth.currentUser!!.uid)
            .whereEqualTo("dealsituation", "doingDeal")
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    data.add(PostDTO(document.get("documentId").toString(), document.get("imageUrl").toString(),
                        document.get("imageUrl2").toString(),document.get("imageUrl3").toString(),document.get("imageUrl4").toString(),
                        document.get("imageUrl5").toString(), document.get("uid").toString(), document.get("title").toString(), document.get("content").toString(), document.get("category").toString(), document.get("dealsituation").toString()))
                }
                var adapter = RecyclerViewAdapter(this)
                adapter.Postdata = data


                matching_my_goods_Recyclerview.adapter = adapter
                matching_my_goods_Recyclerview.layoutManager = LinearLayoutManager(this)
                adapter.setItemClickListener(object : RecyclerViewAdapter.onItemClickListener{      //리사이클러 뷰를 눌렀을 때 발생한는 클릭 이벤트
                    override fun onClick(v: View, position: Int) {
                        val intent = Intent(this@MatchingMyPostInfo, MyItemMoreInfoActivity::class.java)
                        intent.putExtra("doc_id", v.documentID.text.toString())
                        intent.putExtra("state","doing")
                        startActivity(intent)

                    }

                })
            }

    }
}

