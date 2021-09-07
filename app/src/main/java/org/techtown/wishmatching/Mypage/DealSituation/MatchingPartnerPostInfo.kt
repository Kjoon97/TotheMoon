package org.techtown.wishmatching.Mypage.DealSituation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_deal_complete.*
import kotlinx.android.synthetic.main.activity_deal_situ.*
import kotlinx.android.synthetic.main.activity_matching_partner_post_info.*
import kotlinx.android.synthetic.main.doingdeal_row.view.*
import org.techtown.wishmatching.Chatting.ChatLogActivity
import org.techtown.wishmatching.Database.PostDTO
import org.techtown.wishmatching.R

class MatchingPartnerPostInfo : AppCompatActivity() {
    var firestore : FirebaseFirestore? = null
    var storage : FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_partner_post_info)
        supportActionBar?.title = "상대 물품"
        storage = FirebaseStorage.getInstance() //스토리지 초기화
        firestore = FirebaseFirestore.getInstance()
        var data:MutableList<PostDTO> = mutableListOf()
//        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)



            val fromId = FirebaseAuth.getInstance().uid.toString() // 현재 사용자
            val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")


            var my_partner_id:String = ""
            var partner_like_post:String = ""

            var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
            firestore = FirebaseFirestore.getInstance()  //초기화
            firestore!!.collection("Matching_Post")
                .document("${ChatLogActivity.toUser!!.uid.toString()}"+"${fromId.toString()}")
                .get()
                .addOnSuccessListener {
                    my_partner_id= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
//                    Toast.makeText(this,"$my_like_post",Toast.LENGTH_LONG).show()

                    firestore
                        ?.collection("post")!!
                        .whereEqualTo("uid", my_partner_id)
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


                            partner_goods_Recyclerview.adapter = adapter
                            partner_goods_Recyclerview.layoutManager = LinearLayoutManager(this)

                            adapter.setItemClickListener(object : RecyclerViewAdapter.onItemClickListener{      //리사이클러 뷰를 눌렀을 때 발생한는 클릭 이벤트
                                override fun onClick(v: View, position: Int) {
                                    val intent = Intent(this@MatchingPartnerPostInfo, MyItemMoreInfoActivity::class.java)
                                    intent.putExtra("doc_id", v.documentID.text.toString())
                                    intent.putExtra("state","finish")
                                    startActivity(intent)

                                }

                            })
                        }

                }


//        firestore
//            ?.collection("post")!!
//            .whereEqualTo("uid", Authentication.auth.currentUser!!.uid)
//            .whereEqualTo("dealsituation", "dealComplete")
//            .get()
//            .addOnSuccessListener { documents->
//                for(document in documents){
//                    data.add(PostDTO(document.get("documentId").toString(), document.get("imageUrl").toString(),
//                        document.get("imageUrl2").toString(),document.get("imageUrl3").toString(),document.get("imageUrl4").toString(),
//                        document.get("imageUrl5").toString(), document.get("uid").toString(), document.get("title").toString(), document.get("content").toString(), document.get("category").toString(), document.get("dealsituation").toString()))
//
//                }
//                var adapter = RecyclerViewAdapt(this)
//                adapter.Postdata = data
//
//
//                my_goods_Recyclerview.adapter = adapter
//                my_goods_Recyclerview.layoutManager = LinearLayoutManager(this)
//
//                adapter.setItemClickListener(object : RecyclerViewAdapt.onItemClickListener{      //리사이클러 뷰를 눌렀을 때 발생한는 클릭 이벤트
//                    override fun onClick(v: View, position: Int) {
//                        val intent = Intent(this@MatchingPartnerPostInfo, MyItemMoreInfoActivity::class.java)
//                        intent.putExtra("doc_id", v.documentID.text.toString())
//                        intent.putExtra("state","finish")
//                        startActivity(intent)
//
//                    }
//
//                })
//            }
    }

}


