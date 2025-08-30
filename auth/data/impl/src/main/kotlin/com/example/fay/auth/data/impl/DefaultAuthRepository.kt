package com.example.fay.auth.data.impl

import com.example.fay.auth.data.api.AuthRepository
import com.example.fay.core.common.dispatcher.DispatcherProvider
import com.example.fay.core.common.result.Resource
import com.example.fay.core.network.NoNetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val dispatcherProvider: DispatcherProvider
): AuthRepository {
    // TODO: Replace this with datastore observer
    private val authToken = MutableStateFlow<String?>(null)

    override val authenticated: Flow<Boolean> = authToken.map { it != null }

    override fun getAuthToken(): String? = authToken.value

    override fun login(
        email: String,
        password: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val resource = try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.token != null) {
                authToken.update { response.body()?.token }
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

    override fun logout() {
        authToken.update { null }
    }
}