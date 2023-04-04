package com.jangho.rad_app.HomePage

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.jangho.rad_app.R
import org.json.JSONArray


class Intro1Fragment : Fragment() {
    private lateinit var v: View //ViewCreated에서도 사용 위해 전역변수로 늦은 초기화

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Log.e("onAtt", "onAttach()")
    }

    override fun onStart() {
        super.onStart()
        Log.e("Start", "start()")
    }

    override fun onResume() {
        super.onResume()
        Log.e("resume", "onResume()")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 생성시 인플레이트하여 해당 뷰를 생성해주고 지정해줌
        v = inflater.inflate(R.layout.fragment_intro1, container, false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 1. json 파일을 문자열만으로 읽음
        val jsonString = view.context.assets.open("intro.json").reader().readText()
        // 2. JSONArray 로 파싱
        val jsonArray = JSONArray(jsonString)
        // 3. JSONArray 순회: 인덱스별 JsonObject 취득후, key에 해당하는 value 확인
        for (index in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(index)
            val name = jsonObject.getString("name")
            val img = jsonObject.getString("img")

            when(index){
                0-> {
                    v.findViewById<TextView>(R.id.prj1).setText(name)
                    Glide.with(this)
                        .load(img)//이미지 위치
                        .error(R.drawable.error_image)//에러 이미지
                        .into(v.findViewById<ImageView>(R.id.img1))//보여줄 위치
                }
                1-> {
                    v.findViewById<TextView>(R.id.prj2).setText(name)
                    Glide.with(this)
                        .load(img)//이미지 위치
                        .error(R.drawable.error_image)//에러 이미지
                        .into(v.findViewById<ImageView>(R.id.img2))//보여줄 위치
                }
                2-> {
                    v.findViewById<TextView>(R.id.prj3).setText(name)
                    Glide.with(this)
                        .load(img)//이미지 위치
                        .error(R.drawable.error_image)//에러 이미지
                        .into(v.findViewById<ImageView>(R.id.img3))//보여줄 위치
                }
                3-> {
                    v.findViewById<TextView>(R.id.prj4).setText(name)
                    Glide.with(this)
                        .load(img)//이미지 위치
                        .error(R.drawable.error_image)//에러 이미지
                        .into(v.findViewById<ImageView>(R.id.img4))//보여줄 위치
                }
                4-> {
                    v.findViewById<TextView>(R.id.prj5).setText(name)
                    Glide.with(this)
                        .load(img)//이미지 위치
                        .error(R.drawable.error_image)//에러 이미지
                        .into(v.findViewById<ImageView>(R.id.img5))//보여줄 위치
                }
                5-> {
                    v.findViewById<TextView>(R.id.prj6).setText(name)
                    Glide.with(this)
                        .load(img)//이미지 위치
                        .error(R.drawable.error_image)//에러 이미지
                        .into(v.findViewById<ImageView>(R.id.img6))//보여줄 위치
                }
                6-> {
                    v.findViewById<TextView>(R.id.prj7).setText(name)
                    Glide.with(this)
                        .load(img)//이미지 위치
                        .error(R.drawable.error_image)//에러 이미지
                        .into(v.findViewById<ImageView>(R.id.img7))//보여줄 위치
                }
            }
        }
    }



}