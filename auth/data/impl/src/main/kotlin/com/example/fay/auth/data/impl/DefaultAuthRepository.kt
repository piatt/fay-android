package com.example.fay.auth.data.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fay.auth.data.api.AuthRepository
import com.example.fay.core.common.dispatcher.DispatcherProvider
import com.example.fay.core.data.Resource
import com.example.fay.core.network.NoNetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider
): AuthRepository {
    private val authTokenKey = stringPreferencesKey("auth_token")
    private val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[authTokenKey]
    }
    override val authenticated: Flow<Boolean> = authToken.map { it != null }

    override suspend fun getAuthToken(): String? = authToken.first()

    override fun login(
        email: String,
        password: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val resource = try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.token != null) {
                dataStore.edit { preferences ->
                    preferences[authTokenKey] = response.body()?.token!!
                }
                Resource.Success(true)
            } else {
                val messageReason = if (response.code() == 401) {
                    "Invalid credentials"
                } else "Something went wrong"
                Resource.Error(
                    code = response.code(),
                    message = "$messageReason. Please try again."
                )
            }
        } catch (e: HttpException) {
            Resource.Error(e.code(), e.message())
        } catch (e: NoNetworkException) {
            Resource.Exception(e)
        } catch (e: Throwable) {
            Resource.Exception(e)
        }
        emit(resource)
    }.flowOn(dispatcherProvider.io)

    override suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(authTokenKey)
        }
    }
}