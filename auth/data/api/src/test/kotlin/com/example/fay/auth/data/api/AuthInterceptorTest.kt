package com.example.fay.auth.data.api

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthInterceptorTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var authRepository: AuthRepository
    private lateinit var authInterceptor: AuthInterceptor
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        authRepository = mockk()
        authInterceptor = AuthInterceptor(authRepository)
        
        client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `intercept adds auth header when token is available`() = runTest {
        val token = "valid_auth_token"
        coEvery { authRepository.getAuthToken() } returns token
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .build()
        
        val response = client.newCall(request).execute()
        
        assertEquals(200, response.code)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("Bearer $token", recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept does not add auth header when token is null`() = runTest {
        coEvery { authRepository.getAuthToken() } returns null
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .build()
        
        val response = client.newCall(request).execute()
        
        assertEquals(200, response.code)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertNull(recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept preserves existing headers when adding auth header`() = runTest {
        val token = "valid_auth_token"
        coEvery { authRepository.getAuthToken() } returns token
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .addHeader("Content-Type", "application/json")
            .addHeader("User-Agent", "TestApp/1.0")
            .build()
        
        val response = client.newCall(request).execute()
        
        assertEquals(200, response.code)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("Bearer $token", recordedRequest.getHeader("Authorization"))
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"))
        assertEquals("TestApp/1.0", recordedRequest.getHeader("User-Agent"))
    }

    @Test
    fun `intercept preserves existing headers when token is null`() = runTest {
        coEvery { authRepository.getAuthToken() } returns null
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .addHeader("Content-Type", "application/json")
            .addHeader("User-Agent", "TestApp/1.0")
            .build()
        
        val response = client.newCall(request).execute()
        
        assertEquals(200, response.code)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertNull(recordedRequest.getHeader("Authorization"))
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"))
        assertEquals("TestApp/1.0", recordedRequest.getHeader("User-Agent"))
    }

    @Test
    fun `intercept overrides existing authorization header when token is available`() = runTest {
        val token = "valid_auth_token"
        coEvery { authRepository.getAuthToken() } returns token
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .addHeader("Authorization", "Basic old_token")
            .build()
        
        val response = client.newCall(request).execute()
        
        assertEquals(200, response.code)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("Bearer $token", recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept does not override existing authorization header when token is null`() = runTest {
        coEvery { authRepository.getAuthToken() } returns null
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .addHeader("Authorization", "Basic existing_token")
            .build()
        
        val response = client.newCall(request).execute()
        
        assertEquals(200, response.code)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("Basic existing_token", recordedRequest.getHeader("Authorization"))
    }

    @TestFactory
    fun `intercept handles different token formats`() = listOf(
        TokenTestCase("short token", "abc123"),
        TokenTestCase("long token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U"),
        TokenTestCase("token with special characters", "token-with_special.chars123"),
        TokenTestCase("single character token", "a"),
        TokenTestCase("numeric token", "1234567890"),
        TokenTestCase("token with spaces", "token with spaces"),
        TokenTestCase("empty string token", ""),
        TokenTestCase("token with underscore", "token_test_123")
    ).map { testCase ->
        DynamicTest.dynamicTest("token format: ${testCase.name}") {
            runTest {
                coEvery { authRepository.getAuthToken() } returns testCase.token
                
                mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
                
                val request = Request.Builder()
                    .url(mockWebServer.url("/test"))
                    .build()
                
                val response = client.newCall(request).execute()
                
                assertEquals(200, response.code)
                
                val recordedRequest = mockWebServer.takeRequest()
                if (testCase.token.isEmpty()) {
                    // Empty token should result in no Authorization header
                    assertNull(recordedRequest.getHeader("Authorization"))
                } else {
                    assertEquals("Bearer ${testCase.token}", recordedRequest.getHeader("Authorization"))
                }
            }
        }
    }

    @TestFactory
    fun `intercept works with different HTTP methods`() = listOf(
        HttpMethodTestCase("GET", "GET"),
        HttpMethodTestCase("POST", "POST"),
        HttpMethodTestCase("PUT", "PUT"),
        HttpMethodTestCase("DELETE", "DELETE"),
        HttpMethodTestCase("PATCH", "PATCH"),
        HttpMethodTestCase("HEAD", "HEAD")
    ).map { testCase ->
        DynamicTest.dynamicTest("HTTP method: ${testCase.name}") {
            runTest {
                val token = "test_token_for_${testCase.method.lowercase()}"
                coEvery { authRepository.getAuthToken() } returns token
                
                mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
                
                val request = when (testCase.method) {
                    "POST", "PUT", "PATCH" -> {
                        Request.Builder()
                            .url(mockWebServer.url("/test"))
                            .method(testCase.method, "test body".toRequestBody())
                            .build()
                    }
                    else -> {
                        Request.Builder()
                            .url(mockWebServer.url("/test"))
                            .method(testCase.method, null)
                            .build()
                    }
                }
                
                val response = client.newCall(request).execute()
                
                assertEquals(200, response.code)
                
                val recordedRequest = mockWebServer.takeRequest()
                assertEquals(testCase.method, recordedRequest.method)
                assertEquals("Bearer $token", recordedRequest.getHeader("Authorization"))
            }
        }
    }

    @Test
    fun `intercept handles multiple sequential requests correctly`() = runTest {
        val token1 = "first_token"
        val token2 = "second_token"
        
        coEvery { authRepository.getAuthToken() } returns token1 andThen token2
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("First Response"))
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Second Response"))
        
        // First request
        val request1 = Request.Builder()
            .url(mockWebServer.url("/first"))
            .build()
        
        val response1 = client.newCall(request1).execute()
        assertEquals(200, response1.code)
        
        val recordedRequest1 = mockWebServer.takeRequest()
        assertEquals("Bearer $token1", recordedRequest1.getHeader("Authorization"))
        
        // Second request
        val request2 = Request.Builder()
            .url(mockWebServer.url("/second"))
            .build()
        
        val response2 = client.newCall(request2).execute()
        assertEquals(200, response2.code)
        
        val recordedRequest2 = mockWebServer.takeRequest()
        assertEquals("Bearer $token2", recordedRequest2.getHeader("Authorization"))
    }

    @Test
    fun `intercept handles repository exception gracefully`() = runTest {
        coEvery { authRepository.getAuthToken() } throws RuntimeException("Repository error")
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .build()
        
        // Should not throw exception, should proceed without auth header
        val response = client.newCall(request).execute()
        
        assertEquals(200, response.code)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertNull(recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept with chain of interceptors maintains order`() = runTest {
        val token = "test_token"
        coEvery { authRepository.getAuthToken() } returns token
        
        val additionalInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Custom-Header", "CustomValue")
                .build()
            chain.proceed(request)
        }
        
        val clientWithMultipleInterceptors = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(additionalInterceptor)
            .build()
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Success"))
        
        val request = Request.Builder()
            .url(mockWebServer.url("/test"))
            .build()
        
        val response = clientWithMultipleInterceptors.newCall(request).execute()
        
        assertEquals(200, response.code)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("Bearer $token", recordedRequest.getHeader("Authorization"))
        assertEquals("CustomValue", recordedRequest.getHeader("X-Custom-Header"))
    }

    private data class TokenTestCase(
        val name: String,
        val token: String
    )

    private data class HttpMethodTestCase(
        val name: String,
        val method: String
    )
}