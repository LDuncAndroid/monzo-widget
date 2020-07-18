package com.emmav.monzo.widget.feature.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.gone
import com.emmav.monzo.widget.common.visible
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val viewModel by lazy { App.get(this).loginModule.provideLoginViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener { viewModel.onLoginClicked() }

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is LoginViewModel.State.Unknown -> {
                    // Show full screen loading
                    loginProgressBar.visible()
                }
                is LoginViewModel.State.Authenticated -> {
                    // TODO: Show all widgets & their config
                    loginProgressBar.gone()
                    loginButton.gone()
                    instructionsTextView.text = getString(R.string.login_logged_in_body)
                }
                is LoginViewModel.State.Unauthenticated -> {
                    // show log in
                }
                is LoginViewModel.State.RequestMagicLink -> {
                    instructionsTextView.text = getString(R.string.login_redirecting_body)
                    loginButton.gone()
                    redirectToRequestMagicLink(state.url)
                }
                is LoginViewModel.State.Authenticating -> {
                    instructionsTextView.text = getString(R.string.login_logging_in_body)
                }
                is LoginViewModel.State.RequiresStrongCustomerAuthentication -> {
                    loginButton.gone()
                    loginProgressBar.visible()
                    instructionsTextView.text = getString(R.string.login_sca_required)
                }
            }
        })
    }

    private fun redirectToRequestMagicLink(url: String) {
        CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .build()
            .launchUrl(this, Uri.parse(url))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.data?.let {
            if (it.toString().startsWith(getString(R.string.callback_url_scheme))) {
                viewModel.onMagicLinkParamsReceived(
                    it.getQueryParameter("code")!!,
                    it.getQueryParameter("state")!!
                )
            }
        }
    }
}

