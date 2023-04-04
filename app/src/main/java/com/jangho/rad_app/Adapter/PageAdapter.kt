package com.jangho.rad_app.Adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jangho.rad_app.HomePage.Board3Fragment
import com.jangho.rad_app.HomePage.Intro1Fragment
import com.jangho.rad_app.HomePage.Notice2Fragment


class PageAdapter(frag: FragmentActivity?, var fragmentSize: Int) : FragmentStateAdapter(frag !!) {

    //어댑터가 화면에 보여줄 전체 프래그먼트 개수를 반환
    override fun getItemCount(): Int {
        return 10000
    }

    override fun createFragment(position: Int): Fragment {
        //4번째 페이지, 0 1 2 3의 position 경우에도 0번 화면을 띄워줘야 하기 때문,
        if(position % fragmentSize == 0) {
            return Intro1Fragment()
        }
        else if(position % fragmentSize == 1) {
            return Notice2Fragment()
        }
        else{
            return Board3Fragment()
        }
    }
}
