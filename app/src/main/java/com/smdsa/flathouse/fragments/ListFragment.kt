package com.smdsa.flathouse.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.smdsa.flathouse.R
import com.smdsa.flathouse.adapters.FlatDataClass
import com.smdsa.flathouse.adapters.RecycleViewFlatsAdapter
import com.smdsa.flathouse.adapters.SharedPreference
import com.smdsa.flathouse.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: RecycleViewFlatsAdapter
    private lateinit var sharedPreference: SharedPreference
    private var arrayList: ArrayList<FlatDataClass> = ArrayList<FlatDataClass>()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Objects")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater)
        auth = Firebase.auth
        sharedPreference = SharedPreference(requireActivity())
        adapter = RecycleViewFlatsAdapter(arrayList, requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAuth()
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount.toString() == "0") {
                    binding.recycleView.visibility = View.INVISIBLE
                    binding.textNull.visibility = View.VISIBLE
                } else {
                    binding.textNull.visibility = View.INVISIBLE
                    binding.recycleView.visibility = View.VISIBLE
                }
                try{
                    if (snapshot.exists()) {
                        arrayList.clear()
                        for (snap in snapshot.children) {
                            arrayList.add(snap.getValue(FlatDataClass::class.java)!!)
                        }
                    }
                    adapter = RecycleViewFlatsAdapter(arrayList, requireActivity())
                    adapter.setOnRecycleViewClick(object :
                        RecycleViewFlatsAdapter.OnRecycleViewListener {
                        override fun onRecycleViewClick(position: Int) {
                            sharedPreference.save("position", position)
                            findNavController().navigate(R.id.action_listFragment_to_flatFragment)
                        }
                    })
                    binding.recycleView.adapter = adapter
                }

                catch (ex: Exception){}
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        binding.addButton.setOnClickListener {
            try{
                findNavController().navigate(R.id.action_listFragment_to_addFlatFragment)
            }
            catch (ex: IllegalArgumentException){}

        }

        binding.signOutButton.setOnClickListener {
            auth.signOut()
            checkAuth()
        }
    }

    private fun checkAuth(){
        if(auth.currentUser == null){
            findNavController().navigate(R.id.action_listFragment_to_authFragment)
        }
    }
}