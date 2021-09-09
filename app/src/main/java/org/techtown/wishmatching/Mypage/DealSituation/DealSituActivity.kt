package org.techtown.wishmatching.Mypage.DealSituation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.provider.PicassoProvider
import kotlinx.android.synthetic.main.activity_deal_situ.*
import kotlinx.android.synthetic.main.doingdeal_row.*
import kotlinx.android.synthetic.main.doingdeal_row.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.techtown.wishmatching.Authentication
import org.techtown.wishmatching.Database.PostDTO
import org.techtown.wishmatching.R

class DealSituActivity : AppCompatActivity() {
    var refresh_arrayList = ArrayList<PostDTO>()
    var firestore : FirebaseFirestore? = null
    var storage : FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_situ)
        supportActionBar?.title = "등록 목록"
        storage = FirebaseStorage.getInstance() //스토리지 초기화
        firestore = FirebaseFirestore.getInstance()
        var data:MutableList<PostDTO> = mutableListOf()

        srl_deal_situ.setOnRefreshListener {  // 새로고침- 불완전..... 다른 방법 없나봐야함.
            val intent = Intent(this@DealSituActivity, DealSituActivity::class.java)
            startActivity(intent)
//            srl_deal_situ.isRefreshing = false
            finish()
        }


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


                my_goods_Recyclerview.adapter = adapter
                my_goods_Recyclerview.layoutManager = LinearLayoutManager(this)
                adapter.setItemClickListener(object : RecyclerViewAdapter.onItemClickListener{      //리사이클러 뷰를 눌렀을 때 발생한는 클릭 이벤트
                override fun onClick(v: View, position: Int) {
                    val intent = Intent(this@DealSituActivity, MyItemMoreInfoActivity::class.java)
                    intent.putExtra("doc_id", v.documentID.text.toString())
                    intent.putExtra("state","doing")
                    startActivity(intent)

                }

            })
        }

    }
}

class RecyclerViewAdapter(val c:Context): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
    var Postdata = mutableListOf<PostDTO>()

    interface onItemClickListener {
        fun onClick(v: View, position: Int)
    }

    private lateinit var itemClickListener: onItemClickListener

    fun setItemClickListener(onItemClickListener: onItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var mMenus: ImageView
        var firestore = FirebaseFirestore.getInstance()
        init{
            mMenus = itemView.findViewById(R.id.mMenus)
            mMenus.setOnClickListener { popupMenus(it) }
        }


        private fun popupMenus(v:View){  //팝업 메뉴
            val popupMenus = PopupMenu(c,v)
            popupMenus.inflate(R.menu.deal_situ_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.deal_complete->{  //거래 완료를 누르면 해당 물품의 디비의 dealsituation의 doingDeal ->dealComplete로 변경,
                        firestore
                            ?.collection("post")!!
                            .whereEqualTo("documentId", itemView.documentID.text.toString())
                            .get()
                            .addOnSuccessListener { documents->
                                for (document in documents){
                                    firestore!!.collection("post").document(document.id).update(mapOf(
                                        "dealsituation" to "dealComplete"
                                    ))
                                }
                            }
                        itemView.card.visibility = View.GONE
                        true
                    }
                    R.id.deal_situ_delete->{
                        AlertDialog.Builder(c)
                            .setTitle("삭제하기")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("정말로 삭제하시겠습니까? 등록된 물건이 영구히 삭제됩니다.")
                            .setPositiveButton("네"){
                                dialog,_->
                                firestore
                                    ?.collection("post")!!
                                    .whereEqualTo("documentId", itemView.documentID.text.toString())
                                    .get()
                                    .addOnSuccessListener { documents->
                                        for (document in documents){
                                            firestore!!.collection("post").document(document.id).delete()
                                        }
                                    }
                                itemView.card.visibility = View.GONE
                                Toast.makeText(c,"삭제되었습니다.",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("아니요"){
                                dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    else->true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible =true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }

        fun setPost(post : PostDTO){
            PicassoProvider.get().load(post.imageUrl).into(itemView.doingdeal_row_image)
            itemView.stuff_name.text = post.title.toString()
            itemView.documentID.text =post.documentId
            firestore!!.collection("user")
                .whereEqualTo("uid", Authentication.auth.currentUser!!.uid).limit(1)
                .get()
                .addOnSuccessListener { documents->
                    for(document in documents){
                        itemView.deal_location.text = document.data["area"].toString()
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.doingdeal_row,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        val post = Postdata.get(position)
        holder.setPost(post)
    }
    override fun getItemCount(): Int {
        return Postdata.size
    }




}