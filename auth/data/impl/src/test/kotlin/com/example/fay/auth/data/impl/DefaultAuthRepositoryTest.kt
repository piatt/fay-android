package com.example.fay.auth.data.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.example.fay.core.common.DispatcherProvider
import com.example.fay.core.data.Resource
import com.example.fay.core.network.NoNetworkException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultAuthRepositoryTest {
    private lateinit var apiService: AuthApiService
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var repository: DefaultAuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        apiService = mockk()
        dispatcherProvider = mockk {
            every { io } returns testDispatcher
        }
        
        val testFile = File.createTempFile("test_preferences", ".preferences_pb")
        testFile.deleteOnExit()
        dataStore = PreferenceDataStoreFactory.create { testFile }
        
        repository = DefaultAuthRepository(apiService, dataStore, dispatcherProvider)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `authenticated flow emits false when no token stored`() = runTest {
        repository.authenticated.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `authenticated flow emits true when token is stored`() = runTest {
        val token = "test_token"
        
        // Store token first
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("auth_token")] = token
        }
        
        repository.authenticated.test {
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `getAuthToken returns null when no token stored`() = runTest {
        val result = repository.getAuthToken()
        assertNull(result)
    }

    @Test
    fun `getAuthToken returns token when stored`() = runTest {
        val token = "test_token"
        
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("auth_token")] = token
        }
        
        val result = repository.getAuthToken()
        assertEquals(token, result)
    }

    @TestFactory
    fun `login scenarios`() = listOf(
        LoginTestCase(
            name = "successful login with valid credentials",
            response = Response.success(LoginResponse("valid_token")),
            expectedResource = Resource.Success(true)
        ),
        LoginTestCase(
            name = "login failure with 401 status code",
            response = Response.error<LoginResponse>(
                401, 
                "Unauthorized".toResponseBody()
            ),
            expectedResource = Resource.Error(401, "Invalid credentials. Please try again.")
        ),
        LoginTestCase(
            name = "login failure with 500 status code",
            response = Response.error<LoginResponse>(
                500, 
                "Internal Server Error".toResponseBody()
            ),
            expectedResource = Resource.Error(500, "Something went wrong. Please try again.")
        ),
        LoginTestCase(
            name = "successful response with null body",
            response = Response.success<LoginResponse>(null),
            expectedResource = Resource.Error(200, "Something went wrong. Please try again.")
        )
    ).map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            runTest {
                coEvery { 
                    apiService.login(LoginRequest("test@example.com", "password")) 
                } returns testCase.response

                repository.login("test@example.com", "password").test {
                    assertEquals(Resource.Loading, awaitItem())
                    
                    val result = awaitItem()
                    when (testCase.expectedResource) {
                        is Resource.Success -> {
                            assertTrue(result is Resource.Success)
                            assertEquals(testCase.expectedResource.data, result.data)
                            
                            // Verify token was stored for successful login
                            val storedToken = repository.getAuthToken()
                            assertEquals("valid_token", storedToken)
                        }
                        is Resource.Error -> {
                            assertTrue(result is Resource.Error)
                            assertEquals(testCase.expectedResource.code, result.code)
                            assertEquals(testCase.expectedResource.message, result.message)
                        }
                        is Resource.Loading -> {
                            // Should not happen in this test
                        }
                        is Resource.Exception -> {
                            // Should not happen in this test
                        }
                    }
                    
                    awaitComplete()
                }
            }
        }
    }

    @TestFactory
    fun `login exception scenarios`() = listOf(
        ExceptionTestCase(
            name = "HttpException during login",
            exception = HttpException(Response.error<Any>(404, "Not Found".toResponseBody())),
            expectedResource = Resource.Error(404, "HTTP 404 ")
        ),
        ExceptionTestCase(
            name = "NoNetworkException during login",
            exception = NoNetworkException(),
            expectedResource = Resource.Exception(NoNetworkException())
        ),
        ExceptionTestCase(
            name = "Generic exception during login",
            exception = RuntimeException("Something went wrong"),
            expectedResource = Resource.Exception(RuntimeException("Something went wrong"))
        )
    ).map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            runTest {
                coEvery { 
                    apiService.login(LoginRequest("test@example.com", "password")) 
                } throws testCase.exception

                repository.login("test@example.com", "password").test {
                    assertEquals(Resource.Loading, awaitItem())
                    
                    val result = awaitItem()
                    when (testCase.expectedResource) {
                        is Resource.Error -> {
                            assertTrue(result is Resource.Error)
                            assertEquals(testCase.expectedResource.code, result.code)
                        }
                        is Resource.Exception -> {
                            assertTrue(result is Resource.Exception)
                            assertEquals(
                                testCase.expectedResource.exception::class,
                                result.exception::class
                            )
                        }
                        is Resource.Loading -> {
                            // Should not happen in this test
                        }
                        is Resource.Success -> {
                            // Should not happen in this test
                        }
                    }
                    
                    awaitComplete()
                }
            }
        }
    }

    @Test
    fun `logout removes stored token`() = runTest {
        val token = "test_token"
        
        // Store token first
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("auth_token")] = token
        }
        
        // Verify token is stored
        assertEquals(token, repository.getAuthToken())
        
        // Logout
        repository.logout()
        
        // Verify token is removed
        assertNull(repository.getAuthToken())
    }

    @Test
    fun `authenticated flow updates after logout`() = runTest {
        val token = "test_token"
        
        // Store token first
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("auth_token")] = token
        }
        
        repository.authenticated.test {
            assertTrue(awaitItem()) // Should be authenticated
            
            repository.logout()
            
            assertFalse(awaitItem()) // Should not be authenticated after logout
        }
    }

    private data class LoginTestCase(
        val name: String,
        val response: Response<LoginResponse>,
        val expectedResource: Resource<Boolean>
    )

    private data class ExceptionTestCase(
        val name: String,
        val exception: Exception,
        val expectedResource: Resource<Boolean>
    )
}