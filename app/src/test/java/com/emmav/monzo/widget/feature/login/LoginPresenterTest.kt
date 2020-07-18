package com.emmav.monzo.widget.feature.login

import com.emmav.monzo.widget.data.api.AccountsResponse
import com.emmav.monzo.widget.data.api.ApiAccount
import com.emmav.monzo.widget.data.api.ApiToken
import com.emmav.monzo.widget.data.api.MonzoApi
import com.emmav.monzo.widget.data.storage.AuthStorage
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.Mockito.`when` as whenever

private const val CLIENT_ID = "CLIENT_ID"
private const val CLIENT_SECRET = "CLIENT_SECRET"
private const val REDIRECT_URI = "REDIRECT_URI"

private const val DEFAULT_CODE = "DEFAULT_CODE"
private const val DEFAULT_STATE = "DEFAULT_STATE"

class LoginPresenterTest {
    private val defaultToken = ApiToken("access_token", "refresh_token", "type")
    private val prepaid = ApiAccount("pp_id", "acc_type_1")
    private val currentAccount = ApiAccount("ca_id", "acc_type_2")

    private val loginRelay = PublishRelay.create<Unit>()
    private val authCodeRelay = PublishRelay.create<Pair<String, String>>()

    @Mock
    private lateinit var monzoApi: MonzoApi
    @Mock
    private lateinit var userStorage: AuthStorage

    private lateinit var presenter: LoginPresenter
    @Mock
    private lateinit var view: LoginPresenter.View

    @Before
    fun setUp() {
        initMocks(this)

        whenever(view.loginClicks()).thenReturn(loginRelay)
        whenever(view.authCodeChanges()).thenReturn(authCodeRelay)

        whenever(userStorage.state).thenReturn(DEFAULT_STATE)

        whenever(monzoApi.accounts()).thenReturn(Single.just(AccountsResponse(listOf(prepaid, currentAccount))))
        whenever(monzoApi.requestAccessToken(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, DEFAULT_CODE))
            .thenReturn(Single.just(defaultToken))

        presenter = LoginPresenter(
            monzoApi, Schedulers.trampoline(), Schedulers.trampoline(), CLIENT_ID, CLIENT_SECRET,
            REDIRECT_URI, userStorage
        )
    }

    @Test
    fun attachView_hasToken_showLoggedIn() {
        whenever(userStorage.hasToken()).thenReturn(true)

        presenter.attachView(view)

        verify(view).showLoggedIn()
    }

    @Test
    fun attachView_hasToken_startBackgroundRefresh() {
        whenever(userStorage.hasToken()).thenReturn(true)

        presenter.attachView(view)

        verify(view).startBackgroundRefresh()
    }

    @Test
    fun onLoginClicked_showRedirecting() {
        presenter.attachView(view)

        loginRelay.accept(Unit)

        verify(view).showRedirecting()
    }

    @Test
    fun onLoginClicked_hideLoginButton() {
        presenter.attachView(view)

        loginRelay.accept(Unit)

        verify(view).hideLoginButton()
    }

    @Test
    fun onLoginClicked_startLogin() {
        presenter.attachView(view)

        loginRelay.accept(Unit)

        verify(view).startLogin(anyString())
    }

    @Test
    fun onAuthCodeReceived_showLoading() {
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, DEFAULT_STATE))

        verify(view).showLoading()
    }

    @Test
    fun onAuthCodeReceived_showLoggingIn() {
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, DEFAULT_STATE))

        verify(view).showLoggingIn()
    }

    @Test
    fun onAuthCodeReceived_requestAccessToken() {
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, DEFAULT_STATE))

        verify(monzoApi).requestAccessToken(anyString(), anyString(), anyString(), anyString(), anyString())
    }

    @Test
    fun onAuthCodeReceived_saveToken() {
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, DEFAULT_STATE))

        verify(userStorage).saveToken(defaultToken)
    }

    @Test
    fun onAuthCodeReceived_showLoggedIn() {
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, DEFAULT_STATE))

        verify(view).showLoggedIn()
    }

    @Test
    fun onAuthCodeReceived_differentState_dontRequestAccessToken() {
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, "differentState"))

        verify(monzoApi, never()).requestAccessToken(anyString(), anyString(), anyString(), anyString(), anyString())
    }

    @Test
    fun onAuthCodeReceived_differentState_showLogIn() {
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, "differentState"))

        verify(view).showLogIn()
    }

    @Test
    fun onAuthCodeReceived_differentStateThenCorrectState_showLoggedIn() {
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, "differentState"))
        authCodeRelay.accept(Pair(DEFAULT_CODE, DEFAULT_STATE))

        verify(view).showLoggedIn()
    }

    @Test
    fun onAuthCodeReceived_noCurrentAccount_showLoggedIn() {
        whenever(monzoApi.accounts()).thenReturn(Single.just(AccountsResponse(listOf(prepaid))))
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, DEFAULT_STATE))

        verify(view).showLoggedIn()
    }

    @Test
    fun onAuthCodeReceived_noPrepaidAccount_showLoggedIn() {
        whenever(monzoApi.accounts()).thenReturn(Single.just(AccountsResponse(listOf(currentAccount))))
        presenter.attachView(view)

        authCodeRelay.accept(Pair(DEFAULT_CODE, DEFAULT_STATE))

        verify(view).showLoggedIn()
    }
}