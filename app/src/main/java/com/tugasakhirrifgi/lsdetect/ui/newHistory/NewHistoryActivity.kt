package com.tugasakhirrifgi.lsdetect.ui.newHistory

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.databinding.ActivityNewHistoryBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class NewHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewHistoryBinding
    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: NewHistoryAdapter
    private var historyList = mutableListOf<NewHistoryDetection>()
    private var firestore = Firebase.firestore
    private var auth = Firebase.auth
    private var storageRef = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerview = binding.rvHistorydetection
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this)

        //val historyList = mutableListOf<NewHistoryDetection>()
        adapter = NewHistoryAdapter(historyList)
        recyclerview.adapter = adapter
        showData(adapter)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        adapter.setOnItemListener(object : NewHistoryAdapter.ItemClickCallback{
            override fun onDelete(data: NewHistoryDetection) {
                showAlertDialog(ALERT_DIALOG_DELETE, data)
            }

        })

        supportActionBar?.title = "Riwayat Deteksi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun showData(adapter: NewHistoryAdapter) {
        val userID = auth.currentUser?.uid

        firestore.collection("users").document(userID!!).collection("detections")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null){
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    if (snapshots.isEmpty) {
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        historyList.clear()
                        for(document in snapshots.documentChanges){
                            when (document.type) {
                                DocumentChange.Type.ADDED -> {
                                    val history = document.document.toObject(NewHistoryDetection::class.java)
                                    historyList.add(history)
                                }
                                DocumentChange.Type.REMOVED -> {
                                    val history = document.document.toObject(NewHistoryDetection::class.java)
                                    historyList.add(history)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    /*val history = document.document.toObject(NewHistoryDetection::class.java)
                                    historyList.add(history)

                                     */
                                }

                                else -> {}
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun showAlertDialog(type: Int, data: NewHistoryDetection) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String
        if (isDialogClose) {
            dialogTitle =  getString(R.string.cancel)
            dialogMessage = getString(R.string.message_cancel)
        } else {
            dialogMessage = getString(R.string.message_delete)
            dialogTitle = getString(R.string.delete)
        }
        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (!isDialogClose) {
                    deleteData(data)
                }
                //finish()
            }
            setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteData(data: NewHistoryDetection){
        val userID = auth.currentUser?.uid

        val imageURL = storageRef.getReferenceFromUrl(data.image!!)
        imageURL.delete()
            .addOnSuccessListener {
                Log.w(TAG, "Success delete image")
            }
            .addOnFailureListener {
                Log.w(TAG, "Failed delete image")
            }

        firestore.collection("users").document(userID!!).collection("detections")
            .whereEqualTo("timestamp", data.timestamp)
            .get().addOnCompleteListener {
                if (it.isSuccessful && !it.result.isEmpty) {
                    val documentSnapshot = it.result.documents[0]
                    val documentID = documentSnapshot.id
                    firestore.collection("users").document(userID).collection("detections")
                        .document(documentID)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this@NewHistoryActivity, "Hapus data berhasil.....", Toast.LENGTH_LONG).show()
                            //update data
                            adapter.removeItem(data)
                            showData(adapter)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@NewHistoryActivity, "Gagal hapus data : ", Toast.LENGTH_LONG).show()
                        }
                }
            }
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


    companion object {
        const val TAG = "NewHistoryActivity"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }
}

