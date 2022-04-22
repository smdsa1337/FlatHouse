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
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AddFlatActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var binding: ActivityAddFlatBinding
    private lateinit var image: String
    private lateinit var progressDialog: ProgressDialog
    private lateinit var flatDataClass: FlatDataClass
    private var database: DatabaseReference = FirebaseDatabase.getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Objects")
    private var storageReference = FirebaseStorage.getInstance("gs://flathouse-d7d8f.appspot.com/").getReference(SimpleDateFormat("yyyy_MM_dd_HH-mm_ss", Locale.getDefault()).format(Date()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFlatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cardView.setOnClickListener {
            selectImage()
        }
        binding.addButton.setOnClickListener {
            if(binding.areaText.text.toString().isNotEmpty() && binding.addressText.text.toString().isNotEmpty() && binding.countRoomsText.text.toString().isNotEmpty() && binding.floorText.text.toString().isNotEmpty() && binding.priceText.text.toString().isNotEmpty()){
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

    private fun selectImage(){
        intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "image/*"
        }
        startActivityForResult(intent, 100)
    }

    private fun upload() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Создание объявления")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try{
            storageReference.putFile(imageUri).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    image = it.toString()
                    val a = Integer.parseInt(binding.priceText.text.toString()) / Integer.parseInt(binding.areaText.text.toString())
                    flatDataClass = FlatDataClass(
                        Area = binding.areaText.text.toString(),
                        Address = binding.addressText.text.toString(),
                        CountRooms = binding.countRoomsText.text.toString(),
                        Floor = binding.floorText.text.toString(),
                        Price = binding.priceText.text.toString(),
                        Price2metr = a.toString(),
                        Image = image)
                    database.child(getKey("Objects").push().key.toString()).setValue(flatDataClass).addOnCompleteListener {
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
                            Log.e("It in AddFlatActivity.kt is not successful: ","$it")
                        }
                    }
                }
            }
        }
        catch (ex: UninitializedPropertyAccessException){
            if(progressDialog.isShowing){
                progressDialog.cancel()
            }
            Toast.makeText(this,"Изображение не было выбрано", Toast.LENGTH_SHORT).show()
        }
        catch(ex: Exception){
            if(progressDialog.isShowing){
                progressDialog.cancel()
            }
            Log.e("Exception in AddFlatActivity.kt: ","$ex")
        }
    }

    private fun getKey(path: String): DatabaseReference {
        return FirebaseDatabase
            .getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(path)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            imageUri = data?.data!!
            binding.textInCard.text = "Изображение выбрано"
        }
    }
}