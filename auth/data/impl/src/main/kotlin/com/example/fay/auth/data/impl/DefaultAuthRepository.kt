package com.example.fay.auth.data.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fay.auth.data.api.AuthRepository
import com.example.fay.core.common.DispatcherProvider
import com.example.fay.core.data.Resource
import com.example.fay.core.network.NoNetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Default implementation of AuthRepository that handles authentication operations.
 * Uses DataStore for local token storage and API service for remote authentication.
 */
class DefaultAuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider
): AuthRepository {
    /** DataStore key for storing the authentication token */
    private val authTokenKey = stringPreferencesKey("auth_token")
    /** Flow that emits the current auth token from DataStore */
    private val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[authTokenKey]
    }
    /** Flow that emits true if user is authenticated (has a valid token), false otherwise */
    override val authenticated: Flow<Boolean> = authToken.map { it != null }

    /**
     * Retrieves the current authentication token.
     * 
     * @return The auth token if available, null otherwise
     */
    override suspend fun getAuthToken(): String? = authToken.first()

    /**
     * Attempts to authenticate the user with email and password.
     * On successful login, stores the auth token locally.
     *
     * @return Flow emitting Resource states: Loading, Success(true), Error, or Exception
     */
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

    /**
     * Logs out the current user by removing the stored auth token.
     */
    override suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(authTokenKey)
        }
    }
}