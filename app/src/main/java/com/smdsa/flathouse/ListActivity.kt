package com.smdsa.flathouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.RecycleViewFlatsAdapter
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.ActivityListBinding
import java.lang.Exception

class ListActivity : AppCompatActivity(), RecycleViewFlatsAdapter.OnRecycleViewListener {

    private lateinit var binding: ActivityListBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var adapter: RecycleViewFlatsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)
        checkAuth()
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
                }
                try{
                    adapter = RecycleViewFlatsAdapter(arrayList,this@ListActivity)
                    binding.recycleView.adapter = adapter
                    adapter.setOnRecycleViewClick(object : RecycleViewFlatsAdapter.OnRecycleViewListener{
                        override fun onRecycleViewClick(position: Int) {
                            sharedPreference.save("position",position)
                            intent = Intent(this@ListActivity, FlatActivity::class.java)
                            startActivity(intent)
                        }
                    })
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

    override fun onRecycleViewClick(position: Int) {
        Log.e("Check","$position")
        intent = Intent(this, FlatActivity::class.java)
        startActivity(intent)
    }
}