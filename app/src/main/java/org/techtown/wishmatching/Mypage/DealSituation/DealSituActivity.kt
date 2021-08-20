package org.techtown.wishmatching.Mypage.DealSituation

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_deal_situ.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import kotlinx.android.synthetic.main.my_goods_recyclerview_item.*
import kotlinx.android.synthetic.main.my_goods_recyclerview_item.view.*
import org.techtown.wishmatching.Authentication
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

        my_goods_Recyclerview.adapter = RecyclerViewAdapter()
        my_goods_Recyclerview.layoutManager = LinearLayoutManager(this)
    }
    inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var Postdata: ArrayList<PostDTO> = arrayListOf()

        init {
            firestore?.collection("post")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    Postdata.clear()
                    for(snapshot in querySnapshot!!.documents){
                        var item = snapshot.toObject(PostDTO::class.java)
                        if (item != null) {
                            Postdata.add(item)
                        }
                    }
                    notifyDataSetChanged()
                }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.my_goods_recyclerview_item,parent,false)
            return ViewHolder(view)
        }
        inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView
            viewHolder.my_goods_name.text = Postdata[position].title
            val image = storage!!.getReferenceFromUrl(Postdata[position].imageUrl.toString())
            displayImageRef(image, my_goods_imageview)
        }
        override fun getItemCount(): Int {
            return Postdata.size
        }

        private fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
            imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                view.setImageBitmap(bmp)
            }?.addOnFailureListener {
                // Failed to download the image
            }
        }


    }
}