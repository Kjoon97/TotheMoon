package org.techtown.wishmatching

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import org.techtown.wishmatching.Database.ContentDTO
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM=0  //request code
    var storage : FirebaseStorage? = null
    var photoUri: Uri? = null // 이미지 URI 담을 수 있음
    var auth: FirebaseAuth? = null   // 유저의 정보를 가져오기 위함
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        storage = FirebaseStorage.getInstance() //스토리지 초기화
        auth = FirebaseAuth.getInstance()            //초기화
        firestore = FirebaseFirestore.getInstance()  //초기화

        photo_btn.setOnClickListener{
//            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
//            }
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type ="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }
        profile_upload_button.setOnClickListener{
            contentUpload()
        }
    }
    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())//파일이름 입력해주는 코드 - 이름이 중복 설정되지않도록 파일명을 날짜로
        var imageFileName = "IMAGE_"+timestamp+"_.png"

        var storageRef =storage?.reference?.child("Users")?.child(imageFileName)

        //callback 방식
        //파일 업로드 //데이터베이스를 입력해주는코드
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri->
                var contentDTO = ContentDTO()
                contentDTO.imageUrl = uri.toString()    // 받아온 주소를 저장
                contentDTO.uid = auth?.currentUser?.uid // 유저의 uid 저장
                contentDTO.userId = auth?.currentUser?.email //
                contentDTO.Nickname = editTextNickname.text.toString()
                contentDTO.timestamp =System.currentTimeMillis()
                firestore?.collection("Users")?.document()?.set(contentDTO)
                setResult(Activity.RESULT_OK)
                finish()
            }
        } //파일업로드 성공 시 이미지 주소를 받아옴 ,받아오자마자 데이터 모델을 만듦듦

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==PICK_IMAGE_FROM_ALBUM)
            if(resultCode == Activity.RESULT_OK){  //사진을 선택했을 때 이미지의 경로가 이쪽으로 넘어옴
                photoUri = data?.data    //경로담기
                Imageview_Main_Myprofile.setImageURI(photoUri)   //선택한 이미지로 변경
            }else{  //취소버튼 눌렀을 때 작동하는 부분
                finish()  //취소했을 때는 액티비티 그냥 취소
            }
    }
}