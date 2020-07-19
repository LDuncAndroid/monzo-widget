package com.emmav.monzo.widget.feature.home

import com.emmav.monzo.widget.data.storage.WidgetRepository

class HomeModule(
    private val widgetRepository: WidgetRepository
) {

    fun provideHomeViewModel(): HomeViewModel {
        return HomeViewModel(widgetRepository)
    }
}