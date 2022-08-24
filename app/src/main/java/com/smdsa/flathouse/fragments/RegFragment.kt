package com.smdsa.flathouse.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.smdsa.flathouse.R
import com.smdsa.flathouse.databinding.FragmentRegBinding

class RegFragment : Fragment() {

    private lateinit var binding: FragmentRegBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegBinding.inflate(inflater)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.regButton.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            if(login.isNotEmpty() && password.isNotEmpty()){
                auth.createUserWithEmailAndPassword(login, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        try{
                            findNavController().navigate(R.id.action_regFragment_to_listFragment)
                        }
                        catch (ex: IllegalStateException){}
                    }
                    else{
                        when(it.exception.toString()){
                            "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted." ->
                                Toast.makeText(activity,"Неправильно набрана почта", Toast.LENGTH_SHORT).show()

                            "com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]" ->
                                Toast.makeText(activity,"Неподходящий пароль", Toast.LENGTH_SHORT).show()

                            "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account." ->
                                Toast.makeText(activity,"Данная почта уже используется", Toast.LENGTH_SHORT).show()

                            else -> Toast.makeText(activity,"Проверьте подключение к интернету и повторите попытку позже", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else{
                Toast.makeText(activity,"Вы оставили какое-то поле пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }
}