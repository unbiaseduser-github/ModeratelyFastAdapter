package com.unbiaseduser.moderatelyfastadapter.selection

import android.view.View
import com.unbiaseduser.moderatelyfastadapter.Cell

abstract class SelectionCell<T>(itemView: View) : Cell<T>(itemView) {

    internal fun performUpdateSelection(selected: Boolean) {
        updateSelection(selected)
    }

    protected abstract fun updateSelection(selected: Boolean)

}