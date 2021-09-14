package org.techtown.wishmatching

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.Thread.sleep

class SplashActivity : AppCompatActivity() { //로딩액티비티
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        iv_logo1.alpha =0f //그림 안보이게 설정
        iv_logo2.alpha =0f
        iv_logo2.animate().setDuration(1300).alpha(1f)  //1.3초 후에 그림 보이게 설정.
        iv_logo1.animate().setDuration(1300).alpha(1f).withEndAction {
            sleep(1000)  //그림 다 뜨고 1초간 멈추게 설정.
            val user = Firebase.auth.currentUser
            if(user != null) {   //로그인 상태인 사용자면 메인 액티비티로 이동
                user.let {
                    var providerId: String? = null
                    for (profile in it!!.providerData) {
                        providerId = profile.providerId
                    }
                    if(providerId == "password"){
                        if(Authentication.auth.currentUser?.isEmailVerified == true){
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        } else{
                            val intent = Intent(this,LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }else{
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }
                }


            }
            else {    //그렇지 않으면 로그인 액티비티로 이동.
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }

            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)  //그림이 서서히 나타나고 서서히 사라짐
            finish()
        }
    }

}