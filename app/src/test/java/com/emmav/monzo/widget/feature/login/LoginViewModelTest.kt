package com.emmav.monzo.widget.feature.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.WorkManager
import com.emmav.monzo.RxSchedulerRule
import com.emmav.monzo.widget.data.auth.LoginRepository
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when` as whenever

private const val CLIENT_ID = "CLIENT_ID"
private const val REDIRECT_URI = "REDIRECT_URI"

class LoginViewModelTest {
    @Rule @JvmField val rxSchedulerRule = RxSchedulerRule()
    @Rule @JvmField val rule = InstantTaskExecutorRule()

    private val authenticationRepository = mock<LoginRepository> {
        whenever(it.hasToken).thenReturn(true)
        whenever(it.testAuthentication()).thenReturn(Single.just(true))
    }
    private val workManager = mock<WorkManager>()

    private val viewModel by lazy {
        LoginViewModel(
            clientId = CLIENT_ID,
            redirectUri = REDIRECT_URI,
            authenticationRepository = authenticationRepository,
            workManager = workManager
        )
    }

    @Test fun `if authenticated, check SCA state`() {
        viewModel

        verify(authenticationRepository).testAuthentication()
    }
}