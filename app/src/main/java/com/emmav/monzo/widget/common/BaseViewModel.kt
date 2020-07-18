package com.emmav.monzo.widget.common

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel<S : Any>(initialState: S) : ViewModel() {
    protected val disposables = CompositeDisposable()

    private val viewState = MutableLiveData<S>().apply {
        value = initialState
    }
    val state: LiveData<S> = viewState

    @MainThread
    protected fun setState(reducer: S.() -> S) {
        val currentState = viewState.value!!
        val newState = currentState.reducer()
        if (newState != currentState) {
            viewState.value = newState
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}