package com.example.watermeterapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.R
import com.example.watermeterapp.data.Buildings
import com.example.watermeterapp.data.Records
import com.example.watermeterapp.ui.FirstFragmentDirections

class RecordViewHolder (inflater: LayoutInflater, parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.record_item,parent,false)){


        private var recordTime: TextView? = null
        private var recordReading: TextView?=null
        private var recordStatus: TextView?=null

        init{
            recordTime = itemView.findViewById(R.id.recordTimeTextView)
            recordReading = itemView.findViewById(R.id.recordReadingTextView)
            recordStatus = itemView.findViewById(R.id.recordStatusTextView)
        }

        fun bind(records: Records){
            recordTime?.text = records.RecordTime
            recordReading?.text = records.RecordReading.toString()
            recordStatus?.text = records.RecordStatus

        }
}