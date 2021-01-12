package com.example.watermeterapp.adapter

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.ApiPost
import com.example.watermeterapp.R
import com.example.watermeterapp.data.Records
import com.example.watermeterapp.data.SubmitFormat
import com.example.watermeterapp.database.AppDatabase
import com.example.watermeterapp.database.RecordDB
import com.example.watermeterapp.ui.FirstFragmentDirections
import com.example.watermeterapp.ui.SecondFragmentDirections
import com.example.watermeterapp.ui.SecondViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordDBViewHolder (inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.record_item_db,parent,false)){




    val context: Context = parent.context
    var db = AppDatabase.getAppDataBase(context)
    var recDao = db?.recordDao()

    private var recordTime: TextView? = null
    private var recordReading: TextView?=null
    private var recordStatus: TextView?=null
    private var submitButton: Button? = null
    private var deleteButton: Button? = null

    init{
        recordTime = itemView.findViewById(R.id.recordTimeTextView)
        recordReading = itemView.findViewById(R.id.recordReadingTextView)
        recordStatus = itemView.findViewById(R.id.recordStatusTextView)
        submitButton = itemView.findViewById(R.id.uploadButton)
        deleteButton = itemView.findViewById(R.id.deleteButton)
    }

    fun bind(records: RecordDB){



        recordReading?.text = records.RecordReading.toString()
        recordStatus?.text = "Pending"

        deleteButton?.setOnClickListener{
            recDao?.delete(records.RecordID)
            Log.d("deleteButton",recDao?.getAll().toString())

            val action = SecondFragmentDirections.actionSecondFragmentSelf(records.RecordPropertyID)
            val nav = itemView.findNavController()

            nav.navigate(action)
        }


        submitButton?.setOnClickListener{
            val api = ApiPost.create(context)
            val sub = SubmitFormat(records.RecordPropertyID,records.RecordReading)
            api.insertRecord(records.RecordPropertyID,sub).enqueue(object: Callback<SubmitFormat> {
                override fun onResponse(call: Call<SubmitFormat>, response: Response<SubmitFormat>) {
                    Log.d("dbSubmit", "success")
                    recDao?.delete(records.RecordID)
                    val action = SecondFragmentDirections.actionSecondFragmentSelf(records.RecordPropertyID)
                    val nav = itemView.findNavController()

                    nav.navigate(action)
                }

                override fun onFailure(call: Call<SubmitFormat>, t: Throwable) {
                    Log.d("dbSubmit",t.message)
                }

            })



        }

    }


}