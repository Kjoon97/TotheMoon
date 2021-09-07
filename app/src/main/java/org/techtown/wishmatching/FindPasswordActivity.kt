package org.techtown.wishmatching
//123
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_find_password.*
//123
class FindPasswordActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_find_password)
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "비밀번호 찾기"

        auth = FirebaseAuth.getInstance()

        btn_sendtoemail.setOnClickListener {
            val userEmail = editTextToFindPassword.text.toString()
            if(userEmail.isNotEmpty()) {
                auth.sendPasswordResetEmail(userEmail).addOnCompleteListener {
                    if(it.isSuccessful){
                        var dlg = AlertDialog.Builder(this)
                        dlg.setTitle("알림")
                        dlg.setMessage("등록된 이메일로 발송된 메일을 확인하세요.")
                        dlg.setIcon(R.drawable.logo)
                        dlg.setPositiveButton("확인") {diglog, which ->
                            finish()
                        }
                        dlg.show()
                    }else{
                        Toast.makeText(this,"이메일 발송에 실패하였습니다.",Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_LONG).show()
            }

        }

    }

}