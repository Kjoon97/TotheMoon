package org.techtown.wishmatching.Mypage

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.internal.bind.TypeAdapters.URI
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.techtown.wishmatching.Authentication
import org.techtown.wishmatching.Database.ContentDTO
import org.techtown.wishmatching.MainActivity
import org.techtown.wishmatching.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level.parse

class EditProfileActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM=0
    var storage : FirebaseStorage? = null
    var photoUri: Uri? = null // 이미지 URI 담을 수 있음
    var alreadyPhotouri: Uri? = null
    var firestore : FirebaseFirestore? = null
    val myuid = FirebaseAuth.getInstance().uid
    lateinit var realtimedb : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        supportActionBar?.title = "프로필 변경하기"
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        firestore!!.collection("user")
            .whereEqualTo("uid", Authentication.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    val image = storage!!.getReferenceFromUrl(document.data["imageUrl"].toString())
                    displayImageRef(image, myprofile_img)
                    image.downloadUrl.addOnSuccessListener {uri ->
                        photoUri = uri
                        alreadyPhotouri = uri
                    }
                }
            }

        myprofile_imgChange.setOnClickListener{    // 이미지 등록 버튼
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type ="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }

        myprofile_doublecheck.setOnClickListener { //닉네임 중복 확인 버튼
            if(myprofile_doublecheck.text.toString() == "다시입력"){
                myprofile_nickname.isEnabled = true
                myprofile_doublecheck.text = "중복확인"
                return@setOnClickListener
            }
            if(myprofile_nickname.text.toString() == null){
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else{
                firestore!!.collection("user")
                    .whereEqualTo("nickname", myprofile_nickname.text.toString()) //uid
                    .get()
                    .addOnSuccessListener { documents->
                        if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
                            Toast.makeText(this,"사용가능합니다", Toast.LENGTH_SHORT).show()
                            myprofile_doublecheck.text = "다시입력"
                            myprofile_nickname.isEnabled = false
                        } else{                        // 그게 아니라면 메인액티비티로 이동
                            Toast.makeText(this, "아이디가 중복됩니다", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        myfinish_btn.setOnClickListener {// 완료버튼

            var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())//파일이름 입력해주는 코드 - 이름이 중복 설정되지않도록 파일명을 날짜로
            var imageFileName = "IMAGE_"+timestamp+"_.png"
            var storageRef =storage?.reference?.child("user")?.child(imageFileName)

            if(photoUri == null){
                    Toast.makeText(this,"이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            else if(myprofile_nickname.isEnabled == true){
                    Toast.makeText(this, "닉네임 중복을 확인해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
            }else{
                if(photoUri == alreadyPhotouri){   // 프로필 사진 갱신 안하고 닉네임만 변경했을 때
                    realtimedb = Firebase.database.reference  //리얼타임 디비
                    firestore!!.collection("user")
                        .whereEqualTo("uid", Authentication.auth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                firestore!!.collection("user").document(document.id).update(
                                    mapOf(
                                        "nickname" to myprofile_nickname.text.toString(),
                                    )
                                )
                            }
                        }
                    if (myuid != null) {   //리얼타임디비 username 변경
                        realtimedb.child("users").child(myuid).child("username").setValue(myprofile_nickname.text.toString())
                    }

                }
                else{   // 프로필 사진 갱신하기위해 photoUri를 새로 다운받고, 닉네임도 변경했을 때
                    realtimedb = Firebase.database.reference   //리얼타임 디비
                    storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            firestore!!.collection("user")
                                .whereEqualTo("uid", Authentication.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { documents->
                                    for(document in documents){
                                        firestore!!.collection("user").document(document.id).update(mapOf(
                                            "nickname" to myprofile_nickname.text.toString(),
                                            "imageUrl" to uri.toString()
                                        ))
                                    }
                                }
                            if (myuid != null) {  //리얼 타임 디비의 usernaem, profileImageUrl변경
                                realtimedb.child("users").child(myuid).child("username").setValue(myprofile_nickname.text.toString())
                                realtimedb.child("users").child(myuid).child("profileImageUrl").setValue(uri.toString())
                            }
                        }
                    }
                }
                Toast.makeText(this, "수정하였습니다.", Toast.LENGTH_SHORT).show()
                finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==PICK_IMAGE_FROM_ALBUM)
            if(resultCode == Activity.RESULT_OK){  //사진을 선택했을 때 이미지의 경로가 이쪽으로 넘어옴
                photoUri = data?.data    //경로담기
                myprofile_img.setImageURI(photoUri)   //선택한 이미지로 변경
            }else{  //취소버튼 눌렀을 때 작동하는 부분
                finish()  //취소했을 때는 액티비티 그냥 취소
            }
    }


}
