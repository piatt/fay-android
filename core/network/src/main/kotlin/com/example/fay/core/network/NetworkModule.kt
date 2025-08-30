package com.example.fay.core.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * NOTE: In a production app, this value would be based on environment,
     * and would be made available alongside other environment variables via a provider.
     */
    private const val API_BASE_URL = "https://node-api-for-candidates.onrender.com/"

    /**
     * Provides a singleton Retrofit instance for use by API services within the app.
     *
     * @see NetworkConnectivityInterceptor
     */
    @Singleton
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(NetworkConnectivityInterceptor(context))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .validateEagerly(true)
            .addConverterFactory(
                Json.Default.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
}