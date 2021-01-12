package com.example.watermeterapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.R
import com.example.watermeterapp.data.Records
import com.example.watermeterapp.database.RecordDB
import com.example.watermeterapp.ui.SecondViewModel

class RecordDBAdapter(private val records: List<RecordDB>): RecyclerView.Adapter<RecordDBViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordDBViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return RecordDBViewHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: RecordDBViewHolder, position: Int) {
        val records: RecordDB = records[position]
        holder.bind(records)
    }

    override fun getItemCount() = records.size
}