package com.example.fay.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Checks for an active network connection before proceeding with any
 * given network request, and throws [NoNetworkException] if no connection exists.
 *
 * @see [https://developer.android.com/develop/connectivity/network-ops/reading-network-state#instantaneous]
 */
class NetworkConnectivityInterceptor(context: Context) : Interceptor {
    private val connectivityManager = getSystemService(context, ConnectivityManager::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (isNetworkConnected()) {
            return chain.proceed(request)
        } else {
            throw NoNetworkException()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val networkCapabilities = connectivityManager?.let {
            it.getNetworkCapabilities(it.activeNetwork)
        }
        return networkCapabilities != null
                && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

class NoNetworkException : IOException()