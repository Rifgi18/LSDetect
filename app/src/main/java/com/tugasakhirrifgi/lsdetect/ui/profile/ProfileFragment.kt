package com.tugasakhirrifgi.lsdetect.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.databinding.FragmentProfileBinding
import com.tugasakhirrifgi.lsdetect.ui.login.LoginActivity
import com.tugasakhirrifgi.lsdetect.ui.newHistory.NewHistoryActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var safeContext: Context
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference
    private lateinit var storageRef: StorageReference

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        safeContext = requireContext()

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        Log.d(TAG, "onCreateView//_binding: $_binding, safeContext: $safeContext")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storageRef = FirebaseStorage.getInstance().reference

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(safeContext, LoginActivity::class.java))
            activity?.finish()
            return
        } else {

            val userUid = firebaseUser.uid
            documentReference = firestore.collection("users").document(userUid)

            val profileRef = storageRef.child("images/${firebaseUser.uid}/profile/profile.jpg")
            profileRef.downloadUrl
                .addOnSuccessListener {
                    if (_binding != null) {
                        if (isValidContextForGlide(safeContext)) {
                            // Load image via Glide lib using context
                            Glide.with(safeContext)
                                .load(it)
                                .into(binding.profileImage)
                        }
                    } else{
                        Log.d(TAG, "onViewCreated// Binding is null, skipping UI update.")
                    }
                }
                .addOnFailureListener {
                    binding.profileImage.setImageResource(R.drawable.ic_picture)
                }


            documentReference.addSnapshotListener { snapshots, e ->

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                Log.d(TAG, "onViewCreated//_binding: $_binding, auth.currentUser: ${auth.currentUser?.uid}, safeContext: $safeContext")

                if ((snapshots != null) && snapshots.exists()){

                    if (_binding != null) {
                        binding.tvContentUsername.text = snapshots.getString("fName")
                        binding.tvContentEmail.text = snapshots.getString("email")
                        binding.tvContentStatusUser.text = snapshots.getString("statusUser")
                        binding.tvContentDescription.text = snapshots.getString("descriptionUser")
                    } else{
                        Log.d(TAG, "onViewCreated// Binding is null, skipping UI update.")
                    }

                }
            }
        }

        binding.cvHistory.setOnClickListener {
            val intentTonewHistoryList = Intent(safeContext, NewHistoryActivity::class.java)
            startActivity(intentTonewHistoryList)
        }

        binding.btnSignOut.setOnClickListener {
            showAlertDialog(ALERT_DIALOG_DELETE)
        }

        binding.cvChange.setOnClickListener {
            val intentToDetailProfile = Intent(safeContext, ProfileDetailActivity::class.java)

            intentToDetailProfile.putExtra("fName", binding.tvContentUsername.text)
            intentToDetailProfile.putExtra("email", binding.tvContentEmail.text)
            intentToDetailProfile.putExtra("statusUser", binding.tvContentStatusUser.text)
            intentToDetailProfile.putExtra("descriptionUser", binding.tvContentDescription.text)

            startActivity(intentToDetailProfile)

        }



    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String
        if (isDialogClose) {
            dialogTitle =  getString(R.string.cancel)
            dialogMessage = getString(R.string.message_delete)
        } else {
            dialogMessage = getString(R.string.message_cancel)
            dialogTitle = getString(R.string.signOut)
        }
        val alertDialogBuilder = AlertDialog.Builder(safeContext)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (!isDialogClose) {
                    signOut()

                }

            }
            setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun signOut() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(safeContext)
             auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            startActivity(Intent(safeContext, LoginActivity::class.java))
            activity?.finish()
        }
    }


    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume//_binding: $_binding, auth.currentUser: ${auth.currentUser?.uid}, safeContext: $safeContext")
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {

            val profileRef = storageRef.child("images/${firebaseUser.uid}/profile/profile.jpg")
            profileRef.downloadUrl
                .addOnSuccessListener {
                    if (_binding != null) {
                        if (isValidContextForGlide(safeContext)) {
                            // Load image via Glide lib using context
                            Glide.with(safeContext)
                                .load(it)
                                .into(binding.profileImage)
                        }
                    } else{
                        Log.d(TAG, "onViewCreated// Binding is null, skipping UI update.")
                    }
                }
                .addOnFailureListener {
                    binding.profileImage.setImageResource(R.drawable.ic_picture)
                }


            documentReference.addSnapshotListener { snapshots, e ->

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if ( snapshots != null && snapshots.exists() ){
                    if (_binding != null) {
                        binding.tvContentUsername.text = snapshots.getString("fName")
                        binding.tvContentEmail.text = snapshots.getString("email")
                        binding.tvContentStatusUser.text = snapshots.getString("statusUser")
                        binding.tvContentDescription.text = snapshots.getString("descriptionUser")
                    } else{
                        Log.d(TAG, "onResume// Binding is null, skipping UI update.")
                    }
                }
            }
        }


    }

    private fun isValidContextForGlide(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity) {
            val activity = context as Activity
            if (activity.isDestroyed || activity.isFinishing) {
                return false
            }
        }
        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroy//_binding: $_binding, auth.currentUser: ${auth.currentUser?.uid}, safeContext: $safeContext")
    }



    companion object {
        const val TAG = "ProfileFragment"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

}