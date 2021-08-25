package org.techtown.wishmatching

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class MoreInfoAdapter(private val List: ArrayList<MoreInfoImageList>) : RecyclerView.Adapter<MoreInfoAdapter.PagerViewHolder>() {
    var firestore : FirebaseFirestore? = null

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var moreinfo_imgview: ImageView = itemView.findViewById(R.id.img_moreInfo_picture)
        fun bind(@ColorRes bgColor: Int, position: Int) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.activity_more_info,
            parent,
            false
        )
        return PagerViewHolder(view)
    }
    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        firestore = FirebaseFirestore.getInstance()
        Glide.with(holder.itemView)
            .load(List.get(position).imageUrl)
            .into(holder.moreinfo_imgview)
    }

    override fun getItemCount(): Int = List.size
}
