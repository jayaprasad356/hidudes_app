package com.gmwapp.hi_dude.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.databinding.ActivityBankUpdateBinding
import com.gmwapp.hi_dude.viewmodels.BankViewModel
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BankUpdateActivity : BaseActivity() {

    private lateinit var binding: ActivityBankUpdateBinding
    val profileViewModel: ProfileViewModel by viewModels()

    val viewModel: BankViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBankUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

    }



    private fun initUI() {


        val prefs = BaseApplication.getInstance()?.getPrefs()
        val userData = prefs?.getUserData()

        val accountHolderName = userData?.holder_name
        val accountNumber = userData?.account_num
        val ifscCode = userData?.ifsc
        val bankName = userData?.bank
        val branchName = userData?.branch

        // set text
        binding.etHolderName.setText(accountHolderName)
        binding.etAccountNumber.setText(accountNumber)
        binding.etIfsccode.setText(ifscCode)
        binding.etBankName.setText(bankName)
        binding.etBranchName.setText(branchName)




        binding.ivBack.setOnClickListener {
            onBackPressed()
        }



//
//        viewModel.bankResponseLiveData.observe(this, Observer {
//            if (it.success) {
//                showToast(it.message)
//            } else {
//                showToast(it.message)
//            }
//
//        })


        // Set up TextWatcher for all fields
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFields()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        // Attach TextWatcher to all EditTexts
        binding.etHolderName.addTextChangedListener(textWatcher)
        binding.etAccountNumber.addTextChangedListener(textWatcher)
        binding.etIfsccode.addTextChangedListener(textWatcher)
        binding.etBankName.addTextChangedListener(textWatcher)
        binding.etBranchName.addTextChangedListener(textWatcher)

        // Disable button initially
        binding.btnUpdate.isEnabled = false

        // Handle button click
        binding.btnUpdate.setOnClickListener {
            // Perform the update action
            BaseApplication.getInstance()?.getPrefs()?.getUserData()?.id?.let { it1 ->

                viewModel.updatedBank(
                    it1,
                    binding.etHolderName.text.toString(),
                    binding.etAccountNumber.text.toString(),
                    binding.etIfsccode.text.toString(),
                    binding.etBankName.text.toString(),
                    binding.etBranchName.text.toString()
                )

            }


        }

        viewModel.bankResponseLiveData.observe(this, Observer {
            if (it != null && it.success) {
                showToast(it.message)
                BaseApplication.getInstance()?.getPrefs()?.getUserData()?.id?.let {
                    profileViewModel.getUsers(it)
                }
            }
        })

        viewModel.bankErrorLiveData.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                showToast(it) // Show the error message as a Toast
            }
        })


        profileViewModel.getUserLiveData.observe(this, Observer {
            it.data?.let { it1 ->
                BaseApplication.getInstance()?.getPrefs()?.setUserData(it1)
            }
            onBackPressed()
            finish()
        })




    }

    private fun validateFields() {
        val accountHolderName = binding.etHolderName.text.toString().trim()
        val accountNumber = binding.etAccountNumber.text.toString().trim()
        val ifscCode = binding.etIfsccode.text.toString().trim()
        val bankName = binding.etBankName.text.toString().trim()
        val branchName = binding.etBranchName.text.toString().trim()

        // Regex to detect special characters
        val specialCharRegex = "[^a-zA-Z0-9 ]".toRegex()

        var isFieldsValid = true

        // Validate each field and show error if invalid
        if (specialCharRegex.containsMatchIn(accountHolderName)) {
            binding.etHolderName.error = "Special characters are not allowed"
            isFieldsValid = false
        } else {
            binding.etHolderName.error = null
        }

        if (specialCharRegex.containsMatchIn(accountNumber)) {
            binding.etAccountNumber.error = "Special characters are not allowed"
            isFieldsValid = false
        } else {
            binding.etAccountNumber.error = null
        }

        if (specialCharRegex.containsMatchIn(ifscCode)) {
            binding.etIfsccode.error = "Special characters are not allowed"
            isFieldsValid = false
        } else {
            binding.etIfsccode.error = null
        }

        if (specialCharRegex.containsMatchIn(bankName)) {
            binding.etBankName.error = "Special characters are not allowed"
            isFieldsValid = false
        } else {
            binding.etBankName.error = null
        }

        if (specialCharRegex.containsMatchIn(branchName)) {
            binding.etBranchName.error = "Special characters are not allowed"
            isFieldsValid = false
        } else {
            binding.etBranchName.error = null
        }

        // Check if all fields are non-empty
        isFieldsValid = isFieldsValid &&
                accountHolderName.isNotEmpty() &&
                accountNumber.isNotEmpty() &&
                ifscCode.isNotEmpty() &&
                bankName.isNotEmpty() &&
                branchName.isNotEmpty()

        // Enable or disable the update button
        binding.btnUpdate.isEnabled = isFieldsValid
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}