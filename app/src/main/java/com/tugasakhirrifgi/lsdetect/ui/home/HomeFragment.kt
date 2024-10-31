package com.tugasakhirrifgi.lsdetect.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.databinding.FragmentHomeBinding
import com.tugasakhirrifgi.lsdetect.ui.home.inform1.InformGejalaActivity
import com.tugasakhirrifgi.lsdetect.ui.home.inform2.InformLsdActivity
import com.tugasakhirrifgi.lsdetect.ui.home.kasuslsd.WebLsdActivity
import com.tugasakhirrifgi.lsdetect.ui.home.tentang.AboutActivity
import com.tugasakhirrifgi.lsdetect.ui.profile.ProfileFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var safeContext: Context
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        try {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            val root: View = binding.root

            safeContext = requireContext()
            (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

            return root

        } catch (e: Exception) {
            Log.e(TAG, "onCreateView", e)
            throw e
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvInform1.setOnClickListener {
            val intentInform1 = Intent(safeContext, InformGejalaActivity::class.java)
            startActivity(intentInform1)
        }
        binding.cvInform2.setOnClickListener {
            val intentInform2 = Intent(safeContext, InformLsdActivity::class.java)
            startActivity(intentInform2)
        }

        binding.cvWebView.setOnClickListener {
            val intentWebView = Intent(safeContext, WebLsdActivity::class.java)
            startActivity(intentWebView)
        }

        binding.cvInfo.setOnClickListener {
            val intentToAbout = Intent(safeContext, AboutActivity::class.java)
            startActivity(intentToAbout)
        }

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = Date()
        val timeNow = dateFormat.format(date)

        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        auth = Firebase.auth
        firestore = Firebase.firestore
        val userID = auth.currentUser?.uid

        if (userID != null ){
            documentReference = firestore.collection("users").document(userID!!)


            binding.apply {

                documentReference.addSnapshotListener { snapshots, e ->

                    if (e != null) {
                        Log.w(ProfileFragment.TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if ( snapshots != null && snapshots.exists() ){
                        val username = snapshots.getString("fName")
                        when(timeOfDay) {
                            in 0..11 -> { tvWelcome.text = "Selamat Pagi, ${username} ☀" }
                            in 12..15 -> { tvWelcome.text = "Selamat Siang, ${username} ☀" }
                            in 16..20 -> { tvWelcome.text = "Selamat Sore, ${username} \uD83C\uDF11" }
                            in 21..23 -> { tvWelcome.text = "Selamat Malam, ${username} \uD83C\uDF11" }
                            else -> {tvWelcome.text = "Selamat Datang, ${username}"}
                        }
                    }
                }

                tvTimeNow.text = timeNow


                val navOption = NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, true)
                    .build()
                btnDetect.setOnClickListener {
                    findNavController().navigate(R.id.action_navigation_home_to_navigation_detection, null, navOption)
                }

                cvDetection.setOnClickListener {
                    findNavController().navigate(R.id.action_navigation_home_to_navigation_detection, null, navOption)
                }
            }
        } else {
            Log.e("HomeFragment", "User ID is null")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}