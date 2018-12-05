package com.taonce.onitemclick

import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.taonce.onitemclick.base.BaseAdapter
import com.taonce.onitemclick.base.BaseHolder
import kotlin.random.Random


/**
 * Author: taoyongxiang
 * Date: 2018/12/4
 * Project: base
 * Desc: demo
 */

class MainAdapter(ctx: Context, layoutRes: Int, mData: MutableList<String>) : BaseAdapter<String>(ctx, layoutRes, mData) {
	override fun convert(holder: BaseHolder, position: Int) {
		// 获取item中的TextView
		val text = holder.getView<TextView>(R.id.item_text)
		text.text = this.mData[position]

		// 获取item中的Button
		val button = holder.getView<Button>(R.id.item_button)
		button.text = "${Random.nextBoolean()}"
		button.setOnClickListener {
			Log.d("taonce", "button click item is $position")
		}

		// 获取item中的ImageView
		val image = holder.getView<ImageView>(R.id.item_image)
		image.setImageResource(R.mipmap.ic_launcher)
	}

}