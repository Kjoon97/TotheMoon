package org.techtown.wishmatching.Mypage.DealSituation

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.provider.PicassoProvider
import kotlinx.android.synthetic.main.activity_my_item_more_info.*
import org.techtown.wishmatching.MyUndoListener
import org.techtown.wishmatching.R

class MyItemMoreInfoActivity : AppCompatActivity() {
    var firestore : FirebaseFirestore? = null
    var storage : FirebaseStorage? = null
    var goodsId : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_item_more_info)


        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val intent = intent
        goodsId = intent.getStringExtra("doc_id")      //물품 아이디를 인텐트를 통해 받아옴


        firestore!!.collection("post")  //물품 아이디를 바탕으로 post쿼리 조회
            .whereEqualTo("documentId", goodsId)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    tv_itemMoreInfo_category.text = document.data["category"].toString()    //카테고리
                    tv_itemMoreInfo_name.text = document.data["title"].toString()           //물품 이름
                    tv_itemMoreInfo_description.text = document.data["content"].toString()  //물품 설명
                    tv_itemMoreInfo_dateValue.text = document.data["date"].toString()
                    if(document.data["imageUrl"].toString() == null  ){
                        img_itemMoreInfo_img1.visibility= View.INVISIBLE
                        img_itemMoreInfo_img2.visibility= View.INVISIBLE
                        img_itemMoreInfo_img3.visibility= View.INVISIBLE
                        img_itemMoreInfo_img4.visibility= View.INVISIBLE
                        img_itemMoreInfo_img5.visibility= View.INVISIBLE
                    }
                    else if(document.data["imageUrl2"].toString() == null  ){
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_itemMoreInfo_img1)     //물품 이미지
                        img_itemMoreInfo_img2.visibility= View.INVISIBLE
                        img_itemMoreInfo_img3.visibility= View.INVISIBLE
                        img_itemMoreInfo_img4.visibility= View.INVISIBLE
                        img_itemMoreInfo_img5.visibility= View.INVISIBLE

                    }
                    else if(document.data["imageUrl3"].toString() == null  ){
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_itemMoreInfo_img1)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl2"].toString())
                            .into(img_itemMoreInfo_img2)     //물품 이미지
                        img_itemMoreInfo_img3.visibility= View.INVISIBLE
                        img_itemMoreInfo_img4.visibility= View.INVISIBLE
                        img_itemMoreInfo_img5.visibility= View.INVISIBLE
                    }
                    else if(document.data["imageUrl4"].toString() == null  ){
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_itemMoreInfo_img1)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl2"].toString())
                            .into(img_itemMoreInfo_img2)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl3"].toString())
                            .into(img_itemMoreInfo_img3)     //물품 이미지
                        img_itemMoreInfo_img4.visibility= View.INVISIBLE
                        img_itemMoreInfo_img5.visibility= View.INVISIBLE
                    }
                    else if(document.data["imageUrl5"].toString() == null  ){
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_itemMoreInfo_img1)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl2"].toString())
                            .into(img_itemMoreInfo_img2)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl3"].toString())
                            .into(img_itemMoreInfo_img3)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl4"].toString())
                            .into(img_itemMoreInfo_img4)     //물품 이미지
                        img_itemMoreInfo_img5.visibility= View.INVISIBLE
                    }
                    else {
                        PicassoProvider.get().load(document.data["imageUrl"].toString())
                            .into(img_itemMoreInfo_img1)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl2"].toString())
                            .into(img_itemMoreInfo_img2)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl3"].toString())
                            .into(img_itemMoreInfo_img3)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl4"].toString())
                            .into(img_itemMoreInfo_img4)     //물품 이미지
                        PicassoProvider.get().load(document.data["imageUrl5"].toString())
                            .into(img_itemMoreInfo_img5)     //물품 이미지
                    }
                    firestore!!.collection("user")  //동네 정보를 받아와야하기 때문에 user db 쿼리 조회(post db의 uid를 바탕으로)
                        .whereEqualTo("uid", document.data["uid"].toString())
                        .get()
                        .addOnSuccessListener { documents->
                            for(document in documents){
                                tv_itemMoreInfo_areaValue.text = document.data["area"].toString()   //동네네
                            }
                        }
                }
            }

        btn_itemMoreInfo_left.setOnClickListener {
            vlf_itemMoreInfo_imglist.showPrevious()
        }
        btn_itemMoreInfo_right.setOnClickListener {
            vlf_itemMoreInfo_imglist.showNext()
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val state = intent.getStringExtra("state")
        if(state == "doing"){
            var mInflater = menuInflater
            mInflater.inflate(R.menu.edit_item_menu,menu)
            return true
        } else{
            return false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_edit -> {
                val intent = Intent(this, EditItemInfoActivity::class.java)
                intent.putExtra("doc_id", goodsId)
                startActivity(intent)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }


}