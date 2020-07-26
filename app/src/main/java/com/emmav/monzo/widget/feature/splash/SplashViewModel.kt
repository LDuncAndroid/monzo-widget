package com.emmav.monzo.widget.feature.splash

import com.emmav.monzo.widget.common.BaseViewModel
import com.emmav.monzo.widget.data.auth.ClientRepository

class SplashViewModel(
    clientRepository: ClientRepository
) : BaseViewModel<SplashViewModel.State>(initialState = State.Unknown) {

    init {
        if (!clientRepository.clientConfigured) {
            setState { State.RequiresClientIdAndSecret }
        } else {
            setState { State.HasClientIdAndSecret }
        }
    }

    sealed class State {
        object Unknown : State()
        object RequiresClientIdAndSecret : State()
        object HasClientIdAndSecret : State()
    }
}