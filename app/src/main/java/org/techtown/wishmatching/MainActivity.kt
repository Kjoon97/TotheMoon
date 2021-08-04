package org.techtown.wishmatching
// commit test gb
// commit test kjh
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var dataList: ArrayList<ListData> = arrayListOf(
        ListData("1","2"),
        ListData("2","2"),
        ListData("3","2"),
        ListData("4","2"),
        ListData("5","2"),
        ListData("6","2"),
        ListData("7","2"),
        ListData("8","2"),
        ListData("9","2"),
        ListData("10","2")

    )
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        intent.putExtra("DataList", dataList)
        configureBottomNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var mInflater = menuInflater
        mInflater.inflate(R.menu.testmenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profilemenu_test-> startActivity(Intent(this,ProfileActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    // 네비게이션바 , 뷰페이지 어댑터 설정
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