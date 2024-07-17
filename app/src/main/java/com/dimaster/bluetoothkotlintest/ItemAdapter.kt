package com.dimaster.bluetoothkotlintest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimaster.bluetoothkotlintest.databinding.KotlinBtListItemBinding

class ItemAdapter(private val listener: Listener, val adapterType: Boolean) :
    ListAdapter<ListItem, ItemAdapter.MyHolder>(Comparator()) {

    var oldCheckBox: CheckBox? = null

    class MyHolder(view : View,
                   private val adapter: ItemAdapter,
                   private val listener: Listener,
                   val adapterType: Boolean) : RecyclerView.ViewHolder(view)
    {
        private val b = KotlinBtListItemBinding.bind(view)
        private var item1: ListItem? = null
        init {
            b.checkBox.setOnClickListener {
                item1?.let { it1 -> listener.onClick(it1) }
                adapter.selectedCheckBox(it as CheckBox)
            }
            itemView.setOnClickListener {
                if(adapterType){
                    try {
                        item1?.device?.createBond()
                    }catch (e:SecurityException){}
                } else  {
                    item1?.let { it1 -> listener.onClick(it1) }
                    adapter.selectedCheckBox(b.checkBox)
                }

            }
        }
        fun bind(item : ListItem) = with(b) {
            checkBox.visibility = if(adapterType) View.GONE else View.VISIBLE
            item1 = item
            try{
                btNameTv.text = item.device.name
                btMacTv.text = item.device.address
            } catch (e:SecurityException){}

            if(item.isChecked) adapter.selectedCheckBox(checkBox)
        }
    }

    class Comparator : DiffUtil.ItemCallback<ListItem>()
    {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kotlin_bt_list_item, parent, false)
        return MyHolder(view, this, listener, adapterType)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun selectedCheckBox(checkBox: CheckBox)
    {
        oldCheckBox?.isChecked = false
        oldCheckBox = checkBox
        oldCheckBox?.isChecked = true
    }

    interface Listener
    {
        fun onClick(device: ListItem)
        {

        }
    }
}