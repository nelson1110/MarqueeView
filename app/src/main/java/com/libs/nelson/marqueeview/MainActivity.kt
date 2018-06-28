package com.libs.nelson.marqueeview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.libs.nelson.marqueeviewlib.MarqueeView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        marqueeView.setAdapter(object : MarqueeView.MarqueeViewAdapter(){
            override fun getItemLayout(): Int {

                return R.layout.layout
            }

            override fun onBindItemView(itemView: View, position: Int) {
              itemView.findViewById<TextView>(R.id.text).text = position.toString()

            }

            override fun getItemCount(): Int {
                return 4
            }

        })

        marqueeView2.setAdapter(object : MarqueeView.MarqueeViewAdapter(){
            override fun getItemLayout(): Int {

                return R.layout.layout
            }

            override fun onBindItemView(itemView: View, position: Int) {
                itemView.findViewById<TextView>(R.id.text).text = position.toString()

            }

            override fun getItemCount(): Int {
                return 4
            }

        })


        marqueeView3.setAdapter(object : MarqueeView.MarqueeViewAdapter(){
            override fun getItemLayout(): Int {

                return R.layout.layout
            }

            override fun onBindItemView(itemView: View, position: Int) {
                itemView.findViewById<TextView>(R.id.text).text = position.toString()

            }

            override fun getItemCount(): Int {
                return 4
            }

        })

        marqueeView4.setAdapter(object : MarqueeView.MarqueeViewAdapter(){
            override fun getItemLayout(): Int {

                return R.layout.layout
            }

            override fun onBindItemView(itemView: View, position: Int) {
                itemView.findViewById<TextView>(R.id.text).text = position.toString()

            }

            override fun getItemCount(): Int {
                return 1
            }

        })


        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TestAdapter()

    }
}
