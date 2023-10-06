package com.darekbx.infopigula.domain

import com.darekbx.infopigula.repository.RemoteRepository
import com.darekbx.infopigula.repository.remote.model.CurrentUserResponse
import com.darekbx.infopigula.repository.remote.model.LoginResponse
import com.darekbx.infopigula.repository.remote.model.UserData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection

class CurrentUserUseCaseTest {

    @Test
    fun `Successfully fetched current user`() = runTest {
        // Given
        val remoteRepository = mockk<RemoteRepository>()
        val currentUserResponse = mockk<CurrentUserResponse>()
        val userData = mockk<UserData>()

        every { userData.userId } returns "1"
        every { userData.userEmail } returns "email"
        every { userData.subscriptionPlanName } returns "plan"
        every { userData.subscriptionEnd } returns "en"
        every { currentUserResponse.data } returns userData
        coEvery { remoteRepository.currentUser() } returns listOf(currentUserResponse)

        // When
        val result = CurrentUserUseCase(remoteRepository).invoke()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("1", result.getOrNull()?.id)
        assertEquals("email", result.getOrNull()?.name)
    }

    @Test
    fun `User is not logged`() = runTest {
        // Given
        val remoteRepository = mockk<RemoteRepository>()

        coEvery { remoteRepository.currentUser() } throws HttpException(
            Response.error<LoginResponse>(
                HttpURLConnection.HTTP_UNAUTHORIZED,
                "".toResponseBody("text/plain".toMediaTypeOrNull())
            )
        )

        // When
        val result = CurrentUserUseCase(remoteRepository).invoke()

        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is UserNotLoggedInException)
    }
}