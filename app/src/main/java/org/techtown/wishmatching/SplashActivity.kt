package org.techtown.wishmatching

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.Thread.sleep

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val db = Firebase.firestore
//        Handler().postDelayed({
//            val intent = Intent(this,LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//            startActivity(intent)
//            finish()
//        },2500)
        iv_logo1.alpha =0f
        iv_logo2.alpha =0f
        iv_logo2.animate().setDuration(1300).alpha(1f)
        iv_logo1.animate().setDuration(1300).alpha(1f).withEndAction {
            sleep(1000)
            val user = Firebase.auth.currentUser
            if(user != null) {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
//            if(Authentication.auth !=null){
//                db.collection("user")
//                    .whereEqualTo("uid", Authentication.auth!!.uid)
//                    .get()
//                    .addOnSuccessListener { documents->
//                        if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
//                            val intent = Intent(this, LoginActivity::class.java)
//                            startActivity(intent)
//                        } else{                        // 그게 아니라면 메인액티비티로 이동
//                            val intent = Intent(this, MainActivity::class.java)
//                            startActivity(intent)
//                        }
//                    }
//            }
//            val intent = Intent(this,LoginActivity::class.java)
//            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }



    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//    }
}