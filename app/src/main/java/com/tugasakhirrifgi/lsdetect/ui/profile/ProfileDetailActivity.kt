package com.tugasakhirrifgi.lsdetect.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.tugasakhirrifgi.lsdetect.databinding.ActivityProfileDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.helper.LoadingDialog
import java.io.ByteArrayOutputStream

class ProfileDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference
    private lateinit var user: FirebaseUser
    private lateinit var storageRef: StorageReference

    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = Firebase.firestore
        user = auth.currentUser!!
        storageRef = FirebaseStorage.getInstance().reference

        val getData = intent
        val usernameEdit = getData.getStringExtra("fName")
        val emailEdit = getData.getStringExtra("email")
        val statusEdit = getData.getStringExtra("statusUser")
        val descriptionEdit = getData.getStringExtra("descriptionUser")

        binding.edtUsernameEdit.setText(usernameEdit)
        binding.tvDisplayEmail.text = emailEdit
        binding.edtStatusEdit.setText(statusEdit)
        binding.edtDescriptioonEdit.setText(descriptionEdit)


        val imageRef = storageRef.child("images/${user.uid}/profile/profile.jpg")
        imageRef.downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .into(binding.profileImage)
        }


        Log.d(TAG, "username: $usernameEdit")


        binding.btnChangeProfilePicture.setOnClickListener {
            startGallery()
        }

        binding.btnSave.setOnClickListener {

            val loading = LoadingDialog(this)
            loading.startLoading()

            val username = binding.edtUsernameEdit.text.toString()
            val status = binding.edtStatusEdit.text.toString()
            val description = binding.edtDescriptioonEdit.text.toString()

            when {
                username.isEmpty() -> {
                    binding.edtUsernameEdit.error = "Username harus di isi"
                    binding.edtUsernameEdit.requestFocus()
                    return@setOnClickListener
                }
            }

            if (bitmap != null) {
                val profileImgRef = storageRef.child("images/${user.uid}/profile/profile.jpg")

                val imageBitmap = bitmap
                val baos = ByteArrayOutputStream()
                imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                profileImgRef.putBytes(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Berhasil upload gambar", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener{
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
            }


            documentReference = firestore.collection("users").document(user.uid)


            val edited = hashMapOf(
                "fName" to username,
                "statusUser" to status,
                "descriptionUser" to description,
            )
            documentReference.update(edited as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "field edited", Toast.LENGTH_SHORT).show()
                    loading.isDismiss()
                    //finish()
                }


        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImg)
            binding.profileImage.setImageBitmap(bitmap)

        }
    }

    companion object {
        const val TAG = "ProfileDetailActivity"
    }
}