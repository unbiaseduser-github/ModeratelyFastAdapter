package com.unbiaseduser.moderatelyfastadapter

import android.view.View
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.RecyclerView

/**
 * A reusable renderer for a [T].
 */
abstract class Cell<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private sealed interface Binding<out T> {
        data object Unbound : Binding<Nothing>
        data class Bound<T>(val item: T) : Binding<T>
    }

    private var binding: Binding<T> = Binding.Unbound

    /**
     * Gets the currently bound item.
     * @throws IllegalStateException if no item is bound
     */
    protected fun requireBoundItem(): T {
        return when (val binding = binding) {
            is Binding.Bound -> binding.item
            Binding.Unbound -> error("Item has not been bound.")
        }
    }

    internal fun dispatchOnCreate() {
        onCreate()
    }

    /**
     * Called after the cell is constructed. Set your view listeners here and call [requireBoundItem]
     * to act on the current item.
     */
    protected open fun onCreate() = Unit

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun bind(item: T, payloads: List<Any>) {
        binding = Binding.Bound(item)
        updateItem(item, payloads)
    }

    /**
     * @see RecyclerView.Adapter.onBindViewHolder
     */
    protected abstract fun updateItem(item: T, payloads: List<Any>)

    internal fun dispatchOnRecycled() {
        binding = Binding.Unbound
        onRecycled()
    }

    /**
     * @see RecyclerView.Adapter.onViewRecycled
     */
    protected open fun onRecycled() = Unit

}