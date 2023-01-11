package com.example.justblog.main.ui


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.justblog.CryptAndHashAlgorithm
import com.example.justblog.PostSettings
import com.example.justblog.R
import com.example.justblog.RecyclerViewPicAdapter
import com.example.justblog.cropimage.OnCropListener
import com.example.justblog.databinding.FragmentAddPostBinding
import com.example.justblog.main.model.Bucket
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddPost : Fragment() {
   private lateinit var addPostViewModel:AddPostViewModel
   private lateinit var binding: FragmentAddPostBinding
   private lateinit var picAdapter:RecyclerViewPicAdapter
   private lateinit var listOfImages:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentAddPostBinding.inflate(layoutInflater,container,false)
        val view=binding.root

        addPostViewModel= ViewModelProvider(this)[AddPostViewModel::class.java]
        addPostViewModel.getAllDirectories()

        addPostViewModel.getImageList().observe(viewLifecycleOwner){
            listOfImage-> Toast.makeText(requireContext(),listOfImage.size.toString(),Toast.LENGTH_LONG).show()
        }

        addPostViewModel.imageLists.observe(viewLifecycleOwner) {
            listOfImages=it
            initRecyclerView(listOfImages)
           binding.cropView.setUri( Uri.fromFile(File(it[0])))
        }

       addPostViewModel.getDirectoriesList().observe(viewLifecycleOwner) {
           val list = it
           list.add(0, Bucket("All", ""))
           val sortStaffRoles =
               ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, it)
           binding.addPostSpinner.adapter = sortStaffRoles
           binding.addPostSpinner.onItemSelectedListener =
               object : AdapterView.OnItemSelectedListener {
                   override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                       val selectedImageList = ArrayList<String>()
                       when (val selectedItemString =
                           binding.addPostSpinner.selectedItem.toString()) {
                           "All" -> {
                               picAdapter.updateList(listOfImages)
                                   binding.cropView.setUri( Uri.fromFile(File(listOfImages[0])))

                           }
                           else -> {
                               for (hey in listOfImages) {
                                   if (hey.contains(selectedItemString)) {
                                       selectedImageList.add(hey)
                                   }
                               }
                               picAdapter.updateList(selectedImageList)
                               binding.cropView.setUri( Uri.fromFile(File(selectedImageList[0])))
                           }
                       }
                   }
                   override fun onNothingSelected(p0: AdapterView<*>?) {
                   }
               }

       }
        binding.cropView.addOnCropListener(object : OnCropListener {
            @SuppressLint("SimpleDateFormat")
            override fun onSuccess(bitmap: Bitmap) {

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                val currentHourString = sdf.format(Date())
                val pathHash = CryptAndHashAlgorithm.Hash.md5(currentHourString)


                imageViewBitmapToFile(bitmap,pathHash)
                val intent = Intent(requireContext(), PostSettings::class.java)
                intent.putExtra("image", pathHash)
                startActivity(intent)


            }

            override fun onFailure(e: Exception) {
                Toast.makeText(requireContext(), R.string.error_failed_to_clip_image, Toast.LENGTH_LONG).show()
            }
        })

        binding.cropButton.setOnClickListener(View.OnClickListener {
            if (binding.cropView.isOffFrame()) {
                Toast.makeText(requireContext(), R.string.error_image_is_off_frame, Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            binding.cropView.crop()
        })
        return view
    }
    private fun imageViewBitmapToFile(bitmap: Bitmap,time: String): File {
        val file =
            File(requireContext().getExternalFilesDir("/temp/"), "$time.jpg")
        val fOut = FileOutputStream(file, false)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        fOut.flush()
        fOut.close()
        return file
    }
    private fun initRecyclerView(list:ArrayList<String>){
       picAdapter = RecyclerViewPicAdapter(requireContext(),list)
       binding.addPostRecycler.adapter = picAdapter
       binding.addPostRecycler.layoutManager =
       GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
       ViewCompat.setNestedScrollingEnabled(binding.addPostRecycler,false)
       picAdapter.setOnClickItem {
           binding.cropView.setUri( Uri.fromFile(File(it)))
       }

    }


}