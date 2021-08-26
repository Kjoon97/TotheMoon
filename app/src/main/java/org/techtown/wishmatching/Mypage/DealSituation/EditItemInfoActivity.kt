package org.techtown.wishmatching.Mypage.DealSituation

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import org.techtown.wishmatching.MyUndoListener
import org.techtown.wishmatching.R

class EditItemInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item_info)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val mInflater = menuInflater
        mInflater.inflate(R.menu.menu_addpostactivity,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_addpost -> {


                val mySnackbar = Snackbar.make(findViewById(R.id.home2),
                    "수정중입니다.", Snackbar.LENGTH_SHORT)
                mySnackbar.setAction("닫기", MyUndoListener())
                mySnackbar.setTextColor(Color.WHITE)
                mySnackbar.show()



            }
        }

        return super.onOptionsItemSelected(item)
    }
}