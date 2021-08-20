package org.techtown.wishmatching.Mypage

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.techtown.wishmatching.Authentication
import org.techtown.wishmatching.Mypage.DealSituation.DealSituActivity
import org.techtown.wishmatching.Mypage.DealSituation.DealSituationActivity
import org.techtown.wishmatching.R

class MyPageFragment : Fragment(){
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
    var storage : FirebaseStorage? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()  //초기화
        storage = FirebaseStorage.getInstance() //스토리지 초기화

        mypage_changeProfile.setOnClickListener {  //프로필 변경 페이지로 이동
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }
        layout_myPage_edtLoc.setOnClickListener {  //동네 변경 페이지로 이동
            val intent = Intent(context, EditLocationActivity::class.java)
            startActivity(intent)
        }
        layout_myPage_deal_situation.setOnClickListener {  //동네 변경 페이지로 이동
            val intent = Intent(context, DealSituActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        firestore!!.collection("user")
            .whereEqualTo("uid", Authentication.auth.currentUser!!.uid).limit(1)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    mypage_location.text = document.data["area"].toString()
                    mypage_nickname.text = document.data["nickname"].toString()
                    val image = storage!!.getReferenceFromUrl(document.data["imageUrl"].toString())
                    displayImageRef(image, img_myPage_profileImg)

                }
            }

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