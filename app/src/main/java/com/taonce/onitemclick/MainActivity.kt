package com.taonce.onitemclick

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.taonce.onitemclick.base.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val mData = mutableListOf("1")
    val adapter = MainAdapter(this, R.layout.item, mData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_add.setOnClickListener(this)
        bt_addAll.setOnClickListener(this)
        bt_reduce.setOnClickListener(this)
        bt_deleteAll.setOnClickListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        // 点击事件
        adapter.setOnItemClickListener {
            Log.d("taonce", "click item position is $it, value is ${mData[it]}")
        }
        // 长按事件
        adapter.setOnItemLongClickListener {
            Log.d("taonce", "long click position is $it, value is ${mData[it]}")
            return@setOnItemLongClickListener true
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            // 添加一个数据
            R.id.bt_add -> adapter.addData("add")
            // 添加多项数据
            R.id.bt_addAll -> adapter.addListData(mutableListOf("android", "ios", "kotlin", "flutter"), true)
            // 删除一项数据
            R.id.bt_reduce -> adapter.deletePositionData(0)
            // 删除全部数据
            R.id.bt_deleteAll -> adapter.deleteAllData()
        }
    }
}
