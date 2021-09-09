package org.techtown.wishmatching

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.Thread.sleep

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
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
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }

    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//    }
}