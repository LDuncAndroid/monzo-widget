package com.emmav.monzo.widget.feature.setupclient

import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.BaseViewModel
import com.emmav.monzo.widget.common.Text
import com.emmav.monzo.widget.common.text
import com.emmav.monzo.widget.common.textRes
import com.emmav.monzo.widget.data.auth.ClientRepository

class SetupClientViewModel(
    private val clientRepository: ClientRepository
) : BaseViewModel<SetupClientViewModel.State>(initialState = State()) {

    fun onCreateClientClicked() {
        setState { copy(uiState = UiState.CREATE_INSTRUCTIONS) }
    }

    fun onHasExistingClientClicked() {
        setState { copy(uiState = UiState.ENTER_CLIENT_DETAILS) }
    }

    fun onClientIdChanged(clientId: String) {
        setState { copy(clientId = clientId) }
    }

    fun onClientSecretChanged(clientSecret: String) {
        setState { copy(clientSecret = clientSecret) }
    }

    fun onSubmit() {
        clientRepository.clientId = state.value!!.clientId
        clientRepository.clientSecret = state.value!!.clientSecret
        setState { copy(finished = true) }
    }

    data class State(
        val uiState: UiState = UiState.WELCOME,
        val clientId: String? = null,
        val clientSecret: String? = null,
        val finished: Boolean = false
    )

    enum class UiState(val emoji: Text, val title: Text, val subtitle: Text = Text.Empty) {
        WELCOME(
            emoji = text("❓"),
            title = textRes(R.string.setup_welcome_title),
            subtitle = textRes(R.string.setup_welcome_subtitle)
        ),
        CREATE_INSTRUCTIONS(
            emoji = text("ℹ️"),
            title = textRes(R.string.setup_info_title),
            subtitle = textRes(R.string.setup_info_subtitle)
        ),
        ENTER_CLIENT_DETAILS(emoji = text("🔑️"), title = textRes(R.string.setup_enter_client_details_title))
    }
}