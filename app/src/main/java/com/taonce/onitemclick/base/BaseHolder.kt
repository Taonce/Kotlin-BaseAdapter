package com.taonce.onitemclick.base

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.find


/**
 * Author: taoyongxiang
 * Date: 2018/12/4
 * Project: base
 * Desc: RecyclerView.ViewHolder基类
 */

class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

	// 使用`SparseArrayCompat`是为了复用view，不用每次`getView`都去`find`
	private val mViews = SparseArrayCompat<View>()

	/**
	 * 通过resId获取view
     * 将获取到的View转换成具体的View,比如：TextView、Button等等
     * 这里主要用到的是Kotlin里面的`as`操作符
	 */
	 fun <V : View> getView(id: Int): V {
		var view = mViews[id]
		if (view == null) {
			view = itemView.find(id)
			mViews.put(id, view)
		}
		return view as V
	}
}