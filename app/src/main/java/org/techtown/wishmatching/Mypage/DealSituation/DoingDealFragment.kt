package org.techtown.wishmatching.Mypage.DealSituation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.twitter.sdk.android.core.models.TwitterCollection
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.doingdeal_row.*
import kotlinx.android.synthetic.main.fragment_doing_deal.*
import org.techtown.wishmatching.Database.PostDTO
import org.techtown.wishmatching.R
import kotlinx.android.synthetic.main.fragment_doing_deal.view.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.techtown.wishmatching.Mypage.EditLocationActivity
import org.techtown.wishmatching.Mypage.EditProfileActivity


class DoingDealFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v : View = inflater.inflate(R.layout.fragment_doing_deal,container,false)
        return v
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}