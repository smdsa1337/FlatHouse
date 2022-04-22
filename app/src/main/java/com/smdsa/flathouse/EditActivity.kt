package com.smdsa.flathouse

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.ActivityEditBinding
import kotlin.collections.ArrayList

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var progressDialog: ProgressDialog
    private lateinit var image: String
    private lateinit var address: String
    private lateinit var price2metr: String
    private lateinit var floor: String
    private lateinit var flatDataClass: FlatDataClass
    private var arrayKeys: ArrayList<String> = ArrayList<String>()
    private var arrayList: ArrayList<FlatDataClass> = ArrayList<FlatDataClass>()
    private var database: DatabaseReference = FirebaseDatabase.getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Objects")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SharedPreference(this)
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    arrayList.clear()
                    for (snap in snapshot.children){
                        arrayList.add(snap.getValue(FlatDataClass::class.java)!!)
                        arrayKeys.add(snap.key.toString())
                    }
                    var a = sharedPreference.getValueInt("position")
                    binding.priceText.setText("${arrayList[a].Price}")
                    binding.areaText.setText("${arrayList[a].Area}")
                    binding.countRoomsText.setText("${arrayList[a].CountRooms}")
                    image = arrayList[a].Image.toString()
                    address = arrayList[a].Address.toString()
                    price2metr = arrayList[a].Price2metr.toString()
                    floor = arrayList[a].Floor.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.editButton.setOnClickListener {
            if(binding.areaText.text.toString().isNotEmpty()  && binding.countRoomsText.text.toString().isNotEmpty() && binding.priceText.text.toString().isNotEmpty()) {
                if(Integer.parseInt(binding.areaText.text.toString()) > 0){
                    if(Integer.parseInt(binding.priceText.text.toString()) >= Integer.parseInt(binding.areaText.text.toString())){
                        upload()
                    }
                    else{
                        Toast.makeText(this,"Недопустимое значение для цены",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this,"Площадь не может быть меньше единицы",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Вы оставили какое-то поле пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun upload() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Редактирование объявления")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var a = sharedPreference.getValueInt("position")
        try{
            flatDataClass = FlatDataClass(
                Area = binding.areaText.text.toString(),
                Address = address,
                CountRooms = binding.countRoomsText.text.toString(),
                Floor = floor,
                Price = binding.priceText.text.toString(),
                Price2metr = price2metr,
                Image = image)
            database.child(arrayKeys[a]).setValue(flatDataClass).addOnCompleteListener {
                if(it.isSuccessful){
                    if(progressDialog.isShowing){
                        progressDialog.cancel()
                        finish()
                    }
                    else{
                        finish()
                    }
                }
                else{
                    Log.e("Not successful it in EditActivity: ","$it")
                }
            }
        }
        catch (ex: Exception){
            if(progressDialog.isShowing){
                progressDialog.cancel()
            }
            Log.e("Exception in EditActivity.kt: ","$ex")
        }
    }
}