package com.emmav.monzo.widget.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleAdapter<T : Item> : ListAdapter<T, SimpleAdapter.ViewHolder>(DiffCallback<T>()) {
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(viewType, parent, false))
    }

    final override fun onBindViewHolder(holder: ViewHolder, position: Int) = onBind(holder, getItem(position))

    final override fun getItemViewType(position: Int): Int = getLayoutRes(getItem(position))

    abstract fun getLayoutRes(item: T): Int

    abstract fun onBind(holder: ViewHolder, item: T)

    private class DiffCallback<T : Item> : SimpleItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id
    }

    class ViewHolder(val containerView: View) : RecyclerView.ViewHolder(containerView) {
        val context: Context get() = containerView.context
    }
}

abstract class SimpleItemCallback<T> : DiffUtil.ItemCallback<T>() {

    abstract override fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    @SuppressLint("DiffUtilEquals")
    final override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}

interface Item {
    val id: String
}