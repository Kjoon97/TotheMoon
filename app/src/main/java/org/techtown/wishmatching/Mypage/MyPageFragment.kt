package org.techtown.wishmatching.Mypage

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.techtown.wishmatching.Authentication
import org.techtown.wishmatching.CategoryActivity
import org.techtown.wishmatching.LoginActivity
import org.techtown.wishmatching.Mypage.DealSituation.DealCompleteActivity
import org.techtown.wishmatching.Mypage.DealSituation.DealSituActivity
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
        layout_myPage_deal_situation.setOnClickListener {  //물건 등록 페이지로 이동
            val intent = Intent(context, DealSituActivity::class.java)
            startActivity(intent)
        }
        layout_myPage_deal_complete.setOnClickListener {
            val intent = Intent(context, DealCompleteActivity::class.java)
            startActivity(intent)
        }
        layout_myPage_category.setOnClickListener {
            val intent = Intent(context, CategoryActivity::class.java)
            startActivity(intent)
        }
        
        layout_myPage_logout.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("네"){
                        dialog,_->

                    Authentication.auth.signOut()

                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("아니요"){
                        dialog,_->
                    dialog.dismiss()
                }
                .create()
                .show()


        }
        
        layout_myPage_delete.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("계정 삭제하기")
                .setIcon(R.drawable.ic_warning)
                .setMessage("정말로 삭제하시겠습니까?")
                .setPositiveButton("네"){
                        dialog,_->


                    firestore!!.collection("post")
                        .whereEqualTo("uid", Authentication.auth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            if(documents.isEmpty){
                                firestore!!.collection("user")
                                    .whereEqualTo("uid", Authentication.auth.currentUser!!.uid)
                                    .get()
                                    .addOnSuccessListener { documents->
                                        for(document in documents){
                                            firestore!!.collection("user").document(document.id).delete().addOnSuccessListener {
//                                        Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show()
                                                Authentication.auth.currentUser!!.delete().addOnSuccessListener {
                                                    val intent = Intent(activity, LoginActivity::class.java)
                                                    startActivity(intent)
                                                }
                                            }
                                        }
                                    }
                            } else{
                                var i = 0
                                for(document in documents){
                                    if(i == documents.size()-1 ){
                                        firestore!!.collection("post").document(document.id).delete().addOnSuccessListener {
                                            firestore!!.collection("user")
                                                .whereEqualTo("uid", Authentication.auth.currentUser!!.uid)
                                                .get()
                                                .addOnSuccessListener { documents->
                                                    for(document in documents){
                                                        firestore!!.collection("user").document(document.id).delete().addOnSuccessListener {
                                                            Authentication.auth.currentUser!!.delete().addOnSuccessListener {
                                                                val intent = Intent(activity, LoginActivity::class.java)
                                                                startActivity(intent)
                                                            }
                                                        }
                                                    }
                                                }
                                        }
                                    }else{
                                        firestore!!.collection("post").document(document.id).delete()
                                        i++
                                    }
                                }

                            }




                        }

                    Toast.makeText(context,"삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("아니요"){
                        dialog,_->
                    dialog.dismiss()
                }
                .create()
                .show()



        }
        layout_myPage_license.setOnClickListener {
            val intent = Intent(context, LicenseActivity::class.java)
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