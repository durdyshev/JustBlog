package com.example.justblog.main.ui

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.justblog.ProfileImageUpload
import com.example.justblog.databinding.FragmentTakePhotoBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutionException


class TakePhoto : Fragment() {
    private lateinit var processCameraProvider: ProcessCameraProvider
    private var imageCapture: ImageCapture? = null
    private lateinit var binding: FragmentTakePhotoBinding

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTakePhotoBinding.inflate(layoutInflater, container, false)
        if (requestRuntimePermission()){
            initThis()
        }
        return binding.root
    }

    private fun initThis() {
        startCamera()
        binding.takePhoto.setOnClickListener {
            takePhoto()
        }
        binding.flipCamera.setOnClickListener {
            flipCamera()
        }
    }

    private fun takePhoto() {
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

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    AddPost.image = if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) {
                        BitmapFactory.decodeStream(
                            requireContext().contentResolver.openInputStream(output.savedUri!!)
                        ).rotate(90F)
                    } else {
                        BitmapFactory.decodeStream(
                            requireContext().contentResolver.openInputStream(output.savedUri!!)
                        ).rotate(270F)
                    }
                    File(getRealPathFromURI(output.savedUri!!)!!).delete()
                    val intent = Intent(requireActivity(), ProfileImageUpload::class.java)
                    startActivity(intent)
                }
            }
        )
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

    private fun flipCamera() {
        if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA) lensFacing =
            CameraSelector.DEFAULT_BACK_CAMERA else if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) lensFacing =
            CameraSelector.DEFAULT_FRONT_CAMERA
        startCamera()
    }

    private fun startCamera() {
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

    fun getRealPathFromURI(contentUri: Uri): String? {
        val result: String?
        val cursor: Cursor? =
            requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            result = contentUri.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    private fun requestRuntimePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    13
                )
                return false
            }
        }
        //android 13 permission request
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    13
                )
                return false
            }
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                initThis()
            } else
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
        }
    }


    override fun onResume() {
        super.onResume()
        if (requestRuntimePermission()){
            initThis()
        }
    }
}