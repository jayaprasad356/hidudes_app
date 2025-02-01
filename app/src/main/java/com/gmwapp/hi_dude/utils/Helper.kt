package com.gmwapp.hi_dude.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.retrofit.responses.Interests

object Helper {
    private const val TAG = "Helper"

    fun checkNetworkConnection(): Boolean {
        try {
            val connectivityManager: ConnectivityManager = BaseApplication.getInstance()
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeNetwork: NetworkInfo? = null
            activeNetwork = connectivityManager.activeNetworkInfo
            if (activeNetwork != null) {
                return activeNetwork.isConnected
            }
        } catch (e: java.lang.Exception) {
            android.util.Log.e(TAG, "checkNetworkConnection: Exception: " + e.localizedMessage)
        }
        return false
    }

    fun getInterestObject(context: Context, interest: String): Interests {
        if (interest == "Politics") {
            return Interests(
                context.getString(R.string.politics), R.drawable.politics, false
            )

        } else if (interest == "Art") {
            return Interests(
                context.getString(R.string.art), R.drawable.art, false
            )
        } else if (interest == "Sports") {
            return Interests(
                context.getString(R.string.sports), R.drawable.sports, false
            )
        } else if (interest == "Movies") {
            return Interests(
                context.getString(R.string.movies), R.drawable.movie, false
            )
        } else if (interest == "Music") {
            return Interests(
                context.getString(R.string.music), R.drawable.music, false
            )
        } else if (interest == "Foodie") {
            return Interests(
                context.getString(R.string.foodie), R.drawable.foodie, false
            )
        } else {
            return Interests(
                context.getString(R.string.travel), R.drawable.travel, false
            )
        }
    }
}