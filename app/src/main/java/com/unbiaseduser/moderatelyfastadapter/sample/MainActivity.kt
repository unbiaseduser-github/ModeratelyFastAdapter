package com.unbiaseduser.moderatelyfastadapter.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.unbiaseduser.moderatelyfastadapter.Cell
import com.unbiaseduser.moderatelyfastadapter.MultiTypeCellFactory
import com.unbiaseduser.moderatelyfastadapter.sample.databinding.ActivityMainBinding
import com.unbiaseduser.moderatelyfastadapter.sample.databinding.ListItemExampleBinding
import com.unbiaseduser.moderatelyfastadapter.selection.SelectionCell
import com.unbiaseduser.moderatelyfastadapter.selection.SelectionCellAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var selectionTracker: SelectionTracker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listAdapter = SelectionCellAdapter<Item, Long>(
            object : DiffUtil.ItemCallback<Item>() {
                override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return oldItem.text == newItem.text
                }
            },
            MultiTypeCellFactory(
                viewTypeDeterminer = { item -> when {
                    item.id % 3 == 0L -> 2
                    item.id % 2 == 0L -> 1
                    else -> 0
                } }
            ) {
                cell(0, { inflater, parent, _ -> ItemCell(ListItemExampleBinding.inflate(inflater, parent, false)) })
                cell(1, { inflater, parent, _ -> ItemCellAlt(ListItemExampleBinding.inflate(inflater, parent, false)) })
                cell(2, { inflater, parent, _ -> ItemCellNonSelectable(ListItemExampleBinding.inflate(inflater, parent, false)) })
            },
            { it.id }
        )
        with(binding.list) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listAdapter
        }
        selectionTracker = listAdapter.createSelectionTracker(
            binding.list, "item_selection", StorageStrategy.createLongStorage()
        ).apply {
            onRestoreInstanceState(savedInstanceState)
        }
        listAdapter.submitList(List(30) { index ->
            Item(index.toLong() + 1, "Item ${index + 1}")
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectionTracker.onSaveInstanceState(outState)
    }

    data class Item(
        val id: Long,
        val text: String
    )

    class ItemCell(private val binding: ListItemExampleBinding) : SelectionCell<Item>(binding.root) {
        override fun onCreate() {
            binding.root.setOnClickListener { Toast.makeText(binding.root.context, requireBoundItem().text, Toast.LENGTH_SHORT).show() }
        }

        override fun updateItem(item: Item, payloads: List<Any>) {
            binding.text.text = item.text
        }

        override fun updateSelection(selected: Boolean) {
            binding.checkbox.isChecked = selected
        }
    }

    class ItemCellAlt(private val binding: ListItemExampleBinding) : SelectionCell<Item>(binding.root) {
        override fun onCreate() {
            binding.root.setOnClickListener { Toast.makeText(binding.root.context, requireBoundItem().text, Toast.LENGTH_SHORT).show() }
        }

        @SuppressLint("SetTextI18n")
        override fun updateItem(item: Item, payloads: List<Any>) {
            binding.text.text = "Alt: " + item.text
        }

        override fun updateSelection(selected: Boolean) {
            binding.checkbox.isChecked = selected
        }
    }

    class ItemCellNonSelectable(private val binding: ListItemExampleBinding) : Cell<Item>(binding.root) {
        override fun onCreate() {
            binding.checkbox.isVisible = false
            binding.root.setOnClickListener { Toast.makeText(binding.root.context, requireBoundItem().text, Toast.LENGTH_SHORT).show() }
        }

        override fun updateItem(item: Item, payloads: List<Any>) {
            binding.text.text = item.text
        }
    }
}