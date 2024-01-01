package com.example.justblog.main.ui

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.justblog.databinding.FragmentTakePhotoBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutionException


class TakePhoto : Fragment() {
    private lateinit var processCameraProvider: ProcessCameraProvider
    private var imageCapture: ImageCapture? = null
    private lateinit var binding: FragmentTakePhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTakePhotoBinding.inflate(layoutInflater, container, false)
        startCamera1()
        binding.takePhoto.setOnClickListener {
            takePhoto()
        }
        binding.flipCamera.setOnClickListener {
            flipCamera()
        }
        return binding.root
    }

    private fun takePhoto() {
        Toast.makeText(requireContext(), "ssss", Toast.LENGTH_LONG).show()
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("Camera", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d("Camera", msg)
                }
            }
        )
    }

    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

    private fun flipCamera() {
        if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA) lensFacing =
            CameraSelector.DEFAULT_BACK_CAMERA else if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) lensFacing =
            CameraSelector.DEFAULT_FRONT_CAMERA
        startCamera1()
    }

    private fun startCamera1() {
        val cameraFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraFuture.addListener({
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(binding.fragmentTakePhotoPreview.display.rotation)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            this.binding.fragmentTakePhotoPreview.implementationMode =
                PreviewView.ImplementationMode.COMPATIBLE

            try {
                processCameraProvider = cameraFuture.get()
                val preview =
                    Preview.Builder().build()
                preview.setSurfaceProvider(binding.fragmentTakePhotoPreview.surfaceProvider)
                processCameraProvider.unbindAll()

                // lensFacing is used here
                processCameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    lensFacing,
                    imageCapture,
                    preview
                )
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onResume() {
        super.onResume()
        startCamera1()
    }
}