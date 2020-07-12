package com.emmaguy.monzo.widget.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.emmaguy.monzo.widget.App
import com.emmaguy.monzo.widget.R
import com.emmaguy.monzo.widget.common.gone
import com.emmaguy.monzo.widget.common.visible
import com.emmaguy.monzo.widget.sync.SyncWorker
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginPresenter.View {
    private val authCodeChangedRelay = PublishRelay.create<Pair<String, String>>()
    private val presenter by lazy { App.get(this).loginModule.provideLoginPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        presenter.attachView(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val uri = intent?.data
        if (uri != null && uri.toString().startsWith(getString(R.string.callback_url_scheme))) {
            authCodeChangedRelay.accept(Pair(
                    uri.getQueryParameter("code")!!,
                    uri.getQueryParameter("state")!!
            ))
        }
    }

    override fun onDestroy() {
        presenter.detachView()

        super.onDestroy()
    }

    override fun loginClicks(): Observable<Unit> {
        return loginButton.clicks()
    }

    override fun authCodeChanges(): Observable<Pair<String, String>> {
        return authCodeChangedRelay
    }

    override fun showLogIn() {
        loginButton.visible()
    }

    override fun hideLoginButton() {
        loginButton.gone()
    }

    override fun showRedirecting() {
        instructionsTextView.text = getString(R.string.login_redirecting_body)
    }

    override fun startLogin(uri: String) {
        CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()
                .launchUrl(this, Uri.parse(uri))
    }

    override fun showLoggingIn() {
        instructionsTextView.text = getString(R.string.login_logging_in_body)
    }

    override fun showLoggedIn() {
        loginButton.gone()
        instructionsTextView.text = getString(R.string.login_logged_in_body)
    }

    override fun startBackgroundRefresh() {
        WorkManager.getInstance(this)
                .enqueue(OneTimeWorkRequest.Builder(SyncWorker::class.java).build())
    }

    override fun showLoading() {
        loginProgressBar.visible()
    }

    override fun hideLoading() {
        loginProgressBar.gone()
    }
}

