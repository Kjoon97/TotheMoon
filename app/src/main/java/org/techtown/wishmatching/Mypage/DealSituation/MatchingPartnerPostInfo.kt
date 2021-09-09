package org.techtown.wishmatching.Mypage.DealSituation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.provider.PicassoProvider
import kotlinx.android.synthetic.main.activity_deal_complete.*
import kotlinx.android.synthetic.main.activity_deal_situ.*
import kotlinx.android.synthetic.main.activity_matching_partner_post_info.*
import kotlinx.android.synthetic.main.doingdeal_row.view.*
import kotlinx.android.synthetic.main.matching_doingdeal_row.view.*
import org.techtown.wishmatching.Authentication
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
                            var adapter = MatchRecyclerViewAdapter(this)
                            adapter.Postdata = data


                            partner_goods_Recyclerview.adapter = adapter
                            partner_goods_Recyclerview.layoutManager = LinearLayoutManager(this)

                            adapter.setItemClickListener(object : RecyclerViewAdapter.onItemClickListener{      //리사이클러 뷰를 눌렀을 때 발생한는 클릭 이벤트
                                override fun onClick(v: View, position: Int) {
                                    val intent = Intent(this@MatchingPartnerPostInfo, MyItemMoreInfoActivity::class.java)
                                    intent.putExtra("doc_id", v.matching_documentID.text.toString())
                                    intent.putExtra("state","doing")
                                    startActivity(intent)

                                }

                            })
                        }

                }

    }

}

class MatchRecyclerViewAdapter(val c: Context): RecyclerView.Adapter<MatchRecyclerViewAdapter.ViewHolder>() {
    var Postdata = mutableListOf<PostDTO>()

    interface onItemClickListener {
        fun onClick(v: View, position: Int)
    }
    private lateinit var itemClickListener: RecyclerViewAdapter.onItemClickListener

    fun setItemClickListener(onItemClickListener: RecyclerViewAdapter.onItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var firestore = FirebaseFirestore.getInstance()

        fun setPost(post : PostDTO){
            val fromId = FirebaseAuth.getInstance().uid // 현재 사용자

            var post_id = post.documentId
            var check_like : String? = null
            var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
            firestore = FirebaseFirestore.getInstance()  //초기화



//            firestore!!.collection("Matching_Post_id")
//                .document("${ChatLogActivity.toUser!!.uid.toString()}"+"${fromId.toString()}"+"$post_id")
//                .get()
//                .addOnSuccessListener {
//                    check_like= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
//                    Log.d("1123","112111 : ${post_id}")
//                }

//            Toast.makeText(Activity(),check_like.toString(),Toast.LENGTH_SHORT).show()
//            if(check_like != null && check_like != "") {
//                itemView.matching_card.setCardBackgroundColor(Color.parseColor("#FFDCFF"))
//            }

            PicassoProvider.get().load(post.imageUrl).into(itemView.matching_doingdeal_row_image)
            itemView.matching_stuff_name.text = post.title.toString()
            itemView.matching_documentID.text =post.documentId
            firestore!!.collection("Matching_Post_id")
                .document("${ChatLogActivity.toUser!!.uid.toString()}"+"${fromId.toString()}"+"$post_id")
                .get()
                .addOnSuccessListener {
                    check_like= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
                    Log.d("1123","112111 : ${check_like}")
                    itemView.matching_card.setCardBackgroundColor(Color.parseColor("#FFDCFF"))
                }
            firestore!!.collection("Matching_Post_id")
                .document("${fromId.toString()}"+"${ChatLogActivity.toUser!!.uid.toString()}"+"$post_id")
                .get()
                .addOnSuccessListener {
                    check_like= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
                    Log.d("1123","112111 : ${check_like}")
                    itemView.matching_card.setCardBackgroundColor(Color.parseColor("#FFDCFF"))
                }
            firestore!!.collection("user")
                .whereEqualTo("uid", Authentication.auth.currentUser!!.uid).limit(1)
                .get()
                .addOnSuccessListener { documents->
                    for(document in documents){
                        itemView.matching_deal_location.text = document.data["area"].toString()
                    }
                }
        }
    }


    override fun onBindViewHolder(holder: MatchRecyclerViewAdapter.ViewHolder, position: Int) {

        var firestore = FirebaseFirestore.getInstance()
        val fromId = FirebaseAuth.getInstance().uid // 현재 사용자
        firestore = FirebaseFirestore.getInstance()  //초기화
        var check_like : String? = null
//        Log.d("1123","112111 : ${post_id}")

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }



        val post = Postdata.get(position)
        val post_id = post.documentId


//        var post_id = post.documentId
//
//        firestore = FirebaseFirestore.getInstance()  //초기화
//        firestore!!.collection("Matching_Post_id")
//            .document("${ChatLogActivity.toUser!!.uid.toString()}"+"${fromId.toString()}"+"$post_id")
//            .get()
//            .addOnSuccessListener {
//                check_like= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
//                Log.d("1123","112111 : ${post_id}")
//            }

        holder.setPost(post)
    }

    override fun getItemCount(): Int {
        return Postdata.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.matching_doingdeal_row,parent,false)
        return ViewHolder(view)
    }


}
