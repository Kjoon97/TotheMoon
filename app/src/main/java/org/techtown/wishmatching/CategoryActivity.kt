package org.techtown.wishmatching

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.doingdeal_row.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import org.techtown.wishmatching.Database.ContentDTO
import org.techtown.wishmatching.Database.PostDTO

class CategoryActivity : AppCompatActivity() {

    var firestore : FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var storage : FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

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

        }

    }

    fun writeCategory() {

    }
}