package org.techtown.wishmatching.Mypage.DealSituation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.provider.PicassoProvider
import kotlinx.android.synthetic.main.activity_deal_situ.*
import kotlinx.android.synthetic.main.doingdeal_row.view.*
import org.techtown.wishmatching.Database.PostDTO
import org.techtown.wishmatching.R

class DealSituActivity : AppCompatActivity() {
    var firestore : FirebaseFirestore? = null
    var storage : FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_situ)
        supportActionBar?.title = "등록 목록"
        storage = FirebaseStorage.getInstance() //스토리지 초기화
        firestore = FirebaseFirestore.getInstance()
        var data:MutableList<PostDTO> = mutableListOf()
        firestore?.collection("post")!!.get().addOnSuccessListener { documents->
            for(document in documents){
                data.add(PostDTO(document.get("documentId").toString(), document.get("imageUrl").toString(),
                    document.get("uid").toString(), document.get("title").toString(), document.get("content").toString(), document.get("category").toString()))
            }
            var adapter = RecyclerViewAdapter()
            adapter.Postdata = data
            my_goods_Recyclerview.adapter = adapter
            my_goods_Recyclerview.layoutManager = LinearLayoutManager(this)
        }
    }
}


class RecyclerViewAdapter: RecyclerView.Adapter<ViewHolder>(){
    var Postdata = mutableListOf<PostDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.doingdeal_row,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = Postdata.get(position)
        holder.setPost(post)
    }
    override fun getItemCount(): Int {
        return Postdata.size
    }


}

class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    fun setPost(post : PostDTO){
        itemView.stuff_name.text = post.title.toString()
        PicassoProvider.get().load(post.imageUrl).into(itemView.doingdeal_row_image)
    }
}