package org.techtown.wishmatching

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.techtown.wishmatching.Chatting.ChattingFragment
import org.techtown.wishmatching.Mypage.MyPageFragment

class MainFragmentStatePagerAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {

        when(position){
            0 -> {

                return HomeFragment()
            }
            1 -> return ChattingFragment()
            2 -> return MyPageFragment()
            else -> return HomeFragment()
        }
    }
    override fun getCount(): Int = fragmentCount // 자바에서는 { return fragmentCount }

}