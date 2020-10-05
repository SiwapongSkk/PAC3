package com.example.appconnect

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class RvAdapter(val context:Activity,val list:ArrayList<Anime>) : RecyclerView.Adapter<ViewHolder>(){

    val option = RequestOptions().centerCrop().placeholder(R.drawable.ic_launcher_background).error(android.R.drawable.ic_dialog_alert)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = list[position].name
        //holder.tvRating.text = list[position].rating
        holder.tvCate.text = list[position].category
        holder.tvStudio.text = list[position].studio

        Glide.with(context).load(list[position].img).apply(option).into(holder.imgThumb)
    }
}
class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val tvName = itemView.findViewById<TextView>(R.id.rowname)
    //val tvRating = itemView.findViewById<TextView>(R.id.rating)
    val tvCate = itemView.findViewById<TextView>(R.id.categorie)
    val tvStudio = itemView.findViewById<TextView>(R.id.studio)
    val imgThumb = itemView.findViewById<ImageView>(R.id.thumbnail)

}