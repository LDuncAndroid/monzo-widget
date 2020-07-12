package com.emmaguy.monzo.widget.login

import com.emmaguy.monzo.widget.common.BasePresenter
import com.emmaguy.monzo.widget.common.plus
import com.emmaguy.monzo.widget.storage.Repository
import io.reactivex.Observable
import io.reactivex.Scheduler
import timber.log.Timber


class LoginPresenter(
        private val uiScheduler: Scheduler,
        private val clientId: String,
        private val redirectUri: String,
        private val repository: Repository
) : BasePresenter<LoginPresenter.View>() {

    // Step 1: request magic link
    // Step 2: open email, click link
    // Step 3: redirect & auth
    // Step 4: SCA in monzo app
    // Step 5: success

    // Phase 2
    // Step 1: go to https://developers.monzo.com/apps/home and create an app
    // Step 2: copy id & secret and save

    override fun attachView(view: View) {
        super.attachView(view)

        if (repository.authenticated()) {
            view.showLoggedIn()
            view.startBackgroundRefresh()
        }

        disposables += view.loginClicks()
                .subscribe {
                    val state = repository.startLogin()

                    view.showRedirecting()
                    view.hideLoginButton()
                    view.startLogin("https://auth.monzo.com/?client_id=$clientId" +
                            "&redirect_uri=$redirectUri" +
                            "&response_type=code" +
                            "&state=" + state)
                }

        // TODO: Rewrite UI
        disposables += view.authCodeChanges()
                .doOnNext { view.showLoading() }
                .doOnNext { view.showLoggingIn() }
                .flatMapMaybe { (code, state) ->
                    repository.login(
                            redirectUri = redirectUri,
                            code = code,
                            state = state
                    )
                }
                .observeOn(uiScheduler)
                .doOnNext { view.hideLoading() }
                .subscribe({
                    view.showLoggedIn()
                    view.startBackgroundRefresh()
                }, Timber::e)
    }

    interface View : BasePresenter.View {
        fun loginClicks(): Observable<Unit>
        fun authCodeChanges(): Observable<Pair<String, String>>

        fun showLoading()
        fun hideLoading()

        fun showLogIn()

        fun startLogin(uri: String)
        fun showRedirecting()
        fun hideLoginButton()

        fun showLoggingIn()

        fun showLoggedIn()
        fun startBackgroundRefresh()
    }
}
