package com.emmav.monzo.widget.feature.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.bindText
import com.emmav.monzo.widget.common.openUrl
import com.emmav.monzo.widget.common.setVisibility
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
            } else if (viewModel.state.value is LoginViewModel.State.Authenticated) {
                finish()
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

            if (state is LoginViewModel.State.RequestMagicLink) {
                state.url?.let {
                    openUrl(it)
                    finish()
                }
            }
        })
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

