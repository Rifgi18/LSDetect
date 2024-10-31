package com.tugasakhirrifgi.lsdetect.ui.camera

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.tugasakhirrifgi.lsdetect.databinding.ActivityCameraBinding
import com.tugasakhirrifgi.lsdetect.ui.createFile
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.ui.detection.DetectionFragment.Companion.CAMERA_X_RESULT

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var camera: Camera
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    //private var aspectRatio = AspectRatio.RATIO_16_9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        startCamera()


        binding.arrowBack.setOnClickListener { finish() }
        binding.captureImage.setOnClickListener { takePhoto() }
        binding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

            startCamera()
        }
        binding.buttonHelp.setOnClickListener {
            showCustomDialog()
        }

    }

    private fun showCustomDialog(){
        lateinit var isDialog: AlertDialog

        val dialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.bottomsheet_fragment, null)

        val tvModal2: TextView = dialogView.findViewById(R.id.tv_modal_2)
        val btnModal1: Button = dialogView.findViewById(R.id.btn_modal_1)
        val htmlContent = getString(R.string.modalContent2)

        dialog.setView(dialogView)
        dialog.setCancelable(false)
        isDialog = dialog.create()
        isDialog.show()

        tvModal2.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(htmlContent)
        }

        btnModal1.setOnClickListener {
            isDialog.dismiss()
        }

    }



    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            orientationEventListener.enable()

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                binding.flashToggleIB.setOnClickListener {
                    setFlashIcon(camera)
                }
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(application)

        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = (lensFacing == CameraSelector.LENS_FACING_FRONT)
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata( metadata )
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Berhasil mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent()
                    //intent.putExtra("picture", photoFile)
                    intent.putExtra(EXTRA_CAMERAX_IMAGE, output.savedUri.toString())
                    intent.putExtra("isBackCamera", cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)

                    setResult(CAMERA_X_RESULT, intent)
                    finish()
                }
            }
        )
    }

    private fun setFlashIcon(camera: Camera){
        if (camera.cameraInfo.hasFlashUnit()) {
            if (camera.cameraInfo.torchState.value == 0) {
                camera.cameraControl.enableTorch(true)
                binding.flashToggleIB.setImageResource(R.drawable.baseline_flash_on_24)
            } else {
                camera.cameraControl.enableTorch(false)
                binding.flashToggleIB.setImageResource(R.drawable.baseline_flash_off_24)
            }
        } else {
            Toast.makeText(
                this,
                "Flash is not available",
                Toast.LENGTH_LONG
            ).show()
            binding.flashToggleIB.isEnabled = false
        }
    }

    private fun setupView() {
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

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }
                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imageCapture?.targetRotation = rotation
            }
        }
    }
    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }
    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }
}