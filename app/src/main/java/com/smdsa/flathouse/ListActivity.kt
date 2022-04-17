package com.smdsa.flathouse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.RecycleViewFlatsAdapter
import com.smdsa.flathouse.databinding.ActivityListBinding
import java.lang.Exception

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)
        checkAuth()
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
                }
                try{
                    binding.recycleView.adapter = RecycleViewFlatsAdapter(arrayList, this@ListActivity)
                }
                catch (ex: Exception){
                    Log.e("Check","$ex")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.signOutButton.setOnClickListener {
            auth.signOut()
            checkAuth()
        }
        binding.addButton.setOnClickListener {
            intent = Intent(this, AddFlatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkAuth() {
        if(auth.currentUser == null){
            intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}