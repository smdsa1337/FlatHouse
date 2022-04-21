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

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var adapter: RecycleViewFlatsAdapter
    private lateinit var sharedPreference: SharedPreference
    private lateinit var arrayList: ArrayList<FlatDataClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference =  SharedPreference(this)
        arrayList = ArrayList<FlatDataClass>()
        database = FirebaseDatabase.getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Objects")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    arrayList.clear()
                    for (snap in snapshot.children) {
                        arrayList.add(snap.getValue(FlatDataClass::class.java)!!)
                    }
                }
                try{
                    adapter = RecycleViewFlatsAdapter(arrayList,this@ListActivity)
                    adapter.setOnRecycleViewClick(object : RecycleViewFlatsAdapter.OnRecycleViewListener{
                        override fun onRecycleViewClick(position: Int) {
                            sharedPreference.save("position",position)
                            intent = Intent(this@ListActivity, FlatActivity::class.java)
                            startActivity(intent)
                        }
                    })
                    binding.recycleView.adapter = adapter
                }
                catch (ex: Exception){
                    Log.e("Exception in ListActivity.kt: ","$ex")
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.signOutButton.setOnClickListener {
            auth = Firebase.auth
            auth.signOut()
            checkAuth()
        }
        binding.addButton.setOnClickListener {
            intent = Intent(this, AddFlatActivity::class.java)
            startActivity(intent)
            onRestart()
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