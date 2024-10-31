package com.tugasakhirrifgi.lsdetect.ui.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.tugasakhirrifgi.lsdetect.MainActivity
import com.tugasakhirrifgi.lsdetect.databinding.ActivityLoginBinding
import com.tugasakhirrifgi.lsdetect.helper.LoadingDialog
import com.tugasakhirrifgi.lsdetect.ui.register.RegisterActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()

        auth = Firebase.auth


        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edtEmailLogin.error = "Email harus di isi"
                    binding.edtEmailLogin.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.edtEmailLogin.error = "Email tidak valid"
                    binding.edtEmailLogin.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.edtPasswordLogin.error = "Password harus di isi"
                    binding.edtPasswordLogin.requestFocus()
                    return@setOnClickListener
                }
                password.length < 8 -> {
                    binding.edtPasswordLogin.error = "Karakter harus lebih dari 8"
                    binding.edtPasswordLogin.requestFocus()
                    return@setOnClickListener
                }
            }

            loginAccountFirebase(email, password)
        }

        binding.tvToRegister.setOnClickListener {
            val intentToRegis = Intent(this, RegisterActivity::class.java)
            startActivity(intentToRegis)
        }

    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun loginAccountFirebase(email: String, password: String) {
        val loading = LoadingDialog(this)
        loading.startLoading()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    loading.isDismiss()
                    showToast("Login Berhasil")
                    val intentLogin = Intent(this, MainActivity::class.java)
                    startActivity(intentLogin)
                    finish()
                } else {
                    loading.isDismiss()
                    showToast("${it.exception?.message}")
                }
            }
    }



    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
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
        private const val TAG = "LoginActivity"
    }
}