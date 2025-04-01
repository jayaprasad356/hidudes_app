package com.gmwapp.hi_dude.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.AccountPrivacyActivity
import com.gmwapp.hi_dude.activities.EarningsActivity
import com.gmwapp.hi_dude.activities.EditProfileActivity
import com.gmwapp.hi_dude.databinding.FragmentProfileFemaleBinding
import com.gmwapp.hi_dude.dialogs.BottomSheetLogout
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.AccountViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class ProfileFemaleFragment : BaseFragment() {
    lateinit var binding: FragmentProfileFemaleBinding
    private val EDIT_PROFILE_REQUEST_CODE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    var filePath1: String? = ""
    var imageUri: Uri? = null
    private var isCameraRequest = false
    private val accountViewModel: AccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileFemaleBinding.inflate(layoutInflater)
        initUI()
        return binding.root
    }

    private fun updateValues(){
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        val prefs = BaseApplication.getInstance()?.getPrefs()
        val supportMail = prefs?.getSettingsData()?.support_mail
        val subject = getString(R.string.delete_account_mail_subject, userData?.mobile, userData?.language)

        val body = ""
        binding.tvSupportMail.setOnSingleClickListener {
            val intent = Intent(Intent.ACTION_VIEW)

            val data = Uri.parse(("mailto:$supportMail?subject=$subject").toString() + "&body=$body")
            intent.setData(data)

            startActivity(intent)
        }
        binding.tvSupportMail.paintFlags =
            binding.tvSupportMail.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvSupportMail.text =
            supportMail
        binding.tvName.text = userData?.name
        Glide.with(this).load(userData?.image).
        apply(RequestOptions.circleCropTransform()).into(binding.ivProfile)
    }

    private fun initUI(){
        updateValues()
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        val prefs = BaseApplication.getInstance()?.getPrefs()
        val profileStatus = userData?.profile_status.toString()

        binding.clEarnings.setOnSingleClickListener( {
            val intent = Intent(context, EarningsActivity::class.java)
            startActivity(intent)
        })
        binding.ivEditProfile.setOnSingleClickListener( {
            askMediaPermissions()

//            showEditProfileDialog()
//            val intent = Intent(context, EditProfileActivity::class.java)
//            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE)
        })
        binding.clAccountPrivacy.setOnSingleClickListener( {
            val intent = Intent(context, AccountPrivacyActivity::class.java)
            startActivity(intent)
        })
        binding.cvLogout.setOnSingleClickListener( {
            val bottomSheet: BottomSheetLogout =
                BottomSheetLogout()
            fragmentManager?.let { it1 ->
                bottomSheet.show(
                    it1,
                    "ProfileFragment"
                )
            }
        })
        accountViewModel.getSettings()
        accountViewModel.settingsLiveData.observe(viewLifecycleOwner, Observer {
            if (it!=null && it.success) {
                if (it.data != null) {
                    if (it.data.size > 0) {
                        prefs?.setSettingsData(it.data.get(0))
                        val supportMail = prefs?.getSettingsData()?.support_mail
                        binding.tvSupportMail.text =
                            supportMail
                        val userData = prefs?.getUserData()
                        val subject = getString(R.string.delete_account_mail_subject, userData?.mobile,  userData?.language)

                        val body = ""
                        binding.tvSupportMail.setOnSingleClickListener {
                            val intent = Intent(Intent.ACTION_VIEW)

                            val data = Uri.parse(("mailto:$supportMail?subject=$subject").toString() + "&body=$body")
                            intent.setData(data)

                            startActivity(intent)
                        }
                        binding.tvSupportMail.paintFlags =
                            binding.tvSupportMail.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    }
                }
            }
        })
    }

    private fun askMediaPermissions() {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (requiredPermissions.all { ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED }) {
            onMediaPermissionsGranted()
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }

            if (allGranted) {
                onMediaPermissionsGranted()
            } else {
                if (permissions.any { !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it.key) }) {
                    Toast.makeText(requireActivity(), "Permission permanently denied. Enable it in settings.", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireActivity().packageName))
                    requireActivity().startActivity(intent)
                } else {
                    Toast.makeText(requireActivity(), "Permissions denied. Unable to access media.", Toast.LENGTH_LONG).show()
                }
            }
        }

    private fun onMediaPermissionsGranted() {
//        Toast.makeText(requireActivity(), "Media permissions granted!", Toast.LENGTH_LONG).show()
        showEditProfileDialog()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/* video/*" // ✅ Allow both images and videos
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let {
                startCrop(it)
            }
        }
    }

    private fun startCrop(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAllowFlipping(true)
            .setAllowRotation(true)
            .setFixAspectRatio(true)
            .setActivityMenuIconColor(Color.WHITE)
            .setActivityTitle("Crop Image")
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK && result != null) {
                filePath1 = result.getUriFilePath(requireContext(), true)
                filePath1?.let { updateProfileImage(it) }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("CropImage", "Crop failed: ${result?.error?.message}")
            }
        }
    }

    private fun updateProfileImage(filePath: String) {
        val imgFile = File(filePath)
        if (!imgFile.exists()) {
            Log.e("updateProfileImage", "File does not exist: $filePath")
            return
        } else {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            binding.ivProfile.setImageBitmap(myBitmap)
        }

        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userData?.id.toString())

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imgFile)
        val profileBody = MultipartBody.Part.createFormData("image", imgFile.name, requestFile)

        Log.d("updateProfileImage", "Sending File: ${imgFile.name}")
        Log.d("updateProfileImage", "File Size: ${imgFile.length()} bytes")

        if (profileBody.body == null) {
            Log.e("updateProfileImage", "Error: MultipartBody.Part is null")
            return
        }

        userData?.id?.let {
            accountViewModel.doUpdateImage(userIdBody, profileBody)
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = builder.create()
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        val profileStatus = userData?.profile_status.toString()

        // Set up button click listeners
        dialogView.findViewById<MaterialButton>(R.id.btn_edit_profile).setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_upload_photo).setOnClickListener {

//            isCameraRequest = false
//            pickImageFromGallery()
//            dialog.dismiss()

            when (profileStatus) {
                "0" -> {
                    isCameraRequest = false
                    pickImageFromGallery()
                    dialog.dismiss()
                }
                "1" -> {
                    Toast.makeText(requireActivity(), "Profile uploaded. Waiting for approval", Toast.LENGTH_LONG).show()
                }
                "2" -> {
                    Toast.makeText(requireActivity(), "Already profile updated", Toast.LENGTH_LONG).show()
                }
            }
        }

        dialogView.findViewById<ImageView>(R.id.iv_close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Removes default dialog background
        dialog.show()
    }
//
//    private fun pickImageFromGallery() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
//        galleryLauncher.launch(intent)
//    }
//
//    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            imageUri = result.data?.data
//            imageUri?.let {
//                startCrop(it)
//            }
//        }
//    }
//
//    private fun startCrop(uri: Uri) {
//        CropImage.activity(uri)
//            .setAspectRatio(1, 1) // Maintain square crop
//            .setCropShape(CropImageView.CropShape.OVAL) // Or RECTANGLE
//            .setGuidelines(CropImageView.Guidelines.ON) // Show guidelines
//            .setAllowFlipping(true) // Allow flipping
//            .setAllowRotation(true) // Allow rotation
//            .setFixAspectRatio(true) // Maintain the same aspect ratio
//            .setActivityMenuIconColor(Color.WHITE) // Customize menu icon color
//            .setActivityTitle("Crop Image") // Set title for the cropping screen
//            .start(requireContext(), this)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == Activity.RESULT_OK && result != null) {
//                filePath1 = result.getUriFilePath(requireContext(), true) // ✅ Correct file path
//                filePath1?.let { updateProfileImage(it) }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Log.e("CropImage", "Crop failed: ${result?.error?.message}")
//            }
//        }
//    }
//
//    private fun updateProfileImage(filePath: String) {
//        val imgFile = File(filePath)
//        if (!imgFile.exists()) {
//            Log.e("updateProfileImage", "File does not exist: $filePath")
//            return
//        } else {
//            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//            binding.ivProfile.setImageBitmap(myBitmap) // ✅ Set cropped image
//        }
//
//        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
//        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userData?.id.toString())
//
//        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imgFile)
//        val profileBody = MultipartBody.Part.createFormData("image", imgFile.name, requestFile)
//
//        Log.d("updateProfileImage", "Sending File: ${imgFile.name}")
//        Log.d("updateProfileImage", "File Size: ${imgFile.length()} bytes")
//
//        if (profileBody.body == null) {
//            Log.e("updateProfileImage", "Error: MultipartBody.Part is null")
//            return
//        }
//
//        userData?.id?.let {
//            accountViewModel.doUpdateImage(userIdBody, profileBody)
//        }
//    }


}
