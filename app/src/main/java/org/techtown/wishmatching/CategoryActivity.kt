package org.techtown.wishmatching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.ActionBar
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

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


    }
}