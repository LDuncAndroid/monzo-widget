package com.emmav.monzo.widget.feature.setupclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.bindText
import com.emmav.monzo.widget.common.openUrl
import com.emmav.monzo.widget.common.setVisibility
import com.emmav.monzo.widget.feature.login.LoginActivity
import com.emmav.monzo.widget.feature.setupclient.SetupClientViewModel.UiState

class SetupClientActivity : AppCompatActivity(), TextWatcher {

    private val viewModel by lazy {
        App.get(this).setupClientModule.provideSetupClientViewModel()
    }

    private val createClientButton by lazy { findViewById<Button>(R.id.createClientButton) }
    private val existingClientButton by lazy { findViewById<Button>(R.id.existingClientButton) }
    private val goToCreateClientButton by lazy { findViewById<Button>(R.id.goToCreateClientButton) }

    private val setupEnteredClientDetailsButton by lazy { findViewById<Button>(R.id.setupEnteredClientDetailsButton) }
    private val setupClientIdEditText by lazy { findViewById<EditText>(R.id.setupClientIdEditText) }
    private val setupClientSecretEditText by lazy { findViewById<EditText>(R.id.setupClientSecretEditText) }
    private val setupEmojiTextView by lazy { findViewById<TextView>(R.id.setupEmojiTextView) }
    private val setupTitleTextView by lazy { findViewById<TextView>(R.id.setupTitleTextView) }
    private val setupSubtitleTextView by lazy { findViewById<TextView>(R.id.setupSubtitleTextView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setup_client)

        createClientButton.setOnClickListener { viewModel.onCreateClientClicked() }
        existingClientButton.setOnClickListener { viewModel.onHasExistingClientClicked() }
        goToCreateClientButton.setOnClickListener {
            openUrl("https://developers.monzo.com/api")
            viewModel.onHasExistingClientClicked()
        }
        setupEnteredClientDetailsButton.setOnClickListener {
            viewModel.onClientDetailsEntered(
                clientId = setupClientIdEditText.text.toString(),
                clientSecret = setupClientSecretEditText.text.toString()
            )
            startActivity(LoginActivity.buildIntent(this))
            finish()
        }

        setupClientIdEditText.addTextChangedListener(this)
        setupClientSecretEditText.addTextChangedListener(this)

        viewModel.state.observe(this, Observer {
            existingClientButton.setVisibility(visible = it.uiState == UiState.WELCOME)
            createClientButton.setVisibility(visible = it.uiState == UiState.WELCOME)
            goToCreateClientButton.setVisibility(visible = it.uiState == UiState.CREATE_INSTRUCTIONS)
            setupClientIdEditText.setVisibility(visible = it.uiState == UiState.ENTER_CLIENT_DETAILS)
            setupClientSecretEditText.setVisibility(visible = it.uiState == UiState.ENTER_CLIENT_DETAILS)
            setupEnteredClientDetailsButton.setVisibility(visible = it.uiState == UiState.ENTER_CLIENT_DETAILS)
            setupEnteredClientDetailsButton.isEnabled = it.canSaveClientDetails

            setupEmojiTextView.bindText(it.uiState.emoji)
            setupTitleTextView.bindText(it.uiState.title)
            setupSubtitleTextView.bindText(it.uiState.subtitle)
        })
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        viewModel.onClientDetailsChanged(
            setupClientIdEditText.text.toString(),
            setupClientSecretEditText.text.toString()
        )
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, SetupClientActivity::class.java)
        }
    }
}