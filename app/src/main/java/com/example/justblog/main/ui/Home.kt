package com.example.justblog.main.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justblog.CryptAndHashAlgorithm
import com.example.justblog.PostSettings
import com.example.justblog.R
import com.example.justblog.main.adapters.RecyclerViewPicAdapter
import com.example.justblog.cropimage.CropLayout
import com.example.justblog.cropimage.OnCropListener
import com.example.justblog.databinding.FragmentHomeBinding
import com.example.justblog.main.adapters.PostRecyclerViewAdapter
import com.example.justblog.main.model.Bucket
import com.example.justblog.main.model.PostData
import com.example.justblog.main.viewmodel.AddPostViewModel
import com.example.justblog.main.viewmodel.HomeFragmentViewModel
import com.example.justblog.register.ui.Register
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var picAdapter: RecyclerViewPicAdapter
    private lateinit var postRecyclerViewAdapter: PostRecyclerViewAdapter
    private var mAuth: FirebaseAuth? = null
    private val requestPermission = 100
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel
    private var postArrayList = ArrayList<PostData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        mAuth = FirebaseAuth.getInstance()
        initPostRecyclerView()
        initThis()
        initClickListener()
        // Inflate the layout for this fragment
        return view
    }

    private fun initPostRecyclerView() {
        postRecyclerViewAdapter = PostRecyclerViewAdapter(requireContext(), postArrayList)
        binding.homeRecyclerview.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        binding.homeRecyclerview.adapter = postRecyclerViewAdapter
    }

    private fun initThis() {
        homeFragmentViewModel = ViewModelProvider(this)[HomeFragmentViewModel::class.java]
        homeFragmentViewModel.postDataArrayList.observe(viewLifecycleOwner){
            postRecyclerViewAdapter.updateItems(it)
        }
    }

    private fun initClickListener() {
        binding.fab.setOnClickListener {
            if (!requestRuntimePermission()) {
                showImagesDialog()
            }
        }
    }

    private fun showAuthDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        val email = dialog.findViewById<EditText>(R.id.bottom_sheet_email)
        val password = dialog.findViewById<EditText>(R.id.bottom_sheet_pass)
        val login = dialog.findViewById<Button>(R.id.bottom_sheet_login)
        val register = dialog.findViewById<TextView>(R.id.bottom_sheet_layout_register)

        login.setOnClickListener {
            val sharedPreferences: SharedPreferences =
                requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val emailText = email.text.trim().toString()
            val passwordText = password.text.trim().toString()

            if (!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordText)) {
                mAuth!!.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = mAuth!!.currentUser!!.uid
                            val deviceToken: String =
                                FirebaseMessaging.getInstance().token.toString()
                            editor.putString("userId", userId)
                            editor.putString("token", deviceToken)
                            editor.apply()
                            requireActivity().recreate()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                task.exception.toString(),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
            }
        }
        register.setOnClickListener {
            val intent = Intent(requireContext(), Register::class.java)
            startActivity(intent)
        }


        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun showImagesDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayoutimages)

        val addPostViewModel = ViewModelProvider(this)[AddPostViewModel::class.java]
        lateinit var picAdapter: RecyclerViewPicAdapter
        lateinit var listOfImages: ArrayList<String>
        val cropLayout = dialog.findViewById<CropLayout>(R.id.crop_view)
        val addPostSpinner = dialog.findViewById<Spinner>(R.id.add_post_spinner)
        val cropButton = dialog.findViewById<ImageView>(R.id.crop_button)
        val addPostRecycler = dialog.findViewById<RecyclerView>(R.id.add_post_recycler)
        addPostViewModel.getAllDirectories()


        addPostViewModel.getImageList().observe(viewLifecycleOwner) { listOfImage ->
            Toast.makeText(requireContext(), listOfImage.size.toString(), Toast.LENGTH_LONG).show()
        }

        addPostViewModel.imageLists.observe(viewLifecycleOwner) {
            listOfImages = it

            picAdapter = RecyclerViewPicAdapter(requireContext(), listOfImages)
            addPostRecycler.adapter = picAdapter
            addPostRecycler.layoutManager =
                GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
            ViewCompat.setNestedScrollingEnabled(addPostRecycler, false)
            cropLayout.setUri(Uri.fromFile(File(it[0])))

            picAdapter.setOnClickItem { pic ->
                cropLayout.setUri(Uri.fromFile(File(pic)))
            }

        }

        addPostViewModel.getDirectoriesList().observe(viewLifecycleOwner) {
            val list = it
            list.add(0, Bucket("All", ""))
            val sortStaffRoles =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, it)
            addPostSpinner.adapter = sortStaffRoles
            addPostSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        val selectedImageList = ArrayList<String>()
                        when (val selectedItemString =
                            addPostSpinner.selectedItem.toString()) {
                            "All" -> {
                                picAdapter.updateList(listOfImages)
                                cropLayout.setUri(Uri.fromFile(File(listOfImages[0])))

                            }

                            else -> {
                                for (hey in listOfImages) {
                                    if (hey.contains(selectedItemString)) {
                                        selectedImageList.add(hey)
                                    }
                                }
                                picAdapter.updateList(selectedImageList)
                                cropLayout.setUri(Uri.fromFile(File(selectedImageList[0])))
                            }
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }

        }
        cropLayout.addOnCropListener(object : OnCropListener {
            @SuppressLint("SimpleDateFormat")
            override fun onSuccess(bitmap: Bitmap) {

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                val currentHourString = sdf.format(Date())
                val pathHash = CryptAndHashAlgorithm.Hash.md5(currentHourString)


                imageViewBitmapToFile(bitmap, pathHash)
                val intent = Intent(requireContext(), PostSettings::class.java)
                intent.putExtra("image", pathHash)
                startActivity(intent)


            }

            override fun onFailure(e: Exception) {
                Toast.makeText(
                    requireContext(),
                    R.string.error_failed_to_clip_image,
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        cropButton.setOnClickListener(View.OnClickListener {
            if (cropLayout.isOffFrame()) {
                Toast.makeText(
                    requireContext(),
                    R.string.error_image_is_off_frame,
                    Toast.LENGTH_LONG
                ).show()
                return@OnClickListener
            }
            cropLayout.crop()
        })


        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun initRecyclerView(
        cropLayout: CropLayout,
        addPostRecycler: RecyclerView,
        list: ArrayList<String>
    ) {
        picAdapter = RecyclerViewPicAdapter(requireContext(), list)
        addPostRecycler.adapter = picAdapter
        addPostRecycler.layoutManager =
            GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
        ViewCompat.setNestedScrollingEnabled(addPostRecycler, false)
        picAdapter.setOnClickItem {
            cropLayout.setUri(Uri.fromFile(File(it)))
        }

    }

    private fun imageViewBitmapToFile(bitmap: Bitmap, time: String): File {
        val file =
            File(requireContext().getExternalFilesDir("/temp/"), "$time.jpg")
        val fOut = FileOutputStream(file, false)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        fOut.flush()
        fOut.close()
        return file
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun requestRuntimePermission(): Boolean {
        val readStorage = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val requestPermissions: MutableList<String> = ArrayList()
        if (readStorage == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (requestPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                requestPermissions.toTypedArray(),
                requestPermission
            )
        }
        return requestPermissions.size == 1

    }

}