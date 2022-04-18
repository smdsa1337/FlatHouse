package com.smdsa.flathouse

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smdsa.flathouse.databinding.ActivityRegBinding
import java.lang.Exception

class RegActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Правила регистрации")
            .setMessage("Для того, чтобы успешно зарегистрироваться в Flat House, необходимо указать почту в поле ввода логин, и пароль (минимум 6 символов)")
            .setCancelable(true).setPositiveButton("Ок, понял") { dialog, which ->

            }.show()
        binding.regButton.setOnClickListener {
            try {
                if(binding.login.text.toString().isNotEmpty() && binding.password.text.toString().isNotEmpty()){
                    auth.createUserWithEmailAndPassword(binding.login.text.toString(),binding.password.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                            intent = Intent(this, ListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this,"Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Вы не ввели логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
            catch (ex: Exception){
                Log.e("Check","$ex")
            }
        }
    }
}