package com.emmav.monzo.widget.feature.setupclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.AppTheme
import com.emmav.monzo.widget.common.Text
import com.emmav.monzo.widget.common.openUrl
import com.emmav.monzo.widget.common.resolveText
import com.emmav.monzo.widget.feature.login.LoginActivity
import java.util.*

class SetupClientActivity : AppCompatActivity() {

    private val viewModel by lazy {
        App.get(this).setupClientModule.provideSetupClientViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Column {
                    TopAppBar(title = { Text(ContextAmbient.current.getString(R.string.setup_activity_title)) })

                    val state by viewModel.state.observeAsState(SetupClientViewModel.State())
                    if (state.finished) {
                        startActivity(LoginActivity.buildIntent(ContextAmbient.current))
                        finish()
                    }
                    Content(state)
                }
            }
        }
    }

    @Composable
    fun Content(state: SetupClientViewModel.State) {
        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(all = 16.dp)) {
            val (info, input, actions) = createRefs()
            Info(
                modifier = Modifier.constrainAs(info) {
                    centerHorizontallyTo(parent)
                    linkTo(top = parent.top, bottom = input.top)
                },
                emoji = state.uiState.emoji,
                title = state.uiState.title,
                subtitle = state.uiState.subtitle
            )
            if (state.uiState == SetupClientViewModel.UiState.ENTER_CLIENT_DETAILS) {
                Input(modifier = Modifier.constrainAs(input) {
                    top.linkTo(info.bottom)
                }, state = state)
            }
            Actions(
                modifier = Modifier.constrainAs(actions) {
                    bottom.linkTo(parent.bottom)
                }, state = state
            )
        }
    }

    @Composable
    fun Info(
        modifier: Modifier,
        emoji: Text,
        title: Text,
        subtitle: Text
    ) {
        Column(modifier = modifier, horizontalGravity = Alignment.CenterHorizontally) {
            Text(
                text = ContextAmbient.current.resolveText(emoji),
                fontSize = 84.sp
            )
            Text(
                text = ContextAmbient.current.resolveText(title),
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 32.dp)
            )
            Text(
                text = ContextAmbient.current.resolveText(subtitle),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    @Composable
    fun Input(
        modifier: Modifier,
        state: SetupClientViewModel.State
    ) {
        Column(modifier = modifier.padding(top = 16.dp)) {
            TextField(value = state.clientId ?: "",
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    viewModel.onClientIdChanged(clientId = it)
                },
                label = { Text(ContextAmbient.current.getString(R.string.setup_enter_client_id_hint)) }
            )
            TextField(value = state.clientSecret ?: "",
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    viewModel.onClientSecretChanged(clientSecret = it)
                },
                label = { Text(ContextAmbient.current.getString(R.string.setup_enter_client_secret_hint)) }
            )
        }
    }

    @Composable
    fun Actions(
        modifier: Modifier,
        state: SetupClientViewModel.State
    ) {
        Column(modifier = modifier) {
            when (state.uiState) {
                SetupClientViewModel.UiState.WELCOME -> {
                    FullWidthButton(
                        title = R.string.setup_welcome_action_positive,
                        onClick = { viewModel.onHasExistingClientClicked() }
                    )
                    FullWidthButton(
                        title = R.string.setup_welcome_action_negative,
                        onClick = { viewModel.onCreateClientClicked() }
                    )
                }
                SetupClientViewModel.UiState.CREATE_INSTRUCTIONS -> {
                    FullWidthButton(
                        title = R.string.setup_info_action,
                        onClick = {
                            openUrl("https://developers.monzo.com/api")
                            viewModel.onHasExistingClientClicked()
                        }
                    )
                }
                SetupClientViewModel.UiState.ENTER_CLIENT_DETAILS -> {
                    FullWidthButton(
                        title = R.string.setup_entered_client_details,
                        enabled = state.clientId != null && state.clientSecret != null,
                        onClick = { viewModel.onSubmit() }
                    )
                }
            }
        }
    }

    @Composable
    fun FullWidthButton(
        onClick: () -> Unit,
        title: Int,
        enabled: Boolean = true
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
        ) {
            Text(
                text = ContextAmbient.current.getString(title).toUpperCase(Locale.getDefault()),
            )
        }
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, SetupClientActivity::class.java)
        }
    }
}