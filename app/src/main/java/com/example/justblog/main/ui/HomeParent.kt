package com.example.justblog.main.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.justblog.R
import com.example.justblog.databinding.FragmentHomeParentBinding
import com.example.justblog.main.viewmodel.HomeParentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView


class HomeParent : Fragment() {
    private lateinit var binding: FragmentHomeParentBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var homeParentViewModel: HomeParentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeParentViewModel= ViewModelProvider(this)[HomeParentViewModel::class.java]
        binding = FragmentHomeParentBinding.inflate(layoutInflater, container, false)

        navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (controller.currentDestination?.id == R.id.home2) {
                binding.homeParentIconsLinear.visibility = View.VISIBLE
                MainActivity.viewPagerEnable.value = true
            } else {
                binding.homeParentIconsLinear.visibility = View.GONE
                MainActivity.viewPagerEnable.value = false
            }
        }
        val profileButton =
            binding.bottomNavigation.findViewById<BottomNavigationItemView>(R.id.profile)
        profileButton.setOnLongClickListener {
            homeParentViewModel.showSelectImageDialog(requireContext(), R.layout.bottom_sheet_sign_out)
            true
        }

        return binding.root
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavController

    }

}