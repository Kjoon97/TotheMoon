package org.techtown.wishmatching

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.techtown.wishmatching.Database.PostDTO

class ListAdapter (private var list: ArrayList<PostDTO>): RecyclerView.Adapter<ListAdapter.ListItemViewHolder>() {


    // inner class로 ViewHolder 정의
    inner class ListItemViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {


        var photourl: ImageView = itemView!!.findViewById(R.id.item_photo)
        var btn_like: ImageView = itemView!!.findViewById(R.id.btn_like)
        var state_like: Int = 0


        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: ArrayList<PostDTO>, position: Int) {
//            photourl.setImageURI(data.)
//            photourl.setImageResource(data.size)

        }
    }

    // ViewHolder에게 item을 보여줄 View로 쓰일 item_data_list.xml를 넘기면서 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return ListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        val database = Firebase.database
        var dataSnapshot: DataSnapshot? = null
        if (dataSnapshot != null) {
            return dataSnapshot.childrenCount as Int
        }
        else
            return list.size
    }

    // ViewHolder의 bind 메소드를 호출한다.
    override fun onBindViewHolder(holder: ListAdapter.ListItemViewHolder, position: Int) {
        holder.btn_like.setOnClickListener {
            if(holder.state_like==0)
            {
                holder.btn_like.setImageResource(R.drawable.btn_clicked_heart)
                holder.state_like=1
            }
            else
            {
                holder.btn_like.setImageResource(R.drawable.btn_heart)
                holder.state_like=0
            }

        }
//        holder.bind(list,position)
//        var dataSnapshot: DataSnapshot? = null
//        var snapshot : DataSnapshot = dataSnapshot!!.child("post")
//        var snapshotcount : Long = dataSnapshot!!.childrenCount
//        for(i in 0..snapshotcount)
//        {
//            var PostDTO : PostDTO = snapshot.getValue<PostDTO>() as PostDTO
//            list.add(PostDTO)
//        }

        Log.d("ListAdapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        Glide.with(holder.itemView)
            .load(list.get(position).imageUrl)
            .into(holder.photourl)

    }

}