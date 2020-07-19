package com.emmav.monzo.widget.feature.login

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.emmav.monzo.widget.common.BaseViewModel
import com.emmav.monzo.widget.data.storage.LoginRepository
import com.emmav.monzo.widget.feature.sync.SyncWorker
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private val clientId: String,
    private val redirectUri: String,
    private val loginRepository: LoginRepository,
    private val workManager: WorkManager
) : BaseViewModel<LoginViewModel.State>(initialState = State.Unknown) {

    init {
        if (loginRepository.hasToken) {
            setPreSCAAndSync()
        } else {
            setState { State.Unauthenticated }
        }
    }

    /**
     * We don't know if we're approved for SCA until we try to sync
     */
    private fun setPreSCAAndSync() {
        setState { State.RequiresStrongCustomerAuthentication }

        // Check 2 seconds after invoked, then every 10
        disposables += Observable.interval(2, 10, TimeUnit.SECONDS)
            .flatMapSingle { loginRepository.testAuthentication() }
            .filter { isAuthenticated -> isAuthenticated }
            .take(1) // Once we're logged in, we no longer need to poll
            .ignoreElements()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setState { State.Authenticated }
                workManager.enqueue(OneTimeWorkRequest.Builder(SyncWorker::class.java).build())
            }
    }

    fun onLoginClicked() {
        setState {
            State.RequestMagicLink(
                url = "https://auth.monzo.com/?client_id=$clientId" +
                        "&redirect_uri=$redirectUri" +
                        "&response_type=code" +
                        "&state=" + loginRepository.startLogin()
            )
        }
    }

    fun onMagicLinkParamsReceived(code: String, state: String) {
        setState { State.Authenticating }
        disposables += loginRepository.login(redirectUri, code, state)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setPreSCAAndSync() }
    }

    sealed class State {
        object Unknown : State()
        object Unauthenticated : State()

        /**
         * Start of the OAuth flow to authenticate. The app will redirect the user to the [url], which will trigger
         * the sending of a magic link email. Clicking on this email will redirect back [redirectUri] and open
         * [LoginActivity]. We'll then update state to be [Authenticating].
         */
        data class RequestMagicLink(val url: String) : State()

        /**
         * We've been redirected back to the app, via the magic link the user clicked on.
         * Use the information from this link to login with.
         */
        object Authenticating : State()
        object RequiresStrongCustomerAuthentication : State()

        /**
         * We made it! We can successfully sync our data with the Monzo api 🙌🏽.
         */
        object Authenticated : State()
    }
}