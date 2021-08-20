package org.techtown.wishmatching.Mypage.DealSituation

import android.app.TabActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_deal_situation.*
import kotlinx.android.synthetic.main.activity_main.*
import org.techtown.wishmatching.MainFragmentStatePagerAdapter
import org.techtown.wishmatching.R

class DealSituationActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.doingdeal_menu->{
                var doingDealFragment =DoingDealFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,doingDealFragment).commit()
                return true
            }
            R.id.dealcomplete_menu->{
                var dealCompleteFragment =DealCompleteFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,dealCompleteFragment).commit()
                return true
            }
        }
        return false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_situation)
        button_navigation.setOnNavigationItemSelectedListener(this)

    }
}