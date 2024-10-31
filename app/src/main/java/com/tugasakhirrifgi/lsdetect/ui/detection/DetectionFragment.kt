package com.tugasakhirrifgi.lsdetect.ui.detection

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.tugasakhirrifgi.lsdetect.databinding.FragmentDetectionBinding
import com.tugasakhirrifgi.lsdetect.helper.DateHelper
import com.tugasakhirrifgi.lsdetect.helper.LoadingDialog
import com.tugasakhirrifgi.lsdetect.ml.EfficientNetB0
import com.tugasakhirrifgi.lsdetect.ui.camera.CameraActivity
import com.tugasakhirrifgi.lsdetect.ui.rotateBitmap
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class DetectionFragment : Fragment() {

    private var _binding: FragmentDetectionBinding? = null
    private lateinit var safeContext: Context

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference
    private lateinit var storageRef: StorageReference

    private var bitmap: Bitmap? = null
    private var currentImageUri: Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        safeContext = requireContext()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        auth = Firebase.auth
        firestore = Firebase.firestore
        storageRef = FirebaseStorage.getInstance().reference

        val userID = auth.currentUser?.uid
        documentReference = firestore.collection("users").document(userID!!)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        val imageProcessor = ImageProcessor.Builder()
            //.add(NormalizeOp(0.0f, 255.0f))
            .add(ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR))
            //.add(NormalizeOp(0.0f, 255.0f))
            .build()

        binding.ivPreviewImg.setOnClickListener {}
        binding.button2.setOnClickListener { startGallery() }
        binding.button3.setOnClickListener { startCameraX() }
        binding.button1.setOnClickListener {

            if(bitmap == null ){
                Toast.makeText(safeContext, "Pilih Gambar dahulu !", Toast.LENGTH_SHORT).show()
            } else {

                try {

                    var tensorImage = TensorImage(DataType.FLOAT32)
                    tensorImage.load(bitmap)

                    tensorImage = imageProcessor.process(tensorImage)

                    val model = EfficientNetB0.newInstance(safeContext)

                    // Creates inputs for reference.
                    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                    inputFeature0.loadBuffer(tensorImage.buffer)

                    // Runs model inference and gets result.
                    val outputs = model.process(inputFeature0)
                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

                    // Find the index with the maximum probability
                    var maxIdx = 0
                    for (i in outputFeature0.indices) {
                        if (outputFeature0[i] > outputFeature0[maxIdx]) {
                            maxIdx = i
                        }
                    }

                    val diseaseItem = arrayListOf(
                        DiseaseItem(
                            hasil = "Lumpy Skin Disease",
                            tindakan = "Pisahkan sapi yang terinfeksi dari sapi sehat, segera laporkan ke pusat Kesehatan agar mendapatkan perawatan yang tepat. Ketika telah mendapatkan perawatan dari dokter, peternak juga perlu merawat gejala-gejala yang muncul agar tidak menjadi parah. Fokus pada penguatan respon imun dan nafsu makan hewan agar lebih cepat dalam pemulihan."
                        ),
                        DiseaseItem(
                            hasil = "Sapi Sehat",
                            tindakan = "Berikan vaksinasi pada hewan yang sehat untuk mencegah infeksi virus LSD, serta menjaga kebersihan kandang agar terbebas vector penyebab menyebarnya virus seperti caplak, lalat, dan nyamuk. Pemberian pakan sehat seperti rumput hijau dan pemberian vitamin sangat dianjurkan untuk meningkatkan imunitas."
                        ),
                    )

                    val result = diseaseItem[maxIdx]
                    val confidence = "Confidence : ${outputFeature0[maxIdx] * 100} %"

                    binding.result.text = result.hasil
                    binding.resultDetect.text = confidence
                    binding.resultDetectMethode.text = result.tindakan
                    model.close()

                    val formatter = SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.getDefault())
                    val now = Date()
                    val filename = formatter.format(now)
                    val imageRef = storageRef.child("images/${userID}/${filename}.jpg")

                    val imageBitmap = bitmap
                    val baos = ByteArrayOutputStream()
                    imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    val uploadTask = imageRef.putBytes(data)

                    val loading = LoadingDialog(requireActivity())
                    loading.startLoading()

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()

                            val newDetection = hashMapOf(
                                "hasil" to result.hasil,
                                "confidence" to confidence,
                                "tindakan" to result.tindakan,
                                "timestamp" to DateHelper.getCurrentDate(),
                                "image" to imageUrl
                            )

                            documentReference.collection("detections")
                                .add(newDetection)
                                .addOnSuccessListener{db ->
                                    showToast("Berhasil Menambahkan Dokumen")
                                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                                    loading.isDismiss()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        }
                            .addOnFailureListener {
                                Log.w(TAG, "Error adding image", it)
                            }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.result.text = e.message
                }

            }



        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX(){
        val intent = Intent(safeContext, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data
            bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedImg)
            binding.ivPreviewImg.setImageBitmap(bitmap)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {

            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            val isBackCamera = it.data?.getBooleanExtra(CameraActivity.EXTRA_CAMERAX_IMAGE, true) as Boolean
            bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,currentImageUri)
            rotateBitmap(bitmap!!, isBackCamera)
            binding.ivPreviewImg.setImageBitmap(bitmap)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(safeContext, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(safeContext, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            safeContext,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {

        _binding = null

        super.onDestroyView()
    }

    private fun showToast(message: String) {
        Toast.makeText(safeContext, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val TAG = "DetectionFragment"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val EXTRA_FORM = "extra_note"
    }

}