package com.example.watermeterapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.data.Buildings
import com.example.watermeterapp.data.Records

class RecordsAdapter(private val records: List<Records>): RecyclerView.Adapter<RecordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecordViewHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val records:Records = records[position]
        holder.bind(records)

    }

    override fun getItemCount() = records.size
}