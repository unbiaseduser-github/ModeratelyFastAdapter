package com.unbiaseduser.moderatelyfastadapter.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.unbiaseduser.moderatelyfastadapter.Cell
import com.unbiaseduser.moderatelyfastadapter.CellAdapter
import com.unbiaseduser.moderatelyfastadapter.CellFactory

open class SelectionCellAdapter<T, K : Any>(
    diffCallback: DiffUtil.ItemCallback<T>,
    cellFactory: CellFactory<T>,
    private val keyOf: (T) -> K
) : CellAdapter<T>(diffCallback, cellFactory) {

    private var selectionTracker: SelectionTracker<K>? = null

    fun createSelectionTracker(
        recyclerView: RecyclerView,
        selectionId: String,
        storageStrategy: StorageStrategy<K>
    ): SelectionTracker<K> {
        return SelectionTracker.Builder(
            selectionId,
            recyclerView,
            object : ItemKeyProvider<K>(SCOPE_MAPPED) {
                override fun getKey(position: Int): K = keyOf(getItem(position))
                override fun getPosition(key: K): Int = keyToPosition[key] ?: RecyclerView.NO_POSITION
            },
            object : ItemDetailsLookup<K>() {
                override fun getItemDetails(e: MotionEvent): ItemDetails<K>? {
                    return recyclerView.findChildViewUnder(e.x, e.y)?.let {
                        (recyclerView.getChildViewHolder(it) as? SelectionCell<*>)
                    }?.let {
                        object : ItemDetails<K>() {
                            override fun getPosition(): Int {
                                return it.absoluteAdapterPosition
                            }

                            override fun getSelectionKey(): K {
                                return keyOf(getItem(it.absoluteAdapterPosition))
                            }
                        }
                    }
                }
            },
            storageStrategy
        ).build().also {
            this.selectionTracker = it
        }
    }

    private val keyToPosition = mutableMapOf<K, Int>()

    override fun onCurrentListChanged(previousList: List<T>, currentList: List<T>) {
        keyToPosition.clear()
        currentList.forEachIndexed { position, item ->
            keyToPosition[keyOf(item)] = position
        }
    }

    override fun onBindViewHolder(holder: Cell<T>, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? SelectionCell<*>)?.performUpdateSelection(
            selectionTracker?.isSelected(keyOf(getItem(position))) == true
        )
    }

    override fun onBindViewHolder(holder: Cell<T>, position: Int, payloads: List<Any>) {
        val item = getItem(position)
        val isSelected = selectionTracker?.isSelected(keyOf(item)) == true
        if (payloads.size == 1 && payloads[0] == SelectionTracker.SELECTION_CHANGED_MARKER) {
            (holder as? SelectionCell<*>)?.performUpdateSelection(isSelected)
        } else {
            holder.bind(item, payloads)
            (holder as? SelectionCell<*>)?.performUpdateSelection(isSelected)
        }
    }

}