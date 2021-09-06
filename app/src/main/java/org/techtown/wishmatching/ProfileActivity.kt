package org.techtown.wishmatching

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.techtown.wishmatching.Database.ContentDTO
import org.techtown.wishmatching.RealtimeDB.User
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM=0  //request code
    var index = arrayOf(0,0)
    var storage : FirebaseStorage? = null
    var photoUri: Uri? = null // 이미지 URI 담을 수 있음
    var auth: FirebaseAuth? = null   // 유저의 정보를 가져오기 위함
    var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
    val city = arrayOf("지역","울산광역시", "충청남도", "서울특별시", "세종특별자치시", "전라북도", "경기도", "광주광역시", "인천광역시", "전라남도", "강원도", "충청북도", "대구광역시", "경상남도", "경상북도", "부산광역시", "제주특별자치도", "대전광역시")
    val innercity = arrayOf(arrayOf("세부 지역")
        ,arrayOf("동구", "울주군", "남구", "북구", "중구")
        ,arrayOf("청양군", "천안시동남구", "천안시", "태안군", "부여군", "예산군", "논산시", "서산시", "보령시", "금산군", "홍성군", "당진시", "아산시", "서천군", "계룡시", "공주시", "천안시서북구")
        ,arrayOf("중랑구", "서대문구", "구로구", "중구", "서초구", "강북구", "용산구", "도봉구", "노원구", "영등포구", "강동구", "성북구", "은평구", "광진구", "마포구", "동작구", "동대문구", "양천구", "강남구", "관악구", "송파구", "금천구", "종로구", "강서구", "성동구")
        ,arrayOf("세종시")
        ,arrayOf("김제시", "장수군", "순창군", "부안군", "무주군", "군산시", "남원시", "정읍시", "전주시덕진구", "진안군", "고창군", "전주시완산구", "완주군", "전주시", "익산시", "임실군")
        ,arrayOf("김포시", "안산시상록구", "동두천시", "오산시", "용인시", "성남시", "안양시", "용인시기흥구", "안양시동안구", "화성시", "광주시", "의왕시", "포천시", "수원시팔달구", "평택시", "수원시", "안산시", "양평군", "과천시", "고양시덕양구", "성남시수정구", "의정부시", "수원시권선구", "파주시", "양주시", "광명시", "가평군", "여주시", "안양시만안구", "연천군", "부천시", "하남시", "군포시", "수원시장안구", "안산시단원구", "용인시처인구", "고양시일산동구", "시흥시", "성남시중원구", "성남시분당구", "안성시", "고양시", "남양주시", "수원시영통구", "구리시", "이천시", "고양시일산서구", "용인시수지구")
        ,arrayOf("동구", "북구", "남구", "광산구", "서구")
        ,arrayOf("미추홀구", "강화군", "동구", "남동구", "부평구", "옹진군", "서구", "계양구", "연수구", "중구")
        ,arrayOf("광양시", "목포시", "장성군", "함평군", "화순군", "담양군", "무안군", "여수시", "해남군", "나주시", "영암군", "완도군", "순천시", "진도군", "고흥군", "보성군", "곡성군", "장흥군", "강진군", "영광군", "구례군", "신안군")
        ,arrayOf("횡성군", "삼척시", "인제군", "속초시", "동해시", "정선군", "평창군", "홍천군", "영월군", "춘천시", "원주시", "양구군", "강릉시", "화천군", "양양군", "태백시", "철원군", "고성군")
        ,arrayOf("청주시", "단양군", "괴산군", "진천군", "제천시", "영동군", "보은군", "청주시상당구", "음성군", "증평군", "청주시흥덕구", "청주시청원구", "충주시", "청주시서원구", "옥천군")
        ,arrayOf("달서구", "동구", "달성군", "북구", "수성구", "남구", "서구", "중구")
        ,arrayOf("거창군", "거제시", "사천시", "창원시성산구", "밀양시", "창원시마산회원구", "산청군", "양산시", "창녕군", "합천군", "의령군", "남해군", "창원시", "김해시", "창원시의창구", "창원시마산합포구", "함양군", "하동군", "통영시", "진주시", "고성군", "창원시진해구", "함안군")
        ,arrayOf("군위군", "상주시", "영양군", "청도군", "포항시북구", "영주시", "문경시", "영덕군", "김천시", "성주군", "경주시", "구미시", "포항시", "칠곡", "고령군", "포항시남구", "청송군", "영천시", "울진군","안동시", "의성군", "경산시", "울릉군", "예천군", "봉화군")
        ,arrayOf("금정구", "기장군", "수영구", "동구", "부산진구", "해운대구", "동래구", "연제구", "사상구", "북구", "강서구", "남구", "사하구", "서구", "영도구", "중구")
        ,arrayOf("제주시", "서귀포시")
        ,arrayOf("대덕구", "동구", "유성구", "서구", "중구"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        storage = FirebaseStorage.getInstance() //스토리지 초기화
        auth = FirebaseAuth.getInstance()            //초기화
        firestore = FirebaseFirestore.getInstance()  //초기화



        var adapter : ArrayAdapter<String>
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, city)
        spin_edtLocation_city.adapter = adapter



        spin_edtLocation_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                var adpt : ArrayAdapter<String>
                adpt = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item,innercity[p2])
                spin_edtLocation_innercity.adapter = adpt
                index[0] = p2

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        spin_edtLocation_innercity.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
               index[1] = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        btn_profile_previous.visibility = View.INVISIBLE

        btn_profile_imgChange.setOnClickListener{
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type ="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }


        btn_profile_next.setOnClickListener {// 완료버튼
            if(vf_profile_profile.currentView==layout_proflie_basicinfo){
                btn_profile_previous.visibility = View.VISIBLE
                btn_profile_next.text = "완료"
                vf_profile_profile.showNext()
            } else{

                if(photoUri == null){
                    Toast.makeText(this,"이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else if(index[0] == 0){
                    Toast.makeText(this,"지역을 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else if(edt_profile_nickname.isEnabled == true){
                    Toast.makeText(this, "닉네임 중복을 확인해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else{
                    contentUpload()
                    //액티비티 실행
                    Thread.sleep(300)
                    startActivity(Intent(this,MainActivity::class.java))
                }

            }
        }

        btn_profile_previous.setOnClickListener {
            vf_profile_profile.showPrevious()
            btn_profile_next.text = "다음"
            btn_profile_previous.visibility = View.INVISIBLE

        }

        btn_profile_doublecheck.setOnClickListener {
            if(btn_profile_doublecheck.text.toString() == "다시입력"){
                edt_profile_nickname.isEnabled = true
                btn_profile_doublecheck.text = "중복확인"
                return@setOnClickListener
            }
            if(edt_profile_nickname.text.toString() == null){
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else{
                firestore!!.collection("user")
                    .whereEqualTo("nickname", edt_profile_nickname.text.toString()) //uid
                    .get()
                    .addOnSuccessListener { documents->
                        if(documents.isEmpty){
                            Toast.makeText(this@ProfileActivity,"사용가능합니다", Toast.LENGTH_SHORT).show()
                            btn_profile_doublecheck.text = "다시입력"
                            edt_profile_nickname.isEnabled = false
                        } else{
                            Toast.makeText(this@ProfileActivity, "아이디가 중복됩니다", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(Authentication.auth !=null){
            firestore!!.collection("user")
                .whereEqualTo("uid", Authentication.auth!!.uid)
                .get()
                .addOnSuccessListener { documents->
                    if(!(documents.isEmpty)){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
        }
    }


    fun contentUpload(){ // 파이어베이스 로드

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())//파일이름 입력해주는 코드 - 이름이 중복 설정되지않도록 파일명을 날짜로
        var imageFileName = "IMAGE_"+timestamp+"_.png"

        var storageRef =storage?.reference?.child("user")?.child(imageFileName)

        //callback 방식
        //파일 업로드 //데이터베이스를 입력해주는코드
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri->
                var contentDTO = ContentDTO()
                contentDTO.uid = Authentication.auth.currentUser!!.uid
                contentDTO.imageUrl = uri.toString()
                contentDTO.nickname = edt_profile_nickname.text.toString()
                contentDTO.area = city[index[0]]+" "+innercity[index[0]][index[1]]
                contentDTO.userCategory1 = ""
                contentDTO.userCategory2 = ""
                contentDTO.userCategory3 = ""
                firestore?.collection("user")?.document()?.set(contentDTO)
                setResult(Activity.RESULT_OK)

                saveUserToFirebaseDatabase(uri.toString())
            }
        } //파일업로드 성공 시 이미지 주소를 받아옴 ,받아오자마자 데이터 모델을 만듦
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==PICK_IMAGE_FROM_ALBUM)
            if(resultCode == Activity.RESULT_OK){  //사진을 선택했을 때 이미지의 경로가 이쪽으로 넘어옴
                photoUri = data?.data    //경로담기
                img_profile_img.setImageURI(photoUri)   //선택한 이미지로 변경
            }else{  //취소버튼 눌렀을 때 작동하는 부분
                finish()  //취소했을 때는 액티비티 그냥 취소
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl:String) {
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        val user = User(uid,edt_profile_nickname.text.toString(),profileImageUrl)

        // realtime database 에 데이터 업로드 기능 , User 클래스 형식으로 입력
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("ProfileActivity","register userinfo to realtime database")
            }
    }

}

