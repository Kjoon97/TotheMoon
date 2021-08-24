package org.techtown.wishmatching

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*
import org.techtown.wishmatching.Database.PostDTO
import java.text.SimpleDateFormat
import java.util.*

class AddPostActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM=0  //request code
    var storage : FirebaseStorage? = null
    var photoUri: Uri? = null // 이미지 URI 담을 수 있음
    var auth: FirebaseAuth? = null   // 유저의 정보를 가져오기 위함
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
    var selectedCategory : String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)



        storage = FirebaseStorage.getInstance() //스토리지 초기화
        auth = FirebaseAuth.getInstance()            //초기화
        firestore = FirebaseFirestore.getInstance()  //초기화

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val list = listOf<String>("디지털기기", "가구/인테리어", "식품", "스포츠/레저", "남성잡화",
        "여성잡화", "게임/취미", "뷰티/미용", "반려동물용품", "도서/티켓/음반", "유아용품", "기타")
        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        spinner_category.adapter = adapter

        spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2) {
                    0 -> selectedCategory = list[0]
                    1 -> selectedCategory = list[1]
                    2 -> selectedCategory = list[2]
                    3 -> selectedCategory = list[3]
                    4 -> selectedCategory = list[4]
                    5 -> selectedCategory = list[5]
                    6 -> selectedCategory = list[6]
                    7 -> selectedCategory = list[7]
                    8 -> selectedCategory = list[8]
                    9 -> selectedCategory = list[9]
                    10 -> selectedCategory = list[10]
                    11 -> selectedCategory = list[11]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        // 게시글에 올릴 사진 선택 버튼
        img_moreInfo_picture.setOnClickListener{
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type ="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)


        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu_addpostactivity,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_addpost -> {
                contentUpload()
//                액티비티 실행

//                startActivity(Intent(this,MainActivity::class.java))

//                Toast.makeText(this,"작성 완료",Toast.LENGTH_LONG).show()

//                var home : View = findViewById(R.id.home_layout)
//                Snackbar.make(home, "snack", Snackbar.LENGTH_LONG).show()

//                val mySnackbar = Snackbar.make(findViewById(R.id.home2),
//                    "작성 완료", Snackbar.LENGTH_SHORT)
//                mySnackbar.setAction("닫기", MyUndoListener())
//                mySnackbar.setTextColor(Color.WHITE)
//                mySnackbar.show()



            }
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==PICK_IMAGE_FROM_ALBUM)
            if(resultCode == Activity.RESULT_OK){  //사진을 선택했을 때 이미지의 경로가 이쪽으로 넘어옴
                photoUri = data?.data    //경로담기
                img_moreInfo_picture.setImageURI(photoUri)   //선택한 이미지로 변경
            }else{  //취소버튼 눌렀을 때 작동하는 부분
                val intent = Intent(this, AddPostActivity::class.java)
                startActivity(intent)
                finish()  //취소했을 때는 액티비티 그냥 취소
            }
    }
    fun contentUpload(){ // 파이어베이스 로드

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())//파일이름 입력해주는 코드 - 이름이 중복 설정되지않도록 파일명을 날짜로
        var imageFileName = "IMAGE_"+timestamp+"_.png"

        var storageRef =storage?.reference?.child("Post")?.child(imageFileName)

        //callback 방식
        //파일 업로드 //데이터베이스를 입력해주는코드
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri->
                firestore!!.collection("post")
                    .whereEqualTo("uid", auth?.uid)
                    .get()
                    .addOnSuccessListener { documents->
                        var collRef = firestore!!.collection("post")
                        var docReference : String = collRef.document().id

                        firestore?.collection("post")?.document("${docReference}")
                            ?.set(PostDTO("${docReference}","${uri.toString()}", "${auth?.uid}", editText_title.text.toString(), editText_content.text.toString(), "${selectedCategory}","doingDeal"))
                        var intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)

                    }
                setResult(Activity.RESULT_OK)

            }
        } //파일업로드 성공 시 이미지 주소를 받아옴 ,받아오자마자 데이터 모델을 만듦듦
    }
    class MyUndoListener : View.OnClickListener {

        override fun onClick(v: View) {
            // Code to undo the user's last action
        }
    }
}