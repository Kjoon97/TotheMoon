package org.techtown.wishmatching

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.doingdeal_row.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.techtown.wishmatching.Database.ContentDTO
import org.techtown.wishmatching.Database.PostDTO

class CategoryActivity : AppCompatActivity() {

    var db : FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var storage : FirebaseStorage? = null
    var userId : String? = null
    var docId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        db = FirebaseFirestore.getInstance() //파이어베이스 접근을 위한 객체생성
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()


        userId = auth?.currentUser!!.uid // 로그인 유저의 uid
        var collRef = db!!.collection("user")
        var docReference: Query = collRef.whereEqualTo("uid","${userId}")

        Digital.isChecked = true
        Funiture.isChecked = true
        Food.isChecked = true
        Sports.isChecked = true
        ManClothes.isChecked = true
        WomanClothes.isChecked = true
        Games.isChecked = true
        Beauty.isChecked = true
        Animals.isChecked = true
        Books.isChecked = true
        Baby.isChecked = true


        Digital.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
               //val text = db!!.collection("user").whereEqualTo("uid",Authentication.auth.uid)
                //Toast.makeText(this,"${docReference}",Toast.LENGTH_SHORT).show()
                //db!!.collection("user")?.document("$docReference")?.set
                    val data = hashMapOf(
                        "userCategory" to Digital.text.toString()
                    )
                db!!.collection("user").document("$docReference")
                    .update("userCategory",Digital.text.toString())
            }
            else Toast.makeText(this,"${docId}",Toast.LENGTH_SHORT).show()
        }

        //db!!.collection("user").document("")

    }

}