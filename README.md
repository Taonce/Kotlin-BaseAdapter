# Kotlin-BaseAdapter
#### 下面给大家介绍一个简单实用的`RecyclerAdapter`的封装

日常开发中，我们几乎不可能不使用`RecyclerView`这个列表，但是如果每次都去手动去实现它的`Adapter`，相比是极其痛苦的。每次固定的复写它的三个方法也是很无奈，所以笔者就想着封装一个既简单又使用的`Adapter`，满足日常的开发需求，又不冗余。**在使用Kotlin封装的过程中，进一步体会下Kotlin语言的魅力。**

###### 重要的方法和类

1.  `getItemCount()`：返回`Int`值，代表列表的长度

2.  `onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder`：通过`item`布局得到`View`对象，返回我们需要的`ViewHolder`对象

3.  `onBindViewHolder(holder: BaseHolder, position: Int)`：通过`position`获取当前的`item`数据传给`ViewHolder`对象内控件

4.  `RecyclerView.ViewHolder(itemView)`：通过获取的数据对`itemView`及其子`View`进行操作

###### 对上面方法和类进行复写和继承

首先贴出对`ViewHolder`封装的整体代码

```
package com.taonce.onitemclick.BaseAdapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.find


/**
 * Author: taoyongxiang
 * Date: 2018/12/4
 * Project: BaseAdapter
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
```

在`BaseHolder`中主要用到了两点：

1. `SparseArrayCompat`是Android提供的一个工具类，它可以用来代替`HashMap`进行对象的存储，这样我们每次`getView`可以有效的进行复用`View`，不用每次都去`find`。

    这里的`find`是Kotlin中的`anko`库，用来代替`findViewById()`。

2. 在`getView()`方法中我使用了泛型`T`，最终的返回用了`as`操作符，Kotlin中的`as`操作符很强大，比如返回的是一个`ImageView`，我们在使用这个`T`的时候不用在强转，直接可以调用`ImageView`的方法：

```
    // 获取item中的ImageView
    val image = holder.getView<ImageView>(R.id.item_image)
    image.setImageResource(R.mipmap.ic_launcher)
```

其次我们来看看对`Adapter`封装的整体代码：

```
package com.taonce.onitemclick.BaseAdapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup


/**
 * Author: taoyongxiang
 * Date: 2018/12/4
 * Project: BaseAdapter
 * Desc: RecyclerAdapter基类
 */

abstract class BaseAdapter<T>(private val ctx: Context, private val layoutRes: Int, val mData: MutableList<T>)
    : RecyclerView.Adapter<BaseHolder>() {

    private lateinit var mListener: OnItemClickListener
    private lateinit var mLongListener: OnItemLongClickListener

    fun setOnItemClickListener(mListener: OnItemClickListener) {
        this.mListener = mListener
    }

    fun setOnItemLongClickListener(mLongListener: OnItemLongClickListener) {
        this.mLongListener = mLongListener
    }

    /**
     * 数据和item的绑定交给了convert()方法，将ViewHolder和position传进去
     */
    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        convert(holder, position)
        holder.itemView.setOnClickListener {
            mListener.onItemClick(position)
        }
        holder.itemView.setOnLongClickListener { mLongListener.onItemLongClick(position) }
    }

    /**
     * 通过layout的id生成ViewHolder对象
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        return BaseHolder(LayoutInflater.from(ctx).inflate(layoutRes, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    /**
     * 用来给具体Adapter实现逻辑的抽象方法
     */
    abstract fun convert(holder: BaseHolder, position: Int)

    /**
     * 添加一项数据
     * item:添加的数据
     * position:默认为最后一项
     */
    fun addData(item: T, position: Int = mData.size) {
        mData.add(position, item)
        notifyDataSetChanged()
    }

    /**
     * 添加数据
     * listData：添加的数据
     * isDelete：是否删除原来的数据
     */
    fun addListData(listData: MutableList<T>, isDelete: Boolean) {
        if (isDelete) {
            mData.clear()
        }
        mData.addAll(listData)
        notifyDataSetChanged()
    }

    /**
     * 删除指定项数据
     * position:从0开始
     */
    fun deletePositionData(position: Int) {
        // 防止position越界
        if (position >= 0 && position < mData.size) {
            mData.remove(mData[position])
            notifyDataSetChanged()
        } else {
            Log.d("taonce", "delete item failed, position error!")
        }
    }

    /**
     * 删除所有数据
     */
    fun deleteAllData(){
        mData.removeAll(mData)
        notifyDataSetChanged()
    }

    /**
     * item的点击事件
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    /**
     * item的长按事件
     */
    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int): Boolean
    }
}
```

在`BaseAdapter`中，同样使用了泛型`T`，这个泛型代表的是数据源的类型。

主要实现了以下几个功能：

1.  `item`的单击事件和长按事件，分别用了`OnItemClickListener`和`OnItemLongClickListener`两个接口。

2.  `addData(item: T, position: Int = mData.size)`：用来向列表添加一项数据，添加的位置默认是最后一项，也可以通过`position`指定具体位置

3.  `addListData(listData: MutableList<T>, isDelete: Boolean)`：用来向列表添加多项数据，这里`isDelete`参数是用来判断是否删除之前的所有数据，比如在分页时，上拉加载，是不需要删除之前的数据；而刷新当前界面时，是需要重新加载数据的。所以加了一个标志位。

4.  `deletePositionData(position: Int)`：这个方法是用来删除某项数据的

5.  `deleteAllData()`：删除列表所有数据

这里我们把`itemView`和`data`的绑定都交给了`convert(holder: BaseHolder, position: Int)`方法。下面看看具体的实现：
```
package com.taonce.onitemclick

import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.taonce.onitemclick.BaseAdapter.BaseAdapter
import com.taonce.onitemclick.BaseAdapter.BaseHolder
import kotlin.random.Random


/**
 * Author: taoyongxiang
 * Date: 2018/12/4
 * Project: BaseAdapter
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
```

通过实现`BaseAdapter`类，复写它的`convert()`方法，直接在方法里面获取`ItemView`的子`View`，然后将数据和它绑定。

以后我们在用`Adapter`的时候只需要简单的一步就可以完成之前复写那么多的方法了。

###### 在Activity实现封装的方法
```
package com.taonce.onitemclick

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.taonce.onitemclick.BaseAdapter.BaseAdapter
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
        adapter.setOnItemClickListener(object : BaseAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d("taonce", "click item position is $position, value is ${mData[position]}")
            }
        })
        // 长按事件
        adapter.setOnItemLongClickListener(object : BaseAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int): Boolean {
                Log.d("taonce", "long click position is $position, value is ${mData[position]}")
                return true
            }
        })
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

```

效果图如下：
![简单实现](https://upload-images.jianshu.io/upload_images/6297937-29678f4128673f13.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



#### 写在最后

**每个人不是天生就强大,你若不努力,如何证明自己,加油!**

**Thank You!**

**--Taonce**

**如果你觉得这篇文章对你有所帮助，那么就动动小手指，长按下方的二维码，关注一波吧~~非常期待大家的加入**

![专注Kotlin和Android知识的公众号](https://upload-images.jianshu.io/upload_images/6297937-b17430c03e89b9e5.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)Kotlin封装RecycleView的Adapter