package com.smdsa.flathouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.RecycleViewFlatsAdapter
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.ActivityListBinding

class ListActivity : AppCompatActivity(){

    private lateinit var binding: ActivityListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: RecycleViewFlatsAdapter
    private lateinit var sharedPreference: SharedPreference
    private var arrayList: ArrayList<FlatDataClass> = ArrayList<FlatDataClass>()
    private var database: DatabaseReference= FirebaseDatabase.getInstance("https://flathouse-d7d8f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Objects")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference =  SharedPreference(this)
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount.toString() == "0") {
                    binding.recycleView.visibility = View.INVISIBLE
                    binding.textNull.visibility = View.VISIBLE
                } else {
                    binding.textNull.visibility = View.INVISIBLE
                    binding.recycleView.visibility = View.VISIBLE
                }
                if (snapshot.exists()) {
                    arrayList.clear()
                    for (snap in snapshot.children) {
                        arrayList.add(snap.getValue(FlatDataClass::class.java)!!)
                    }
                }
                adapter = RecycleViewFlatsAdapter(arrayList, this@ListActivity)
                adapter.setOnRecycleViewClick(object :
                    RecycleViewFlatsAdapter.OnRecycleViewListener {
                    override fun onRecycleViewClick(position: Int) {
                        sharedPreference.save("position", position)
                        intent = Intent(this@ListActivity, FlatActivity::class.java)
                        startActivity(intent)
                    }
                })
                binding.recycleView.adapter = adapter
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