package com.emmav.monzo.widget.feature.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.emmav.monzo.RxSchedulerRule
import com.emmav.monzo.widget.data.storage.Repository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @Rule @JvmField val rxSchedulerRule = RxSchedulerRule()
    @Rule @JvmField val rule = InstantTaskExecutorRule()

    private val repository = mock<Repository> {
        whenever(it.accounts()).thenReturn(Observable.just(emptyList()))
    }

    private val viewModel by lazy {
        SettingsViewModel(appWidgetId = 1, repository = repository)
    }

    @Test fun `set rows`() {
        viewModel


    }
}