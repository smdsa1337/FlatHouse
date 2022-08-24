package com.smdsa.flathouse.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.smdsa.flathouse.R
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.FragmentFlatBinding

class FlatFragment : Fragment() {

    private lateinit var binding: FragmentFlatBinding
    private lateinit var sharedPreference: SharedPreference
    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Objects")
    private var arrayKeys = ArrayList<String>()
    private var arrayList = ArrayList<FlatDataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFlatBinding.inflate(inflater)
        sharedPreference = SharedPreference(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val a = sharedPreference.getValue("position")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    arrayList.clear()
                    for (snap in snapshot.children){
                        arrayList.add(snap.getValue(FlatDataClass::class.java)!!)
                        arrayKeys.add(snap.key.toString())
                    }
                    try{
                        Glide.with(requireActivity()).load(arrayList[a].Image).centerCrop()
                            .into(binding.objectImage)
                        binding.priceText.text = "Цена: ${arrayList[a].Price}"
                        binding.addressText.text = "Адрес: ${arrayList[a].Address}"
                        binding.areaText.text = "Площадь: ${arrayList[a].Area}"
                        binding.floorText.text = "Этаж: ${arrayList[a].Floor}"
                        binding.price2metrText.text = "Цена за кв. метр: ${arrayList[a].Price2metr}"
                        binding.countRoomsText.text = "Кол-во комнат: ${arrayList[a].CountRooms}"
                    }
                    catch (ex: Exception){
                        findNavController().popBackStack()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                findNavController().popBackStack()
            }
        })

        binding.removeButton.setOnClickListener {
            binding.editButton.isEnabled = false
            val progressBar = ProgressBar(activity)
            progressBar.isIndeterminate = true

            val builder = AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("Удаление объявления")
                .setView(progressBar).create()

            builder.show()

            try{
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(arrayList[a].Image.toString())
                storageReference.delete().addOnCompleteListener {
                    if(it.isSuccessful){
                        database.child(arrayKeys[a]).removeValue().addOnCompleteListener { it2 ->
                            if(it2.isSuccessful){
                                if(builder.isShowing){
                                    builder.cancel()
                                    sharedPreference.clearSharedPreference()
                                    try{
                                        findNavController().navigate(R.id.action_flatFragment_to_listFragment)
                                    }
                                    catch (ex: Exception){
                                        builder.cancel()
                                        findNavController().popBackStack()
                                    }
                                }
                            }
                            else{
                                builder.cancel()
                            }
                        }
                    }
                }
            }
            catch (ex: Exception){
                findNavController().popBackStack()
            }

        }

        binding.editButton.setOnClickListener {
            binding.removeButton.isEnabled = false
            findNavController().navigate(R.id.action_flatFragment_to_editFragment)
            binding.removeButton.isEnabled = true
        }
    }
}