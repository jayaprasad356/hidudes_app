package com.gmwapp.hi_dude.retrofit.callbacks

import retrofit2.Callback

interface NetworkCallback<T> : Callback<T> {
    fun onNoNetwork()
}