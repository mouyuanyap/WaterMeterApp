package com.example.watermeterapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.R
import com.example.watermeterapp.data.Records

class RecordViewHolder (inflater: LayoutInflater, parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.record_item,parent,false)){


        private var recordTime: TextView? = null
        private var recordReading: TextView?=null
        private var recordStatus: TextView?=null

        init{
            recordTime = itemView.findViewById(R.id.recordBuildingIDTextView)
            recordReading = itemView.findViewById(R.id.recordReadingTextView)
            recordStatus = itemView.findViewById(R.id.recordStatusTextView)
        }

        fun bind(records: Records){
            recordTime?.text = records.RecordTime
            recordReading?.text = records.RecordReading.toString()
            recordStatus?.text = records.RecordStatus

        }
}