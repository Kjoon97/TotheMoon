package org.techtown.wishmatching


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    private lateinit var twitterAuthClient: TwitterAuthClient   //트위터 관련
    private lateinit var callbackManager: CallbackManager       //페이스북 관련
    private lateinit var googleSignInClient: GoogleSignInClient //구글 관련
    var GOOGLE_LOGIN_CODE =9001
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextUserEmail.setSelection(editTextUserEmail.length())
        editTextUserPassword.setSelection(editTextUserEmail.length())
//        if(MySharedPreferences.getUserId(this).isNotEmpty()) {    SharedPreference를 쓰지않아도 될거같음
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        //------------------------------------------------------------------------------------------------------------------------------------
        //Twitter
        initTwitter()       //컨텐츠뷰 설정 전에 실행되어야함. 그렇지 않으면 트위터 버튼이 눌리지 않음
        setContentView(R.layout.activity_login)     //컨텐츠뷰 설정
        twitterAuthClient = TwitterAuthClient()     //트위터 로그인 관련
        initTwitterSignIn() //트위터 로그인에서의 setOnClickListener()


        //--------------------------------------------------------------------------------------------------------------------------------------
        //Facebook

        callbackManager = CallbackManager.Factory.create();     //페이스북 로그인 관련
        initFacebookSignIn()       //페이스북 로그인에서의 setOnClickListener()


        //--------------------------------------------------------------------------------------------------------------------------------------------
        //Local
        btn_find_password.setOnClickListener {
            var intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }

        btnSignIn.setOnClickListener { // 로컬 회원가입 액티비티로 이동
            var intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        //로컬 로그인
        btnLogIn.setOnClickListener {
            var userEmail = editTextUserEmail.text.toString()
            var userPw = editTextUserPassword.text.toString()
            var user = Authentication.auth.currentUser

            if(userEmail.isEmpty() || userPw.isEmpty()) {
                Toast.makeText(this,"이메일과 비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            Authentication.auth?.signInWithEmailAndPassword(userEmail, userPw)?.addOnCompleteListener(this) {
                if(Authentication.auth.currentUser?.isEmailVerified==null) {
                    return@addOnCompleteListener
                }
                else {
                    if (Authentication.auth.currentUser!!.isEmailVerified) { //인증메일에서 링크 클릭시 로그인 가능
                        if (it.isSuccessful) {
                            db.collection("user")
                                .whereEqualTo("uid", Authentication.auth!!.uid)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (documents.isEmpty) {            // 처음 로그인 하면 프로필 화면으로 이동
                                        val intent = Intent(this, ProfileActivity::class.java)
                                        startActivity(intent)
                                    } else {                        // 그게 아니라면 메인액티비티로 이동
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                        } else {

                        }
                    } else {
                        Toast.makeText(this, "인증 메일을 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this,"이메일과 비밀번호를 확인해주세요.",Toast.LENGTH_LONG).show()
            }
        }


        //---------------------------------------------------------------------------------------------------------------------------------------------
        //Google

        initGoogle()    //구글 로그인 초기 설정
        google_sign_in_button.setOnClickListener {  //구글 로그인
            googleLogin()
        }

        //----------------------------------------------------------------------------------------------------------------------------------------------



    }

//    override fun onStart() {
//        super.onStart()
//
//        if(Authentication.auth.currentUser !=null){
//            val user = Authentication.auth.currentUser
//            user.let {
//                var providerId : String? = null
//                for(profile in it!!.providerData) {
//                    providerId = profile.providerId
//                }
//                if(providerId == "password"){
//                    if(Authentication.auth.currentUser?.isEmailVerified == true){
//                        db.collection("user")
//                            .whereEqualTo("uid", Authentication.auth!!.uid)
//                            .get()
//                            .addOnSuccessListener { documents->
//                                if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
//                                    val intent = Intent(this, ProfileActivity::class.java)
//                                    startActivity(intent)
//                                } else{                        // 그게 아니라면 메인액티비티로 이동
//                                    val intent = Intent(this, MainActivity::class.java)
//                                    startActivity(intent)
//                                }
//                            }
//                    } else {}
//                } else{
//                    db.collection("user")
//                        .whereEqualTo("uid", Authentication.auth!!.uid)
//                        .get()
//                        .addOnSuccessListener { documents->
//                            if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
//                                val intent = Intent(this, ProfileActivity::class.java)
//                                startActivity(intent)
//                            } else{                        // 그게 아니라면 메인액티비티로 이동
//                                val intent = Intent(this, MainActivity::class.java)
//                                startActivity(intent)
//                            }
//                        }
//                }
//            }
//
//        }
//    }

    //-------------------------------------------------------------------------------------------------
    private fun initTwitter(){      //API키를 대입해서 트위터 인증이 가능하도록 초기 설정
        val authConfig = TwitterAuthConfig(
            getString(R.string.twitter_consumer_key),
            getString(R.string.twitter_consumer_secret)
        )
        val config = TwitterConfig.Builder(this)
            .twitterAuthConfig(authConfig)
            .build()

        Twitter.initialize(config)
    }

    private fun initTwitterSignIn(){    //트위터 로그인 버튼을 누르면 트위터 로그인 페이지가 뜨게하고, 그 이후 콜백을 관리
        twitterLogInButton.callback = object : Callback<TwitterSession>(){
            override fun success(result: Result<TwitterSession>?) {
                handleTwitterLogin(result!!.data)   //로그인에 성공하였다면, 트위터에 로그인한 사용자의 데이터를 인자로 넘겨줌(파이어베이스에 로그인 등록)
            }
            override fun failure(exception: TwitterException?) {
                Toast.makeText(applicationContext," 실패",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun handleTwitterLogin(session: TwitterSession){    //파이어베이스에서 트위터로그인을 한 사용자를 등록
        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret)
        Authentication.auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful){
//                    MySharedPreferences.setUserId(this, "have been login")
//                    MySharedPreferences.setLoginType(this, "twitter")
                    db.collection("user")
                        .whereEqualTo("uid", Authentication.auth.uid) //uid
                        .get()
                        .addOnSuccessListener { documents->
                            if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
                                val intent = Intent(this, ProfileActivity::class.java)
                                startActivity(intent)
                            } else{                        // 그게 아니라면 메인액티비티로 이동
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                } else{ //파이어베이스에 트위터 로그인 등록 못했을 시
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }

            }
    }

//--------------------------------------------------------------------------------------------------

    private fun initFacebookSignIn(){
        btn_facebook_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {     //페이스북 로그인 버튼을 누르면 페이스북에서 로그인 페이지가 뜨게하고, 그 이후 콜백을 관리
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                if (loginResult != null) {
                    handleFacebookAccessToken(loginResult.accessToken)
                        //이부분은 잠시 보류
                }
            }
            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }
            override fun onError(exception: FacebookException) {
                Log.d(TAG, "facebook:onError")
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {  //파이어베이스에서 페이스북 로그인을 한 사용자 등록
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        Authentication.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    MySharedPreferences.setLoginType(this, "facebook")
                    // Sign in success, update UI with the signed-in user's information
                    db.collection("user")
                        .whereEqualTo("uid", Authentication.auth!!.uid)
                        .get()
                        .addOnSuccessListener { documents->
                            if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
                                val intent = Intent(this, ProfileActivity::class.java)
                                startActivity(intent)
                            } else{                        // 그게 아니라면 메인액티비티로 이동
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

//--------------------------------------------------------------------------------------------------

    fun initGoogle(){
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)  //구글로그인 관련(옵션 설정)
            .requestIdToken("122414114381-r55omesp4e0ccedutpibirmr0h81m54s.apps.googleusercontent.com")
            .requestEmail() //이메일 아이디 받아옴
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }


    fun googleLogin(){  //구글 로그인 단계
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)// account안에 있는 토큰아이디 넘기기
        Authentication.auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { // 로그인 결과값 가져오기
                    task ->
                if(task.isSuccessful){
//                    MySharedPreferences.setLoginType(this, "google")
                    db.collection("user")
                        .whereEqualTo("uid", Authentication.auth!!.uid)
                        .get()
                        .addOnSuccessListener { documents->
                            if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
                                val intent = Intent(this, ProfileActivity::class.java)
                                startActivity(intent)
                            } else{                        // 그게 아니라면 메인액티비티로 이동
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }

                }else{ // 실패시
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

//--------------------------------------------------------------------------------------------------


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)     //이부분은 문제없이 돌아감
        twitterAuthClient?.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data) //구글에서 넘겨주는 로그인 결과값 가져오기
            if(result.isSuccess){ //성공 시 파이어베이스에 넣게끔 만들기
                var account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }



}