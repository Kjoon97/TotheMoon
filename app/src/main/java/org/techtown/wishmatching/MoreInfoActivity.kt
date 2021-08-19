package org.techtown.wishmatching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.provider.PicassoProvider
import kotlinx.android.synthetic.main.activity_more_info.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class MoreInfoActivity : AppCompatActivity() {
    var firestore : FirebaseFirestore? = null
    var storage : FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val intent = intent
        val goodsId = intent.getStringExtra("doc_id")      //물품 아이디를 인텐트를 통해 받아옴

        firestore!!.collection("post")  //물품 아이디를 바탕으로 post쿼리 조회
            .whereEqualTo("documentId", goodsId)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    tv_moreInfo_category.text = document.data["category"].toString()    //카테고리
                    tv_moreInfo_name.text = document.data["title"].toString()           //물품 이름
                    tv_moreInfo_description.text = document.data["content"].toString()  //물품 설명
                    PicassoProvider.get().load(document.data["imageUrl"].toString()).into(img_moreInfo_picture)     //물품 이미지
                    firestore!!.collection("user")  //동네 정보를 받아와야하기 때문에 user db 쿼리 조회(post db의 uid를 바탕으로)
                        .whereEqualTo("uid", document.data["uid"].toString())
                        .get()
                        .addOnSuccessListener { documents->
                            for(document in documents){
                                tv_moreInfo_areaValue.text = document.data["area"].toString()   //동네네
                           }
                        }
                }
            }

        //만약 좋아요를 이미 누른 상태이면 좋아요 색이 빨간색이 되어야함

        btn_moreInfo_like.setOnClickListener {
            //좋아요 버튼 이벤트 처리

        }
    }
}