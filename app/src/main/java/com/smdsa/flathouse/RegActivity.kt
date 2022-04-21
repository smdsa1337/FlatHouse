package com.smdsa.flathouse

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private lateinit var alertDialogBuilder : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        alertDialogBuilder = AlertDialog.Builder(this)
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
                            if(it.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted."){
                                Toast.makeText(this,"Неправильно набрана почта", Toast.LENGTH_SHORT).show()
                            }
                            if(it.exception.toString() == "com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]"){
                                Toast.makeText(this,"Неподходящий пароль", Toast.LENGTH_SHORT).show()
                            }
                            if(it.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."){
                                Toast.makeText(this,"Данная почта уже используется", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Вы не ввели логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
            catch (ex: Exception){
                Log.e("Exception in RegActivity.kt: ","$ex")
            }
        }
    }
}