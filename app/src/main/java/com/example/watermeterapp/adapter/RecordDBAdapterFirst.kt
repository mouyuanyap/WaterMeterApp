package com.example.watermeterapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.database.RecordDB

class RecordDBAdapterFirst(private val records: List<RecordDB>): RecyclerView.Adapter<RecordDBViewHolderFirst>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordDBViewHolderFirst {
        val inflater = LayoutInflater.from(parent.context)

        return RecordDBViewHolderFirst(inflater,parent)
    }

    override fun onBindViewHolder(holder: RecordDBViewHolderFirst, position: Int) {
        val records: RecordDB = records[position]
        holder.bind(records)
    }

    override fun getItemCount() = records.size
}