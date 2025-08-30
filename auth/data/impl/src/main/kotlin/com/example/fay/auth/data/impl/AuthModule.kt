package com.example.fay.auth.data.impl

import com.example.fay.auth.data.api.AuthRepository
import com.example.fay.core.common.dispatcher.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Singleton
    @Provides
    fun provideAuthApiService(retrofit: Retrofit) : AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        apiService: AuthApiService,
        dispatcherProvider: DispatcherProvider
    ) : AuthRepository {
        return DefaultAuthRepository(apiService, dispatcherProvider)
    }
}