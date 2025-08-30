package com.example.fay.core.network

import android.content.Context
import com.example.fay.auth.data.api.AuthInterceptor
import com.example.fay.auth.data.api.AuthRepository
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
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * NOTE: In a production app, this value would be based on environment,
     * and would be made available alongside other environment variables via a provider.
     */
    private const val API_BASE_URL = "https://node-api-for-candidates.onrender.com/"

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = createOkHttpClient(context)

    @Singleton
    @Provides
    @AuthenticatedOkHttpClient
    fun provideAuthenticatedOkHttpClient(
        @ApplicationContext context: Context,
        authRepository: AuthRepository
    ): OkHttpClient = createOkHttpClient(context, authRepository)

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit = createRetrofit(client)

    @Singleton
    @Provides
    @AuthenticatedRetrofit
    fun provideAuthenticatedRetrofit(
        @AuthenticatedOkHttpClient client: OkHttpClient
    ): Retrofit = createRetrofit(client)

    private fun createOkHttpClient(
        context: Context,
        authRepository: AuthRepository? = null
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(NetworkConnectivityInterceptor(context))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).apply {
                if (authRepository != null) {
                    addInterceptor(AuthInterceptor(authRepository))
                }
            }
            .build()
    }

    private fun createRetrofit(client: OkHttpClient): Retrofit {
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