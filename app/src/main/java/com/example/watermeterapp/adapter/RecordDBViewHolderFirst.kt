package com.example.watermeterapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.Api
import com.example.watermeterapp.ApiPost
import com.example.watermeterapp.R
import com.example.watermeterapp.SessionManager
import com.example.watermeterapp.data.BuildingReturn
import com.example.watermeterapp.data.SubmitFormat
import com.example.watermeterapp.database.AppDatabase
import com.example.watermeterapp.database.RecordDB
import com.example.watermeterapp.ui.FirstFragmentDirections
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecordDBViewHolderFirst (inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.record_item_db,parent,false)){

    val context: Context = parent.context
    var db = AppDatabase.getAppDataBase(context)
    var recDao = db?.recordDao()



    private var recordBuildingID: TextView? = null
    private var recordReading: TextView?=null
    private var recordStatus: TextView?=null
    private var submitButton: Button? = null
    private var deleteButton: Button? = null


    init{
        recordBuildingID = itemView.findViewById(R.id.recordBuildingIDTextView)
        recordReading = itemView.findViewById(R.id.recordReadingTextView)
        recordStatus = itemView.findViewById(R.id.recordStatusTextView)
        submitButton = itemView.findViewById(R.id.uploadButton)
        deleteButton = itemView.findViewById(R.id.deleteButton)

    }

    fun bind(records: RecordDB){

        recordBuildingID?.text = records.RecordPropertyName

        recordReading?.text = records.RecordReading.toString()
        recordStatus?.text = "Pending"

        deleteButton?.setOnClickListener{
            recDao?.delete(records.RecordID)
            Log.d("deleteButton",recDao?.getAll().toString())

            val action = FirstFragmentDirections.actionFirstFragmentSelf(isCheck = true)
            val nav = itemView.findNavController()

            nav.navigate(action)
        }

        val sessionManager = context?.let { SessionManager(it.applicationContext) }

        submitButton?.setOnClickListener{
            val api = ApiPost.create(context)
            val sub = SubmitFormat(sessionManager?.fetchUserID()!!,records.RecordPropertyID,records.RecordReading)
            api.insertRecord(records.RecordPropertyID,sub).enqueue(object: Callback<SubmitFormat> {
                override fun onResponse(call: Call<SubmitFormat>, response: Response<SubmitFormat>) {
                    Log.d("dbSubmit", "success")
                    recDao?.delete(records.RecordID)
                    val action = FirstFragmentDirections.actionFirstFragmentSelf(isCheck = true)
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