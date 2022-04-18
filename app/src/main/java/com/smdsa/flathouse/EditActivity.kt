package com.smdsa.flathouse

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.ActivityEditBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreference: SharedPreference
    private lateinit var ImageUri : Uri
    var NewImage : Boolean = false
    var arrayKeys = ArrayList<String>()
    var image = ""
    var prevImage = ""
    val myRef : DatabaseReference = FirebaseDatabase
        .getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Objects")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SharedPreference(this)
        var arrayList = ArrayList<FlatDataClass>()
        database = FirebaseDatabase.getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Objects")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    arrayList.clear()
                    for (snap in snapshot.children){
                        val objects = snap.getValue(FlatDataClass::class.java)
                        val keys = snap.key
                        arrayList.add(objects!!)
                        arrayKeys.add(keys!!)
                    }
                    var a = sharedPreference.getValueInt("position")
                    binding.priceText.setText("${arrayList[a].Price}")
                    binding.addressText.setText("${arrayList[a].Address}")
                    binding.areaText.setText("${arrayList[a].Area}")
                    binding.floorText.setText("${arrayList[a].Floor}")
                    binding.price2metr.setText("${arrayList[a].Price2metr}")
                    binding.countRoomsText.setText("${arrayList[a].CountRooms}")
                    prevImage = arrayList[a].Image.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.cardView.setOnClickListener {
            selectImage()
            NewImage = true
        }
        binding.editButton.setOnClickListener {
            if(binding.areaText.text.toString().isNotEmpty() && binding.addressText.text.toString().isNotEmpty() &&
                binding.countRoomsText.text.toString().isNotEmpty() && binding.floorText.text.toString().isNotEmpty() &&
                binding.priceText.text.toString().isNotEmpty() && binding.price2metr.text.toString().isNotEmpty()) {
                uploadImage()
            }
            else{
                Toast.makeText(this,"Вы оставили какое-то поле пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImage() {
        intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "image/*"
        }
        startActivityForResult(intent, 100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            ImageUri = data?.data!!
            binding.textInCard.text = "Изображение выбрано"
        }
    }
    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Редактирование объявления")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var a = sharedPreference.getValueInt("position")
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH-mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance("gs://flathouse-d7d8f.appspot.com/").getReference(
            fileName
        )
        try{
            if(NewImage){
                storageReference.putFile(ImageUri).addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener {
                        image = it.toString()
                        var flatDataClass = FlatDataClass(
                            Area = binding.areaText.text.toString(),
                            Address = binding.addressText.text.toString(),
                            CountRooms = binding.countRoomsText.text.toString(),
                            Floor = binding.floorText.text.toString(),
                            Price = binding.priceText.text.toString(),
                            Price2metr = binding.price2metr.text.toString(),
                            Image = image)

                        if(image != ""){
                            flatDataClass.Image = image
                        }
                        else{
                            flatDataClass.Image = prevImage
                        }
                        myRef.child(arrayKeys[a].toString()).setValue(flatDataClass).addOnCompleteListener {
                            if(it.isSuccessful){
                                finish()
                            }
                            else{
                                Log.e("Check","$it")
                            }
                        }.addOnCanceledListener {

                        }
                    }
                }
            }
            else{
                var flatDataClass = FlatDataClass(
                    Area = binding.areaText.text.toString(),
                    Address = binding.addressText.text.toString(),
                    CountRooms = binding.countRoomsText.text.toString(),
                    Floor = binding.floorText.text.toString(),
                    Price = binding.priceText.text.toString(),
                    Price2metr = binding.price2metr.text.toString(),
                    Image = prevImage)
                myRef.child(arrayKeys[a].toString()).setValue(flatDataClass).addOnCompleteListener {
                    if(it.isSuccessful){
                        finish()
                    }
                    else{
                        Log.e("Check","$it")
                    }
                }.addOnCanceledListener {

                }
            }
        }
        catch (ex: Exception){
            if(progressDialog.isShowing){
                progressDialog.cancel()
            }
            Log.e("Check","$ex")
        }
    }
}
