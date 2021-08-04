package org.techtown.wishmatching
// commit test gb
// commit test kjh
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.twitter.sdk.android.core.TwitterCore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        configureBottomNavigation()

    }

    private fun configureBottomNavigation() {
        vp_ac_main_frag_pager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 3)

        tl_ac_main_bottom_menu.setupWithViewPager(vp_ac_main_frag_pager)

        val bottomNaviLayout: View =
            this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        tl_ac_main_bottom_menu.getTabAt(0)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(1)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_add_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(2)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_my_page_tab) as RelativeLayout
    }

}