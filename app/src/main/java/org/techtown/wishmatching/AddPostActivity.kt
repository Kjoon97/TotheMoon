package org.techtown.wishmatching

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
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

    private var context: Context? = null
    var PICK_IMAGE_MULTIPLE = 1
    lateinit var imagePath: String
    var imagesPathList: MutableList<String> = arrayListOf()
    var imagesUrlList: MutableList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)



        storage = FirebaseStorage.getInstance() //스토리지 초기화
        auth = FirebaseAuth.getInstance()            //초기화
        firestore = FirebaseFirestore.getInstance()  //초기화

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // 게시글에 올릴 사진 선택 버튼
        img_moreInfo_picture.setOnClickListener{
//            var photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type ="image/*"
//            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_PICK
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture")
                , PICK_IMAGE_MULTIPLE
            )
            Toast.makeText(this,"최대 5장까지 선택가능합니다.",Toast.LENGTH_LONG).show()


        }
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode ==PICK_IMAGE_FROM_ALBUM)
//            if(resultCode == Activity.RESULT_OK){  //사진을 선택했을 때 이미지의 경로가 이쪽으로 넘어옴
//                photoUri = data?.data    //경로담기
//                img_moreInfo_picture.setImageURI(photoUri)   //선택한 이미지로 변경
//            }else{  //취소버튼 눌렀을 때 작동하는 부분
//                val intent = Intent(this, AddPostActivity::class.java)
//                startActivity(intent)
//                finish()  //취소했을 때는 액티비티 그냥 취소
//            }
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != data) {

            if(data.clipData!=null) {
                var mclipdata = data.clipData


                if (mclipdata?.itemCount == 0){
                    Toast.makeText(this,"사진을 선택해주세요.",Toast.LENGTH_LONG).show()
                }
                if (mclipdata?.itemCount == 1){
                    val data1 :Uri? = mclipdata?.getItemAt(0)?.uri
                    imageview_selected1.setImageURI(data1)
                    imagesPathList.add(data1.toString())
                }
                if (mclipdata?.itemCount == 2){
                    val data1 :Uri? = mclipdata?.getItemAt(0)?.uri
                    val data2 :Uri? = mclipdata?.getItemAt(1)?.uri
                    imageview_selected1.setImageURI(data1)
                    imageview_selected2.setImageURI(data2)
                    imagesPathList.add(data1.toString())
                    imagesPathList.add(data2.toString())
                }
                if (mclipdata?.itemCount == 3){
                    val data1 :Uri? = mclipdata?.getItemAt(0)?.uri
                    val data2 :Uri? = mclipdata?.getItemAt(1)?.uri
                    val data3 :Uri? = mclipdata?.getItemAt(2)?.uri
                    imageview_selected1.setImageURI(data1)
                    imageview_selected2.setImageURI(data2)
                    imageview_selected3.setImageURI(data3)
                    imagesPathList.add(data1.toString())
                    imagesPathList.add(data2.toString())
                    imagesPathList.add(data3.toString())
                }
                if (mclipdata?.itemCount == 4){
                    val data1 :Uri? = mclipdata?.getItemAt(0)?.uri
                    val data2 :Uri? = mclipdata?.getItemAt(1)?.uri
                    val data3 :Uri? = mclipdata?.getItemAt(2)?.uri
                    val data4 :Uri? = mclipdata?.getItemAt(3)?.uri
                    imageview_selected1.setImageURI(data1)
                    imageview_selected2.setImageURI(data2)
                    imageview_selected3.setImageURI(data3)
                    imageview_selected4.setImageURI(data4)
                    imagesPathList.add(data1.toString())
                    imagesPathList.add(data2.toString())
                    imagesPathList.add(data3.toString())
                    imagesPathList.add(data4.toString())
                }
                if (mclipdata?.itemCount == 5){
                    val data1 :Uri? = mclipdata?.getItemAt(0)?.uri
                    val data2 :Uri? = mclipdata?.getItemAt(1)?.uri
                    val data3 :Uri? = mclipdata?.getItemAt(2)?.uri
                    val data4 :Uri? = mclipdata?.getItemAt(3)?.uri
                    val data5 :Uri? = mclipdata?.getItemAt(4)?.uri
                    imageview_selected1.setImageURI(data1)
                    imageview_selected2.setImageURI(data2)
                    imageview_selected3.setImageURI(data3)
                    imageview_selected4.setImageURI(data4)
                    imageview_selected5.setImageURI(data5)
                    imagesPathList.add(data1.toString())
                    imagesPathList.add(data2.toString())
                    imagesPathList.add(data3.toString())
                    imagesPathList.add(data4.toString())
                    imagesPathList.add(data5.toString())
                }
                else if((mclipdata?.itemCount!! > 5)){
                    Toast.makeText(this,"사진은 최대 5장 까지만 선택가능합니다.",Toast.LENGTH_LONG).show()
                }
            }
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
                contentUpload(imagesPathList.count())

                val mySnackbar = Snackbar.make(findViewById(R.id.home2),
                    "등록중입니다.", Snackbar.LENGTH_SHORT)
                mySnackbar.setAction("닫기", MyUndoListener())
                mySnackbar.setTextColor(Color.WHITE)
                mySnackbar.show()



            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun contentUpload(num : Int){ // 파이어베이스 로드

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())//파일이름 입력해주는 코드 - 이름이 중복 설정되지않도록 파일명을 날짜로
        var imageFileName = "IMAGE_"+timestamp+"_.png"
        var imageFileName2 = "IMAGE_"+timestamp+"1_.png"
        var imageFileName3 = "IMAGE_"+timestamp+"2_.png"
        var imageFileName4 = "IMAGE_"+timestamp+"3_.png"
        var imageFileName5 = "IMAGE_"+timestamp+"4_.png"
        // 이미지 5개에 대해서 중복 방지

        val today = SimpleDateFormat("yyyy-MM-dd").format(Date())


        var storageRef =storage?.reference?.child("Post")?.child(imageFileName)
        var storageRef2 =storage?.reference?.child("Post")?.child(imageFileName2)
        var storageRef3 =storage?.reference?.child("Post")?.child(imageFileName3)
        var storageRef4 =storage?.reference?.child("Post")?.child(imageFileName4)
        var storageRef5 =storage?.reference?.child("Post")?.child(imageFileName5)


        //callback 방식
        //파일 업로드 //데이터베이스를 입력해주는코드
        if(num == 0) {
            var collRef = firestore!!.collection("post")
            var docReference: String = collRef.document().id
            firestore?.collection("post")?.document("${docReference}")
                ?.set(
                    PostDTO(
                        "${docReference}",
                        "null", "null", "null",
                        "null", "null", "${auth?.uid}",
                        editText_title.text.toString(), editText_content.text.toString(),
                        "${selectedCategory}", "doingDeal",
                        today
                    )
                )
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if(num == 1) {
            storageRef?.putFile(imagesPathList.get(0).toUri()!!)?.addOnSuccessListener {
                storageRef?.downloadUrl?.addOnSuccessListener { uri1->
                            firestore!!.collection("post")
                                .whereEqualTo("uid", auth?.uid)
                                .get()
                                .addOnSuccessListener { documents ->
                                    var collRef = firestore!!.collection("post")
                                    var docReference: String = collRef.document().id

                                    firestore?.collection("post")?.document("${docReference}")
                                        ?.set(
                                            PostDTO(
                                                "${docReference}",
                                                uri1.toString(), "null", "null",
                                                "null", "null", "${auth?.uid}",
                                                editText_title.text.toString(), editText_content.text.toString(),
                                                "${selectedCategory}", "doingDeal",
                                                today
                                            )
                                        )
                                    var intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)

                                }
                            setResult(Activity.RESULT_OK)
                }

            }
        }
        if(num == 2) {
            storageRef?.putFile(imagesPathList.get(0).toUri()!!)?.addOnSuccessListener {
                storageRef?.downloadUrl?.addOnSuccessListener { uri1->
                    storageRef2?.putFile(imagesPathList.get(1).toUri()!!)?.addOnSuccessListener {
                        storageRef2?.downloadUrl?.addOnSuccessListener { uri2->
                                    firestore!!.collection("post")
                                        .whereEqualTo("uid", auth?.uid)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            var collRef = firestore!!.collection("post")
                                            var docReference: String = collRef.document().id

                                            firestore?.collection("post")?.document("${docReference}")
                                                ?.set(
                                                    PostDTO(
                                                        "${docReference}",
                                                        uri1.toString(), uri2.toString(), "null",
                                                        "null", "null", "${auth?.uid}",
                                                        editText_title.text.toString(), editText_content.text.toString(),
                                                        "${selectedCategory}", "doingDeal",
                                                        today
                                                    )
                                                )
                                            var intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)

                                        }
                                    setResult(Activity.RESULT_OK)
                        }
                    }
                }

            }
        }
        if(num == 3) {
            storageRef?.putFile(imagesPathList.get(0).toUri()!!)?.addOnSuccessListener {
                storageRef?.downloadUrl?.addOnSuccessListener { uri1->
                    storageRef2?.putFile(imagesPathList.get(1).toUri()!!)?.addOnSuccessListener {
                        storageRef2?.downloadUrl?.addOnSuccessListener { uri2->
                            storageRef3?.putFile(imagesPathList.get(2).toUri()!!)?.addOnSuccessListener {
                                storageRef3?.downloadUrl?.addOnSuccessListener { uri3->
                                            firestore!!.collection("post")
                                                .whereEqualTo("uid", auth?.uid)
                                                .get()
                                                .addOnSuccessListener { documents ->
                                                    var collRef = firestore!!.collection("post")
                                                    var docReference: String = collRef.document().id

                                                    firestore?.collection("post")?.document("${docReference}")
                                                        ?.set(
                                                            PostDTO(
                                                                "${docReference}",
                                                                uri1.toString(), uri2.toString(), uri3.toString(),
                                                                "null", "null", "${auth?.uid}",
                                                                editText_title.text.toString(), editText_content.text.toString(),
                                                                "${selectedCategory}", "doingDeal",
                                                                today
                                                            )
                                                        )
                                                    var intent = Intent(this, MainActivity::class.java)
                                                    startActivity(intent)

                                                }
                                            setResult(Activity.RESULT_OK)
                                }
                            }
                        }
                    }
                }

            }
        }
        if(num == 4) {
            storageRef?.putFile(imagesPathList.get(0).toUri()!!)?.addOnSuccessListener {
                storageRef?.downloadUrl?.addOnSuccessListener { uri1->
                    storageRef2?.putFile(imagesPathList.get(1).toUri()!!)?.addOnSuccessListener {
                        storageRef2?.downloadUrl?.addOnSuccessListener { uri2->
                            storageRef3?.putFile(imagesPathList.get(2).toUri()!!)?.addOnSuccessListener {
                                storageRef3?.downloadUrl?.addOnSuccessListener { uri3->
                                    storageRef4?.putFile(imagesPathList.get(3).toUri()!!)?.addOnSuccessListener {
                                        storageRef4?.downloadUrl?.addOnSuccessListener { uri4->
                                                    firestore!!.collection("post")
                                                        .whereEqualTo("uid", auth?.uid)
                                                        .get()
                                                        .addOnSuccessListener { documents ->
                                                            var collRef = firestore!!.collection("post")
                                                            var docReference: String = collRef.document().id

                                                            firestore?.collection("post")?.document("${docReference}")
                                                                ?.set(
                                                                    PostDTO(
                                                                        "${docReference}",
                                                                        uri1.toString(), uri2.toString(), uri3.toString(),
                                                                        uri4.toString(), "null", "${auth?.uid}",
                                                                        editText_title.text.toString(), editText_content.text.toString(),
                                                                        "${selectedCategory}", "doingDeal",
                                                                        today
                                                                    )
                                                                )
                                                            var intent = Intent(this, MainActivity::class.java)
                                                            startActivity(intent)

                                                        }
                                                    setResult(Activity.RESULT_OK)


                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
//        gs://wishmatching-ed07a.appspot.com/Post/IMAGE_20210824_144421_.png
//        gs://wishmatching-ed07a.appspot.com/Post/IMAGE_20210825_024947_.png
        if(num == 5) {
            storageRef?.putFile(imagesPathList.get(0).toUri()!!)?.addOnSuccessListener {
                storageRef?.downloadUrl?.addOnSuccessListener { uri1->
                    storageRef2?.putFile(imagesPathList.get(1).toUri()!!)?.addOnSuccessListener {
                        storageRef2?.downloadUrl?.addOnSuccessListener { uri2->
                            storageRef3?.putFile(imagesPathList.get(2).toUri()!!)?.addOnSuccessListener {
                                storageRef3?.downloadUrl?.addOnSuccessListener { uri3->
                                    storageRef4?.putFile(imagesPathList.get(3).toUri()!!)?.addOnSuccessListener {
                                        storageRef4?.downloadUrl?.addOnSuccessListener { uri4->
                                            storageRef5?.putFile(imagesPathList.get(4).toUri()!!)?.addOnSuccessListener {
                                                storageRef5?.downloadUrl?.addOnSuccessListener { uri5->
                                                    firestore!!.collection("post")
                                                        .whereEqualTo("uid", auth?.uid)
                                                        .get()
                                                        .addOnSuccessListener { documents ->
                                                            var collRef = firestore!!.collection("post")
                                                            var docReference: String = collRef.document().id

                                                            firestore?.collection("post")?.document("${docReference}")
                                                                ?.set(
                                                                    PostDTO(
                                                                        "${docReference}",
                                                                        uri1.toString(), uri2.toString(), uri3.toString(),
                                                                        uri4.toString(), uri5.toString(), "${auth?.uid}",
                                                                        editText_title.text.toString(), editText_content.text.toString(),
                                                                        "${selectedCategory}", "doingDeal",
                                                                        today
                                                                    )
                                                                )
                                                            var intent = Intent(this, MainActivity::class.java)
                                                            startActivity(intent)

                                                        }
                                                    setResult(Activity.RESULT_OK)
                                                }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }





        }
//            firestore!!.collection("post")
//                .whereEqualTo("uid", auth?.uid)
//                .get()
//                .addOnSuccessListener { documents ->
//                    var collRef = firestore!!.collection("post")
//                    var docReference: String = collRef.document().id
//
//                    firestore?.collection("post")?.document("${docReference}")
//                        ?.set(
//                            PostDTO(
//                                "${docReference}",
//                                imagesUrlList.get(0), imagesUrlList.get(1), imagesUrlList.get(2),
//                                imagesUrlList.get(3), imagesUrlList.get(4), "${auth?.uid}",
//                                editText_title.text.toString(), editText_content.text.toString(),
//                                "아직미정", "doingDeal"
//                            )
//                        )
//                    var intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//
//                }
//            setResult(Activity.RESULT_OK)
//        }

//        storageRef?.putFile(imagesPathList.get(0).toUri()!!)?.addOnSuccessListener {
//            storageRef.downloadUrl.addOnSuccessListener { uri->
//                firestore!!.collection("post")
//                    .whereEqualTo("uid", auth?.uid)
//                    .get()
//                    .addOnSuccessListener { documents->
//                        var collRef = firestore!!.collection("post")
//                        var docReference : String = collRef.document().id
//
//                        firestore?.collection("post")?.document("${docReference}")
//                            ?.set(PostDTO("${docReference}",
//                                imagesPathList.get(0),imagesPathList.get(1),imagesPathList.get(2),
//                                imagesPathList.get(3),imagesPathList.get(4), "${auth?.uid}",
//                                editText_title.text.toString(), editText_content.text.toString(),
//                                "아직미정","doingDeal"))
//                        var intent = Intent(this,MainActivity::class.java)
//                        startActivity(intent)
//
//                    }
//                setResult(Activity.RESULT_OK)
//
//            }
//        } //파일업로드 성공 시 이미지 주소를 받아옴 ,받아오자마자 데이터 모델을 만듦듦
    }
    class MyUndoListener : View.OnClickListener {

        override fun onClick(v: View) {
            // Code to undo the user's last action
        }
    }


