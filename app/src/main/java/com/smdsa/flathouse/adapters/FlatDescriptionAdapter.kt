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

class FlatDescriptionAdapter(val data: ArrayList<FlatDataClass>, val context: Context) :
    RecyclerView.Adapter<FlatDescriptionAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.objectImage)
        var price: TextView = itemView.findViewById(R.id.priceText)
        var address: TextView = itemView.findViewById(R.id.addressText)
        var area: TextView = itemView.findViewById(R.id.areaText)
        var floor : TextView = itemView.findViewById(R.id.floorText)
        var Price2metr : TextView = itemView.findViewById(R.id.price2metrText)
        var countRooms : TextView = itemView.findViewById(R.id.countRoomsText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.flat_description_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(context).load(data[holder.adapterPosition].Image).centerCrop()
            .into(holder.image)
        holder.price.text = "Цена: "+data[holder.adapterPosition].Price + " рублей"
        holder.address.text ="Адресс: " + data[holder.adapterPosition].Address
        holder.area.text ="Площадь: " + data[holder.adapterPosition].Area
        holder.floor.text = "Этаж: " + data[holder.adapterPosition].Floor
        holder.Price2metr.text = "Цена за кв. метр: " + data[holder.adapterPosition].Price2metr
        holder.countRooms.text = "Количество комнат: " + data[holder.adapterPosition].CountRooms
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
