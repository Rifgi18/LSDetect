package com.tugasakhirrifgi.lsdetect.ui.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.databinding.ActivityRegisterBinding
import com.tugasakhirrifgi.lsdetect.helper.LoadingDialog
import com.tugasakhirrifgi.lsdetect.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        auth = Firebase.auth
        firestore = Firebase.firestore

        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            val username = binding.edtUsernameRegister.text.toString()

            when {
                username.isEmpty() -> {
                    binding.edtUsernameRegister.error = "Username harus di isi"
                    binding.edtUsernameRegister.requestFocus()
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    binding.edtEmailRegister.error = "Email harus di isi"
                    binding.edtEmailRegister.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.edtEmailRegister.error = "Email tidak valid"
                    binding.edtEmailRegister.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.edtPasswordRegister.error = "Password harus di isi"
                    binding.edtPasswordRegister.requestFocus()
                    return@setOnClickListener
                }
                password.length < 8 -> {
                    binding.edtPasswordRegister.error = "Karakter harus lebih dari 8"
                    binding.edtPasswordRegister.requestFocus()
                    return@setOnClickListener
                }
            }

            registerAccountFirebase(email, password, username)
        }

        binding.tvToLogin.setOnClickListener {
            val intentToLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentToLogin)
            finish()
        }
    }


    private fun registerAccountFirebase(email: String, password: String, username: String) {

        val loading = LoadingDialog(this)
        loading.startLoading()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    showToast("Registrasi Berhasil")
                    val userID = auth.currentUser?.uid
                    documentReference = firestore.collection("users").document(userID!!)
                    val user = hashMapOf(
                        "fName" to username,
                        "email" to email,
                        "statusUser" to "Belum di isi",
                        "descriptionUser" to "Belum di isi",
                    )
                    documentReference.set(user)
                        .addOnSuccessListener {db ->
                        Log.d(TAG, "onSuccess: user profile is created$userID")
                            loading.isDismiss()
                        }
                        .addOnFailureListener {e ->
                            Log.d(TAG, "onFailure: ")
                            loading.isDismiss()
                        }
                    val intentLogin = Intent(this, LoginActivity::class.java)
                    startActivity(intentLogin)

                } else {
                    showToast("${it.exception?.message}")
                    loading.isDismiss()
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        const val TAG = "RegisterActivity"
    }

}