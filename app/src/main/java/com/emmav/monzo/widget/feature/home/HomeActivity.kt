package com.emmav.monzo.widget.feature.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.SimpleAdapter
import com.emmav.monzo.widget.common.gone
import com.emmav.monzo.widget.common.visible
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_home_row.view.*

class HomeActivity : AppCompatActivity() {
    private val viewModel by lazy { App.get(this).homeModule.provideHomeViewModel() }
    private val widgetsAdapter = WidgetsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        homeRecyclerView.apply {
            adapter = widgetsAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.state.observe(this, Observer { state ->
            if (state.loading) {
                homeProgressBar.visible()
            } else {
                homeProgressBar.gone()
            }

            if (state.widgets.isEmpty()) {
                // TODO: Empty view
            } else {
                homeRecyclerView.visible()
                widgetsAdapter.submitList(state.widgets)
            }
        })
    }

    class WidgetsAdapter : SimpleAdapter<WidgetRow>() {
        override fun getLayoutRes(item: WidgetRow): Int {
            return when (item) {
                is WidgetRow.Widget -> R.layout.item_home_row
            }
        }

        override fun onBind(holder: ViewHolder, item: WidgetRow) {
            when (item) {
                is WidgetRow.Widget -> item.bind(holder)
            }
        }

        private fun WidgetRow.Widget.bind(holder: ViewHolder) {
            holder.containerView.homeTextView.text = title
        }
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }
}

