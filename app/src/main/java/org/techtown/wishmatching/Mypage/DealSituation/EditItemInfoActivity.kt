package org.techtown.wishmatching.Mypage.DealSituation

import android.app.Activity
import android.content.Intent

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.provider.PicassoProvider
import kotlinx.android.synthetic.main.activity_edit_item_info.*
import org.techtown.wishmatching.R
import java.text.SimpleDateFormat
import java.util.*

class EditItemInfoActivity : AppCompatActivity() {
    var firestore : FirebaseFirestore? = null
    var storage : FirebaseStorage? = null
    var selectedCategory : String? = ""
    var currentImgIndex = 0
    var changedImageList = arrayOfNulls<Uri>(5)
    lateinit var imgArray : Array<ImageView>
    var columnList = arrayOf("imageUrl", "imageUrl2", "imageUrl3", "imageUrl4", "imageUrl5")
    var goodsId : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item_info)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        imgArray = arrayOf(img_editItemInfo_img1, img_editItemInfo_img2, img_editItemInfo_img3, img_editItemInfo_img4, img_editItemInfo_img5)
        val intent = intent
        goodsId = intent.getStringExtra("doc_id")


        for(index in imgArray.indices){
            imgArray[index].setOnClickListener {
                currentImgIndex = index
                var intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(intent,1)
            }
        }

        val list = listOf<String>("디지털기기", "가구/인테리어", "식품", "스포츠/레저", "남성잡화",
            "여성잡화", "게임/취미", "뷰티/미용", "반려동물용품", "도서/티켓/음반", "유아용품", "기타")
        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        spinner_editItemInfo_categoryValue.adapter = adapter


        spinner_editItemInfo_categoryValue.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCategory = list[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


        firestore!!.collection("post")  //물품 아이디를 바탕으로 post쿼리 조회
            .whereEqualTo("documentId", goodsId)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    edt_editItemInfo_titleValue.setText(document.data["title"].toString())
                    edt_editItemInfo_descriptionValue.setText(document.data["content"].toString())
                    for(index in list.indices){
                        if(list[index] == document.data["category"]){
                            spinner_editItemInfo_categoryValue.setSelection(index)
                        }
                    }


                    for(index in columnList.indices){
                        if(document.data[columnList[index]].toString() != "null"){
                            PicassoProvider.get().load(document.data[columnList[index]].toString())
                                .into(imgArray[index])
                        } else{
                            PicassoProvider.get().load(R.drawable.camera_moto)
                                .into(imgArray[index])
                        }
                    }
                }
            }

        btn_editItemInfo_left.setOnClickListener {
            vlf_editItemInfo_imglist.showPrevious()
        }
        btn_editItemInfo_right.setOnClickListener {
            vlf_editItemInfo_imglist.showNext()
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val mInflater = menuInflater
        mInflater.inflate(R.menu.menu_addpostactivity,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_addpost -> {
                Upload(0)
//                val intent = Intent(this, DealSituActivity::class.java)
//                startActivity(intent)
                finish()

            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun Upload(i: Int){
        if(i ==5){
            firestore!!.collection("post").document(goodsId!!).get().addOnSuccessListener { document->
                if(changedImageList[0] == null && document["imageUrl"] !== "null"){
                    changedImageList[0] = document["imageUrl"].toString().toUri()
                }
                if(changedImageList[1] == null && document["imageUrl"] !== "null"){
                    changedImageList[1] = document["imageUrl2"].toString().toUri()
                }
                if(changedImageList[2] == null && document["imageUrl"] !== "null"){
                    changedImageList[2] = document["imageUrl3"].toString().toUri()
                }
                if(changedImageList[3] == null && document["imageUrl"] !== "null"){
                    changedImageList[3] = document["imageUrl4"].toString().toUri()
                }
                if(changedImageList[4] == null && document["imageUrl"] !== "null") {
                    changedImageList[4] = document["imageUrl5"].toString().toUri()
                }
                val today = SimpleDateFormat("yyyy-MM-dd").format(Date())
                firestore!!.collection("post").document(goodsId!!)
                    .update(mapOf(
                        "category" to selectedCategory,
                        "content" to edt_editItemInfo_descriptionValue.text.toString(),
                        "imageUrl" to changedImageList[0].toString(),
                        "imageUrl2" to changedImageList[1].toString(),
                        "imageUrl3" to changedImageList[2].toString(),
                        "imageUrl4" to changedImageList[3].toString(),
                        "imageUrl5" to changedImageList[4].toString(),
                        "title" to edt_editItemInfo_titleValue.text.toString(),
                        "date" to today
                    ))
            }
            return;
        }

        if(changedImageList[i] !== null){
            var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            var imageFileName = "IMAGE_"+timestamp+"_.png"
            var storageRef =storage?.reference?.child("Post")?.child(imageFileName)
            storageRef?.putFile(changedImageList[i]!!)?.addOnSuccessListener {
                storageRef?.downloadUrl?.addOnSuccessListener { uri->
                    changedImageList[i] = uri
                    Upload(i+1)
                }
            }
        }else{
            Upload(i+1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==1)
            if(resultCode == Activity.RESULT_OK){
                imgArray[currentImgIndex]!!.setImageURI(data?.data)
                changedImageList[currentImgIndex] = data?.data
            }else{
                finish()
            }
    }
}