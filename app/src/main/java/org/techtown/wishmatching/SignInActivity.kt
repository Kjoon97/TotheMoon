package org.techtown.wishmatching

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        var user = Firebase.auth.currentUser

        btnOk.setOnClickListener { // 인증 완료시
            val userEmail = editTextSignInEmail.text.toString()
            val userPassword = editTextSignInPassword.text.toString()
            auth?.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(this) {task ->
                if (task.isSuccessful) Log.d(TAG, "signInWithEmail:success")
                user=auth.currentUser
                user!!.sendEmailVerification()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) Log.d(TAG, "Email sent.")
                }
            }
            var dlg = AlertDialog.Builder(this)
            dlg.setTitle("알림")
            dlg.setMessage("등록된 이메일로 발송된 인증메일을 확인하세요.")
            dlg.setIcon(R.drawable.logo)
            dlg.setPositiveButton("확인") {diglog, which ->
                finish()
            }
            dlg.show()
        }


    }


}