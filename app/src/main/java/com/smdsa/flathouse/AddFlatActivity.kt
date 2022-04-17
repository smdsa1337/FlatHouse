package com.smdsa.flathouse

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.databinding.ActivityAddFlatBinding
import java.text.SimpleDateFormat
import java.util.*

class AddFlatActivity : AppCompatActivity() {

    private lateinit var ImageUri : Uri
    private lateinit var binding: ActivityAddFlatBinding
    val myRef : DatabaseReference = FirebaseDatabase
        .getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Objects")
    var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFlatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addPhoto.setOnClickListener {
            selectImage()
        }
        binding.addButton.setOnClickListener {
            uploadImage()
        }
    }

    private fun getFirebaseReference(path: String): DatabaseReference {
        return FirebaseDatabase
            .getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(path)
    }

    private fun selectImage() {
        intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "image/*"
        }
        startActivityForResult(intent, 100)
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Upload photo")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH-mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance("gs://flathouse-d7d8f.appspot.com/").getReference(
            fileName
        )
        storageReference.putFile(ImageUri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                image = it.toString()
                if(binding.areaText.text.toString().isNotEmpty() &&
                    binding.addressText.text.toString().isNotEmpty() &&
                    binding.countRoomsText.text.toString().isNotEmpty() &&
                    binding.floorText.text.toString().isNotEmpty() &&
                    binding.priceText.text.toString().isNotEmpty() &&
                    binding.price2metr.text.toString().isNotEmpty()){
                    var flatDataClass = FlatDataClass(
                        Area = binding.areaText.text.toString(),
                        Address = binding.addressText.text.toString(),
                        CountRooms = binding.countRoomsText.text.toString(),
                        Floor = binding.floorText.text.toString(),
                        Price = binding.priceText.text.toString(),
                        Price2metr = binding.price2metr.text.toString(),
                        Image = image)
                    var key = getFirebaseReference("News").push().key
                    myRef.child(key.toString()).setValue(flatDataClass).addOnCompleteListener {
                        if(it.isSuccessful){

                        }
                        else{
                            Log.e("Check","$it")
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Вы оставили какое-то поле пустым",Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }.addOnFailureListener{
            Log.e("Check","$it")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            ImageUri = data?.data!!
        }
    }
}