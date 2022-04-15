package com.smdsa.flathouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smdsa.flathouse.databinding.ActivityAuthBinding
import java.lang.Exception

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        checkAuth()
        binding.toRegText.setOnClickListener {
            intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }
        binding.authButton.setOnClickListener {
            try {
                if(binding.login.text.toString().isNotEmpty() && binding.password.text.toString().isNotEmpty()){
                    auth.signInWithEmailAndPassword(binding.login.text.toString(),binding.password.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                            intent = Intent(this, ListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this,"Неверный логин или пароль",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Вы не ввели логин или пароль",Toast.LENGTH_SHORT).show()
                }
            }
            catch (ex: Exception){
                Log.e("Check","$ex")
            }
        }
    }

    private fun checkAuth() {
        if(auth.currentUser != null){
            intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}