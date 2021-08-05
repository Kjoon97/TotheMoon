package org.techtown.wishmatching

// commit test
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import kotlinx.android.synthetic.main.activity_login.*
import org.techtown.wishmatching.Database.ContentDTO
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth   // 트위터
    private lateinit var auth2: FirebaseAuth  // 페이스북
    var auth3 : FirebaseAuth? =null           // 구글
    lateinit var twitterAuthClient: TwitterAuthClient
    lateinit var callbackManager: CallbackManager
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE =9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initTwitter()
        setContentView(R.layout.activity_login)
        twitterAuthClient = TwitterAuthClient()
        auth = FirebaseAuth.getInstance()
        initTwitterSignIn()

        auth2 = Firebase.auth
        callbackManager = CallbackManager.Factory.create();

        btnSignIn.setOnClickListener { // 회원가입 액티비티
            var intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        //로컬 로그인
        btnLogIn.setOnClickListener {
            var userEmail = editTextUserEmail.text.toString()
            var userPw = editTextUserPassword.text.toString()
            var user = Firebase.auth.currentUser

            auth?.signInWithEmailAndPassword(userEmail, userPw)?.addOnCompleteListener(this) {
                if(user!!.isEmailVerified) { //인증메일에서 링크 클릭시 로그인 가능
                    if (it.isSuccessful) startActivity(Intent(this, MainActivity::class.java))
                    else Toast.makeText(this,"인증 메일을 확인해주세요.",Toast.LENGTH_SHORT).show()
                }
                else Toast.makeText(this, "LogIn fail.", Toast.LENGTH_SHORT).show()
            }
        }


//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"))

        btn_facebook_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                if (loginResult != null) {
                    handleFacebookAccessToken(loginResult.accessToken)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
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
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    }
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })

        // 구글 로그인
        auth3 = FirebaseAuth.getInstance()
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)  //구글로그인 옵션
            .requestIdToken(getString(R.string.default_web_client_id)) //구글 api키
            .requestEmail() //이메일 아이디 받아옴
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)


    }

    private fun initTwitter(){
        val authConfig = TwitterAuthConfig(
            getString(R.string.twitter_consumer_key),
            getString(R.string.twitter_consumer_secret)
        )
        val config = TwitterConfig.Builder(this)
            .twitterAuthConfig(authConfig)
            .build()

        Twitter.initialize(config)
    }

    private fun initTwitterSignIn(){
        twitterLogInButton.callback = object : Callback<TwitterSession>(){
            override fun success(result: Result<TwitterSession>?) {
                handleTwitterLogin(result!!.data)
                Toast.makeText(applicationContext," 성공",Toast.LENGTH_LONG).show()
            }

            override fun failure(exception: TwitterException?) {
                Toast.makeText(applicationContext," 실패",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun handleTwitterLogin(session: TwitterSession){
        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful){
                    val db = Firebase.firestore
                    db.collection("user")
                        .whereEqualTo("uid", auth.uid) //uid
                        .get()
                        .addOnSuccessListener { documents->
                            if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
                                var contentDTO = ContentDTO()
                                contentDTO.uid = auth?.currentUser?.uid
                                contentDTO.userId = auth?.currentUser?.email
                                db?.collection("user")?.document()?.set(contentDTO)
                                val intent = Intent(this, ProfileActivity::class.java)
                                startActivity(intent)
                            } else{                        // 그게 아니라면 메인액티비티로 이동
                                for(docuemnt in documents){
                                    if(docuemnt.get("area") == null || docuemnt.get("imageUrl") == null || docuemnt.get("nickname") == null){// ??????
                                        val intent = Intent(this, ProfileActivity::class.java)
                                        startActivity(intent)
                                    } else{
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }


                } else{

                }

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data) //구글에서 넘겨주는 로그인 결과값 가져오기
            if(result.isSuccess){ //성공 시 파이어베이스에 넣게끔 만들기
                var account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
        twitterAuthClient?.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth2.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth2.currentUser
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

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth2.currentUser
        //updateUI(currentUser)
    }

    fun googleLogin(){  //구글 로그인 단계
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)// account안에 있는 토큰아이디 넘기기
        auth3?.signInWithCredential(credential)
            ?.addOnCompleteListener { // 로그인 결과값 가져오기
                    task ->
//                if (task.isSuccessful){   //아이디와 비밀번호가 일치시에 작동
//                    moveMainPage(task.result?.user)
                if(task.isSuccessful){
                    val db = Firebase.firestore
                    db.collection("user")
                        .whereEqualTo("uid", auth.uid)
                        .get()
                        .addOnSuccessListener { documents->
                            if(documents.isEmpty){            // 처음 로그인 하면 프로필 화면으로 이동
                                var contentDTO = ContentDTO()
                                contentDTO.uid = auth?.currentUser?.uid
                                contentDTO.userId = auth?.currentUser?.email
                                db?.collection("user")?.document()?.set(contentDTO)
                                val intent = Intent(this, ProfileActivity::class.java)
                                startActivity(intent)
                            } else{                        // 그게 아니라면 메인액티비티로 이동
                                for(docuemnt in documents){
                                    if(docuemnt.get("area") == null || docuemnt.get("imageUrl") == null || docuemnt.get("Nickname") == null){// ??????
                                        val intent = Intent(this, ProfileActivity::class.java)
                                        startActivity(intent)
                                    } else{
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }


                }else{ // 실패시
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
//    fun moveMainPage(user: FirebaseUser?){ //로그인 성공 시 다음 페이지로 넘어가는 함수, 파베의 유저 상태를 넘겨줌
//        if(user != null){ // 상태가 있을 경우, 메인 액티비티로 넘어감
//            startActivity(Intent(this,MainActivity::class.java))
//        }
//    }

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Pass the activity result back to the Facebook SDK
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }


}