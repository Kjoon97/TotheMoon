package org.techtown.wishmatching

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import android.content.ContentValues.TAG
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? =null // 어쎈티케이션 라이브러리 호출
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE =9001
    lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 구글 로그인
        auth = FirebaseAuth.getInstance()
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)  //구글로그인 옵션
            .requestIdToken(getString(R.string.default_web_client_id)) //구글 api키
            .requestEmail() //이메일 아이디 받아옴
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)


        //////////////////// 페이스북 로그인
        auth = Firebase.auth

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"))

        btn_facebook_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                if (loginResult != null) {
                    handleFacebookAccessToken(loginResult.accessToken)
                }
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(exception: FacebookException) {
                Log.d(TAG, "facebook:onError")
            }
        })
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    if (loginResult != null) {
                        handleFacebookAccessToken(loginResult.accessToken)
                    }
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })
        //////////////////// 페이스북 로그인
    }


    fun googleLogin(){  //구글 로그인 단계
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 구글 로그인한 걸 파이어베이스에 연동
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data) //구글에서 넘겨주는 로그인 결과값 가져오기
            if(result.isSuccess){ //성공 시 파이어베이스에 넣게끔 만들기
                var account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)// account안에 있는 토큰아이디 넘기기
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { // 로그인 결과값 가져오기
                    task ->
                if (task.isSuccessful){   //아이디와 비밀번호가 일치시에 작동
                    moveMainPage(task.result?.user)
                }else{ // 실패시
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun moveMainPage(user: FirebaseUser?){ //로그인 성공 시 다음 페이지로 넘어가는 함수, 파베의 유저 상태를 넘겨줌
        if(user != null){ // 상태가 있을 경우, 메인 액티비티로 넘어감
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Pass the activity result back to the Facebook SDK
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

}