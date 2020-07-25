package com.emmav.monzo.widget.feature.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.bindText
import com.emmav.monzo.widget.common.setVisibility
import com.emmav.monzo.widget.feature.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val viewModel by lazy { App.get(this).loginModule.provideLoginViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        loginActionButton.setOnClickListener {
            if (viewModel.state.value is LoginViewModel.State.RequiresStrongCustomerAuthentication) {
                val monzoAppIntent = packageManager.getLaunchIntentForPackage("co.uk.getmondo")
                if (monzoAppIntent != null) {
                    startActivity(monzoAppIntent)
                } else {
                    Toast.makeText(this, R.string.login_requires_sca_monzo_not_installed, Toast.LENGTH_SHORT).show()
                }
            } else {
                viewModel.onLoginClicked()
            }
        }

        viewModel.state.observe(this, Observer { state ->
            loginProgressBar.setVisibility(visible = state.showLoading)
            loginActionButton.bindText(state.actionButton)
            loginEmojiTextView.bindText(state.emoji)
            loginTitleTextView.bindText(state.title)
            loginSubtitleTextView.bindText(state.subtitle)

            when (state) {
                is LoginViewModel.State.RequestMagicLink -> {
                    state.url?.let {
                        redirectToRequestMagicLink(it)
                        finish()
                    }
                }
                is LoginViewModel.State.Authenticated -> {
                    startActivity(HomeActivity.buildIntent(this))
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

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}

