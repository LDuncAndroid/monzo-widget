package com.emmav.monzo.widget.feature.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.AppTheme
import com.emmav.monzo.widget.common.EmptyState
import com.emmav.monzo.widget.common.text
import com.emmav.monzo.widget.common.textRes
import com.emmav.monzo.widget.feature.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Scaffold(topBar = {
                    TopAppBar(title = { Text(ContextAmbient.current.getString(R.string.home_activity_title)) })
                }, bodyContent = {
                    val state by viewModel.state.observeAsState(HomeViewModel.State())
                    if (state.clickedWidget != null) {
                        startActivity(
                            SettingsActivity.buildIntent(
                                context = ContextAmbient.current,
                                appWidgetId = state.clickedWidget!!.first,
                                widgetTypeId = state.clickedWidget!!.second
                            )
                        )
                    }
                    Content(
                        state = state
                    )
                })
            }
        }
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }
}

@Composable
private fun Content(
    state: HomeViewModel.State,
) {
    when {
        state.loading -> {
            Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                CircularProgressIndicator()
            }
        }
        state.widgets.isEmpty() -> {
            EmptyState(
                emoji = text("🔜"),
                title = textRes(R.string.home_empty_title),
                subtitle = textRes(R.string.home_empty_subtitle)
            )
        }
        else -> {
            WidgetList(state.widgets)
        }
    }
}

@Composable
private fun WidgetList(widgets: List<WidgetRow>) {
    LazyColumnFor(items = widgets, modifier = Modifier.fillMaxHeight()) { widget ->
        Row(modifier = Modifier.fillParentMaxWidth()) {
            Card(
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillParentMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .clickable(onClick = { widget.click(Unit) })
            ) {
                Column {
                    Text(
                        text = widget.title,
                        style = TextStyle(fontSize = 22.sp),
                        modifier = Modifier.padding(all = 16.dp),
                    )
                    Text(
                        text = widget.amount,
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun WidgetListPreview() {
    WidgetList(widgets = listOf(WidgetRow(title = "hi", amount = "£1.23", click = {})))
}