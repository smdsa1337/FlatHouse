package com.smdsa.flathouse.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smdsa.flathouse.R

class RecycleViewFlatsAdapter(private val data: ArrayList<FlatDataClass>, private val context: Context) :
    RecyclerView.Adapter<RecycleViewFlatsAdapter.VH>() {

    private lateinit var mListener: OnRecycleViewListener

    interface OnRecycleViewListener{
        fun onRecycleViewClick (position: Int)
    }

    fun setOnRecycleViewClick(listener: OnRecycleViewListener){
        mListener = listener
    }

    class VH(itemView: View, listener: OnRecycleViewListener) : RecyclerView.ViewHolder(itemView){
        var image: ImageView = itemView.findViewById(R.id.objectImage)
        var price: TextView = itemView.findViewById(R.id.priceText)
        var address: TextView = itemView.findViewById(R.id.addressText)
        var area: TextView = itemView.findViewById(R.id.areaText)
        init {
            itemView.setOnClickListener {
                listener.onRecycleViewClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.flat_item, parent, false), mListener)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(context).load(data[holder.adapterPosition].Image).centerCrop()
            .into(holder.image)
        holder.price.text = "Цена: " + data[holder.adapterPosition].Price + " рублей"
        holder.address.text ="Адрес: " + data[holder.adapterPosition].Address
        holder.area.text ="Площадь: " + data[holder.adapterPosition].Area
    }

    override fun getItemCount(): Int {
        return data.size
    }
}