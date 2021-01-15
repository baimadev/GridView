package com.baima.jetpack

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)


        val linearLayout = findViewById<LinearLayout>(R.id.rak_icon_grid)
        val width = linearLayout.measuredWidth
        GridViewHelper(linearLayout)
    }


}

val caches = SparseArray<View>()

class MyViewCacheExtension : RecyclerView.ViewCacheExtension(){
    override fun getViewForPositionAndType(
        recycler: RecyclerView.Recycler,
        position: Int,
        type: Int
    ): View? {

        return caches.get(position)
    }
}

class MyRecyclerViewAdapter(val list:MutableList<String>) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        const val DEFAULT_TYPE = 0
        const val SPECITAL_TYPE = 1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.banner1)
        val text = itemView.findViewById<TextView>(R.id.text)
    }

    inner class SpecitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.banner1)
        val text = itemView.findViewById<TextView>(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.e("xia","create viewHolder")
        if(viewType == DEFAULT_TYPE){
            val view  = LayoutInflater.from(parent.context).inflate(R.layout.home_banner_item,parent,false)
            return ViewHolder(view);
        }else{
            val view  = LayoutInflater.from(parent.context).inflate(R.layout.home_banner_item,parent,false)
            return SpecitalViewHolder(view);
        }
    }

    override fun getItemCount(): Int {
        return 25
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) SPECITAL_TYPE
        else DEFAULT_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.e("xia","bindViewHolder $position")
        if(holder is ViewHolder){
            holder.text.text = position.toString()
            Glide.with(holder.imageView.context)
                .load(list[0])
                .into(holder.imageView)
        }else if(holder is SpecitalViewHolder){
            caches.put(position,holder.itemView)
            holder.text.text = position.toString()
            Glide.with(holder.imageView.context)
                .load(list[5])
                .into(holder.imageView)
        }
    }

}
