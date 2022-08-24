package com.smdsa.flathouse.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.smdsa.flathouse.R
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.FragmentEditBinding

class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var image: String
    private lateinit var address: String
    private lateinit var price2metr: String
    private lateinit var floor: String
    private lateinit var flatDataClass: FlatDataClass
    private var arrayKeys: ArrayList<String> = ArrayList<String>()
    private var arrayList: ArrayList<FlatDataClass> = ArrayList<FlatDataClass>()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Objects")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater)
        sharedPreference = SharedPreference(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try{
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        arrayList.clear()
                        for (snap in snapshot.children){
                            arrayList.add(snap.getValue(FlatDataClass::class.java)!!)
                            arrayKeys.add(snap.key.toString())
                        }
                        val a = sharedPreference.getValue("position")
                        binding.priceText.setText("${arrayList[a].Price}")
                        binding.areaText.setText("${arrayList[a].Area}")
                        binding.countRoomsText.setText("${arrayList[a].CountRooms}")
                        image = arrayList[a].Image.toString()
                        address = arrayList[a].Address.toString()
                        price2metr = arrayList[a].Price2metr.toString()
                        floor = arrayList[a].Floor.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    findNavController().navigate(R.id.action_editFragment_to_listFragment)
                }
            })
        }
        catch (ex: Exception){
            findNavController().navigate(R.id.action_editFragment_to_listFragment)
        }

        binding.editButton.setOnClickListener {
            if(binding.areaText.text.toString().isNotEmpty()  && binding.countRoomsText.text.toString().isNotEmpty() && binding.priceText.text.toString().isNotEmpty()) {
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
            }
        }
    }

    private fun upload() {
        val progressBar = ProgressBar(activity)
        progressBar.isIndeterminate = true

        val progressDialog = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setTitle("Создание объявления")
            .setView(progressBar).create()

        progressDialog.show()

        val a = sharedPreference.getValue("position")

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
                        findNavController().popBackStack()
                    }

                    else{
                        findNavController().popBackStack()
                    }
                }
            }
        }
        catch (ex: Exception){
            if(progressDialog.isShowing){
                progressDialog.cancel()
                findNavController().navigate(R.id.action_editFragment_to_listFragment)
            }
        }
    }
}