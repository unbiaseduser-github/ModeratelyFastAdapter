package com.unbiaseduser.moderatelyfastadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

open class CellAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val cellFactory: CellFactory<T>
) : ListAdapter<T, Cell<T>>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        return cellFactory.getItemViewType(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Cell<T> {
        return cellFactory.createCell(LayoutInflater.from(parent.context), parent, viewType).also {
            it.dispatchOnCreate()
        }
    }

    override fun onBindViewHolder(holder: Cell<T>, position: Int) {
        holder.bind(getItem(position), emptyList())
    }

    override fun onBindViewHolder(holder: Cell<T>, position: Int, payloads: List<Any>) {
        holder.bind(getItem(position), payloads)
    }

    override fun onViewRecycled(holder: Cell<T>) {
        holder.dispatchOnRecycled()
    }

}