package com.gmwapp.hi_dude.utils

import android.os.AsyncTask
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel

class UsersImage (val profileViewModel: ProfileViewModel, val userId: Int) :
    AsyncTask<String?, String?, String>() {
    private var image: String? = null

    override fun onPostExecute(result: String) {
        image = result
    }

    override fun doInBackground(vararg p0: String?): String {
        try {
            var user = profileViewModel.getUserSync(
                userId
            )
            return user.body()?.data?.image.toString()
        } catch (e: Exception) {
            return "";
        }
    }
}
