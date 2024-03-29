package com.maouni92.messengerapp.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AlertDialogLayout
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.maouni92.messengerapp.Preferences
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.databinding.FragmentProfileBinding
import com.maouni92.messengerapp.helper.ThemeMode
import com.maouni92.messengerapp.helper.hideKeyboard
import com.maouni92.messengerapp.model.User
import com.maouni92.messengerapp.ui.ChangePassword
import com.maouni92.messengerapp.ui.LoginActivity
import com.maouni92.messengerapp.ui.MainActivity
import com.maouni92.messengerapp.viewModel.AuthViewModel
import java.io.ByteArrayOutputStream
import java.util.*


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val profileViewModel: ProfileViewModel by viewModels()

    private var isNotificationsEnabled = true
    private lateinit var pref: Preferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pref = Preferences(container!!.context)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        isNotificationsEnabled = pref.isNotificationsEnabled()
        binding.notificationsSwitch.isChecked = isNotificationsEnabled

        profileViewModel.getCurrentUser()

        profileViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.profileNameTv.text = user.name
            Glide.with(requireActivity())
                .load(user.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.profile_picture)
                .into(binding.profileImageView)
        }


        binding.darkModeItem.setOnClickListener { chooseThemeDialog(container.context) }

        binding.changePassItem.setOnClickListener {
            val intent = Intent(context, ChangePassword::class.java)
            startActivity(intent)
        }
        binding.editNameItem.setOnClickListener {
            showEditNameDialog()
        }

        binding.notificationItem.setOnClickListener {
            isNotificationsEnabled = !isNotificationsEnabled
            binding.notificationsSwitch.isChecked = isNotificationsEnabled
        }

        binding.notificationsSwitch.setOnCheckedChangeListener { _, checked ->

            Log.d("ProfileFragment", "/////////// Notification State: $checked")
            isNotificationsEnabled = checked
            pref.enableNotifications(checked)
        }

        binding.logoutItem.setOnClickListener {
            signOut()

        }

        binding.profileImageView.setOnClickListener {
            imageLauncher.launch("image/*")
        }

        return root
    }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.profileImageView.setImageURI(uri)
            //uploadImage(uri)
            profileViewModel.uploadProfileImage(uri)
        }


    private fun showEditNameDialog() {
        val inputEditNameField = EditText(context).apply {
            hint = "Your name"
            textSize = 16f
            setPadding(8, 10, 8, 10)
            setText(binding.profileNameTv.text)
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.myAlertDialog)
            .setTitle("Edit Name")
            .setView(inputEditNameField)
            .setPositiveButton("OK") { dialog, _ ->
                val name = inputEditNameField.text.toString()
                binding.profileNameTv.text = name
                profileViewModel.changeUserName(name)
                inputEditNameField.hideKeyboard(requireContext())
                dialog.dismiss()

            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun chooseThemeDialog(context: Context) {

        val builder = AlertDialog.Builder(context, R.style.myAlertDialog)

        builder.setTitle(getString(R.string.theme_mode_dialg_title))
        builder.setSingleChoiceItems(
            arrayOf("light", "Dark"),
            pref.currentThemeMode()
        ) { dialog, item ->

            when (item) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    pref.setThemeMode(ThemeMode.LIGHT)
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    pref.setThemeMode(ThemeMode.DARK)
                    dialog.dismiss()
                }

            }
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOut() {

        profileViewModel.signOut()
        val intent = Intent(this.context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
        activity?.finish()


    }
}


