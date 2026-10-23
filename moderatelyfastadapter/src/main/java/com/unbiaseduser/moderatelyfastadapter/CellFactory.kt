package com.unbiaseduser.moderatelyfastadapter

import android.view.LayoutInflater
import android.view.ViewGroup

fun interface CellFactory<T> {
    fun createCell(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): Cell<T>
    fun getItemViewType(item: T): Int = 0
}

class MultiTypeCellFactory<T> private constructor(
    private val viewTypeToCellFactory: Map<Int, CellFactory<T>>,
    private val viewTypeDeterminer: (T) -> Int
) : CellFactory<T> {

    constructor(
        builder: ViewTypeToCellFactoryBuilder<T>,
        viewTypeDeterminer: (T) -> Int
    ) : this(builder.build(), viewTypeDeterminer)

    override fun createCell(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): Cell<T> {
        return requireNotNull(viewTypeToCellFactory[viewType]) {
            "No CellFactory registered for view type $viewType"
        }.createCell(inflater, parent, viewType)
    }

    override fun getItemViewType(item: T): Int {
        return viewTypeDeterminer(item)
    }

}

class ViewTypeToCellFactoryBuilder<T> {
    private val data: MutableMap<Int, CellFactory<T>> = mutableMapOf()
    fun <S : T> cell(viewType: Int, factory: CellFactory<S>) = apply {
        check(viewType !in data) {
            "A CellFactory is already registered for view type $viewType"
        }

        @Suppress("UNCHECKED_CAST")
        data[viewType] = factory as CellFactory<T>
    }
    internal fun build(): Map<Int, CellFactory<T>> = data.toMap()
}

@JvmSynthetic
fun <T> MultiTypeCellFactory(
    viewTypeDeterminer: (T) -> Int,
    builderConfig: ViewTypeToCellFactoryBuilder<T>.() -> Unit
) = MultiTypeCellFactory<T>(
    builder = ViewTypeToCellFactoryBuilder<T>().apply(builderConfig),
    viewTypeDeterminer = viewTypeDeterminer
)