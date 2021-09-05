package org.techtown.wishmatching

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : AppCompatActivity() {

    var db: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var storage: FirebaseStorage? = null
    var userId: String? = null
    var docId: String? = null
    var count: Int = 0
    var num = 1
    var Clicked = arrayOf(false,false,false,false,false,false,false,false,false,false,false,false)
    var Category = arrayOf("디지털기기","가구/인테리어","식품","스포츠/레저","남성잡화","여성잡화","게임/취미","미용","반려동물물품","도서/티켓/음반",
        "유아용품","기타")

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        db = FirebaseFirestore.getInstance() //파이어베이스 접근을 위한 객체생성
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth?.currentUser!!.uid // 로그인 유저의 uid
        val intent = intent
        docId = intent.getStringExtra("doc_id")


        Digital.setOnClickListener {
            Clicked[0] = !Clicked[0]
            if (Clicked[0] == true) { //선택했을때
                Digital.setBackgroundColor(Color.GRAY)
                count += 1
            } else { //미선택
                Clicked[0] == false
                count -= 1
                Digital.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Funiture.setOnClickListener {
            Clicked[1] = !Clicked[1]
            if (Clicked[1] == true) { //선택했을때
                Funiture.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[1] == false
                count -= 1
                Funiture.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Food.setOnClickListener {
            Clicked[2] = !Clicked[2]
            if (Clicked[2] == true) { //선택했을때
                Food.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[2] == false
                count -= 1
                Food.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Sports.setOnClickListener {
            Clicked[3] = !Clicked[3]
            if (Clicked[3] == true) { //선택했을때
                Sports.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[3] == false
                count -= 1
                Sports.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        ManClothes.setOnClickListener {
            Clicked[4] = !Clicked[4]
            if (Clicked[4] == true) { //선택했을때
                ManClothes.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[4] == false
                count -= 1
                ManClothes.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        WomanClothes.setOnClickListener {
            Clicked[5] = !Clicked[5]
            if (Clicked[5] == true) { //선택했을때
                WomanClothes.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[5] == false
                count -= 1
                WomanClothes.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Games.setOnClickListener {
            Clicked[6] = !Clicked[6]
            if (Clicked[6] == true) { //선택했을때
                Games.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[6] == false
                count -= 1
                Games.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Beauty.setOnClickListener {
            Clicked[7] = !Clicked[7]
            if (Clicked[7] == true) { //선택했을때
                Beauty.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[7] == false
                count -= 1
                Beauty.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Animals.setOnClickListener {
            Clicked[8] = !Clicked[8]
            if (Clicked[8] == true) { //선택했을때
                Animals.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[8] == false
                count -= 1
                Animals.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Books.setOnClickListener {
            Clicked[9] = !Clicked[9]
            if (Clicked[9] == true) { //선택했을때
                Books.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[9] == false
                count -= 1
                Books.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Baby.setOnClickListener {
            Clicked[10] = !Clicked[10]
            if (Clicked[10] == true) { //선택했을때
                Baby.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[10] == false
                count -= 1
                Baby.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }

        Etc.setOnClickListener {
            Clicked[11] = !Clicked[11]
            if (Clicked[11] == true) { //선택했을때
                Etc.setBackgroundColor(Color.GRAY)
                count += 1
            }
            else { //미선택
                Clicked[11] == false
                count -= 1
                Etc.setBackgroundColor(Color.parseColor("#86C8E6"))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu_category,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.set_category -> {
                if (count > 3) Toast.makeText(this, "카테고리는 최대 3개까지 설정가능합니다.", Toast.LENGTH_SHORT)
                    .show()
                else {
                    db!!.collection("user").whereEqualTo("uid", Authentication.auth.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                db!!.collection("user").document(document.id)
                                    .update("userCategory1" , "")
                            }
                        }
                    db!!.collection("user").whereEqualTo("uid", Authentication.auth.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                db!!.collection("user").document(document.id)
                                    .update("userCategory2" , "")
                            }
                        }
                    db!!.collection("user").whereEqualTo("uid", Authentication.auth.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                db!!.collection("user").document(document.id)
                                    .update("userCategory3" , "")
                            }
                        }
                    for (x in 0..11 step 1) {
                        if (Clicked[x]) {
                            db!!.collection("user").whereEqualTo("uid", Authentication.auth.uid)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        db!!.collection("user").document(document.id)
                                            .update("userCategory" + "${num++}", Category[x])
                                    }
                                }
                        }
                    }
//                    for(i in 0..100000) {
//                        var value:Int
//                        value = i+1
//                    }
                    Thread.sleep(150)

//                    finishActivity(3)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivityForResult(intent,3)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

}