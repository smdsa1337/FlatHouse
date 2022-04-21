package com.smdsa.flathouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.ActivityFlatBinding
import java.lang.Exception

class FlatActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityFlatBinding
    private lateinit var sharedPreference: SharedPreference
    private var database: DatabaseReference = FirebaseDatabase.getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Objects")
    private var arrayKeys = ArrayList<String>()
    private var arrayList = ArrayList<FlatDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SharedPreference(this)
        var a = sharedPreference.getValueInt("position")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    arrayList.clear()
                    for (snap in snapshot.children){
                        arrayList.add(snap.getValue(FlatDataClass::class.java)!!)
                        arrayKeys.add(snap.key.toString())
                    }
                    try{
                        Glide.with(this@FlatActivity).load(arrayList[a].Image).centerCrop()
                            .into(binding.objectImage)
                        binding.priceText.text = "Цена: ${arrayList[a].Price}"
                        binding.addressText.text = "Адрес: ${arrayList[a].Address}"
                        binding.areaText.text = "Площадь: ${arrayList[a].Area}"
                        binding.floorText.text = "Этаж: ${arrayList[a].Floor}"
                        binding.price2metrText.text = "Цена за кв. метр: ${arrayList[a].Price2metr}"
                        binding.countRoomsText.text = "Кол-во комнат: ${arrayList[a].CountRooms}"
                    }
                    catch (ex: Exception){
                        Log.e("Exception in FlatActivity: ","$ex")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.removeButton.setOnClickListener {
            database.child(arrayKeys[a]).removeValue().addOnCompleteListener {
                if(it.isSuccessful){
                    sharedPreference.clearSharedPreference()
                    finish()
                }
                else{
                    Log.e("Not successful it in FlatActivity: ","$it")
                }
            }
        }
        binding.editButton.setOnClickListener {
            intent = Intent(this,EditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        sharedPreference.clearSharedPreference()
        super.onBackPressed()
    }
}