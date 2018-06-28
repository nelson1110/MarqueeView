package com.libs.nelson.marqueeview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.libs.nelson.marqueeviewlib.MarqueeView

class TestAdapter : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            val item = LayoutInflater.from(parent.context).inflate(R.layout.item_marquee,parent,false)
            return TestViewHolder(item)
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {

            holder.itemView.findViewById<MarqueeView>(R.id.item_marquee).setAdapter(object : MarqueeView.MarqueeViewAdapter() {
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


    }


    class TestViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}