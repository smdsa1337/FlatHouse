package com.smdsa.flathouse.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.smdsa.flathouse.R
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.databinding.FragmentAddFlatBinding
import java.text.SimpleDateFormat
import java.util.*

class AddFlatFragment : Fragment() {

    private lateinit var binding: FragmentAddFlatBinding
    private lateinit var imageUri: Uri
    private lateinit var image: String
    private lateinit var flatDataClass: FlatDataClass
    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Objects")
    private var storageReference = FirebaseStorage.getInstance().getReference(SimpleDateFormat("yyyy_MM_dd_HH-mm_ss", Locale.getDefault()).format(Date()))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFlatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardView.setOnClickListener {
            selectImage()
        }

        binding.addButton.setOnClickListener {
            binding.addButton.isEnabled = false
            if(binding.areaText.text.toString().isNotEmpty() && binding.addressText.text.toString().isNotEmpty() && binding.countRoomsText.text.toString().isNotEmpty() && binding.floorText.text.toString().isNotEmpty() && binding.priceText.text.toString().isNotEmpty()){
                if(Integer.parseInt(binding.areaText.text.toString()) > 0){
                    if(Integer.parseInt(binding.priceText.text.toString()) >= Integer.parseInt(binding.areaText.text.toString())){
                        upload()
                    }
                    else{
                        Toast.makeText(activity,"Недопустимое значение для цены",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(activity,"Площадь не может быть меньше единицы",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(activity,"Вы оставили какое-то поле пустым", Toast.LENGTH_SHORT).show()
                binding.addButton.isEnabled = true
            }
        }
    }

    private fun selectImage(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "image/*"
        }
        startActivityForResult(intent, 100)
    }

    private fun upload() {
        val progressBar = ProgressBar(activity)
        progressBar.isIndeterminate = true

        val alertDialog = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setTitle("Создание объявления")
            .setView(progressBar).create()

        alertDialog.show()

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
                    database.child(getKey("Objects").push().key.toString()).setValue(flatDataClass).addOnCompleteListener { it2 ->
                        if(it2.isSuccessful){
                            if(alertDialog.isShowing){
                                alertDialog.cancel()
                                try{
                                    findNavController().navigate(R.id.action_addFlatFragment_to_listFragment)
                                }
                                catch (ex: Exception){}
                                binding.addButton.isEnabled = true
                            }
                            else{
                                try{
                                    findNavController().navigate(R.id.action_addFlatFragment_to_listFragment)
                                }
                                catch (ex: Exception){
                                    findNavController().popBackStack()
                                }
                                binding.addButton.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
        catch (ex: UninitializedPropertyAccessException){
            if(alertDialog.isShowing){
                alertDialog.cancel()
            }
            binding.addButton.isEnabled = true
            Toast.makeText(activity,"Изображение не было выбрано", Toast.LENGTH_SHORT).show()
        }
        catch(ex: Exception){
            if(alertDialog.isShowing){
                alertDialog.cancel()
            }
            binding.addButton.isEnabled = true
            Toast.makeText(activity,"Произошла ошибка, повторите попытку позже", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getKey(path: String): DatabaseReference {
        return FirebaseDatabase
            .getInstance()
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