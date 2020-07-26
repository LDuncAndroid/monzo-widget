package com.emmav.monzo.widget.feature.home

import com.emmav.monzo.widget.data.appwidget.WidgetRepository

class HomeModule(
    private val widgetRepository: WidgetRepository
) {

    fun provideHomeViewModel(): HomeViewModel {
        return HomeViewModel(widgetRepository)
    }
}