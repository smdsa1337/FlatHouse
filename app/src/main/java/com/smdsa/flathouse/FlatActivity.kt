package com.smdsa.flathouse

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.ActivityFlatBinding
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


class FlatActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityFlatBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreference : SharedPreference =  SharedPreference(this)
        var arrayList = ArrayList<FlatDataClass>()
        database = FirebaseDatabase.getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Objects")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    arrayList.clear()
                    for (snap in snapshot.children){
                        val objects = snap.getValue(FlatDataClass::class.java)
                        arrayList.add(objects!!)
                    }
                    var a = sharedPreference.getValueInt("position")
                    sharedPreference.clearSharedPreference()
                    Glide.with(this@FlatActivity).load(arrayList[a].Image).centerCrop()
                        .into(binding.objectImage)
                    binding.priceText.text = "Цена: ${arrayList[a].Price}"
                    binding.addressText.text = "Адрес: ${arrayList[a].Address}"
                    binding.areaText.text = "Площадь: ${arrayList[a].Area}"
                    binding.floorText.text = "Этаж: ${arrayList[a].Floor}"
                    binding.price2metrText.text = "Цена за кв. метр: ${arrayList[a].Price2metr}"
                    binding.countRoomsText.text = "Кол-во комнат: ${arrayList[a].CountRooms}"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}