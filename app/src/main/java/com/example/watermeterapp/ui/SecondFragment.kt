package com.example.watermeterapp.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.ApiPost
import com.example.watermeterapp.R
import com.example.watermeterapp.SessionManager
import com.example.watermeterapp.adapter.RecordDBAdapter
import com.example.watermeterapp.adapter.RecordsAdapter
import com.example.watermeterapp.data.SubmitFormat
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private val args:SecondFragmentArgs by navArgs()
    private lateinit var viewModel: SecondViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = context?.let { SessionManager(it.applicationContext) }

        viewModel = ViewModelProvider(this).get(SecondViewModel::class.java)



        view.findViewById<EditText>(R.id.inputRecord).setText("")

        viewModel.fetchRecordDB(args.propertyID)
        viewModel.recordDb.observe(viewLifecycleOwner, {
            Log.d("startobs", it.toString())
            view.findViewById<RecyclerView>(R.id.recordDbRecycler).apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = RecordDBAdapter(it)
            }
        })


        viewModel.fetchRecordDetails(args.propertyID)
        viewModel.recordDetails.observe(viewLifecycleOwner, {
            Log.d("listening!", "1")
            view.findViewById<RecyclerView>(R.id.detailsRecycler).apply {

                layoutManager = LinearLayoutManager(activity)
                adapter = RecordsAdapter(it)
            }
            view.findViewById<TextView>(R.id.record_name_textView).text = it[0].UnitBlock + "-" + it[0].UnitName + "-" + it[0].UnitFloor

            view.findViewById<EditText>(R.id.inputRecord).addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (view.findViewById<EditText>(R.id.inputRecord).text.toString() == "") {
                        view.findViewById<TextView>(R.id.thisMonthUsage_TextView).text = "-"
                    } else {
                        val lastMonthReading: Int = it[0].RecordReading
                        val thisMonthReading: Int = view.findViewById<EditText>(R.id.inputRecord).text.toString().toInt()
                        val thisMonthUsage: Int = thisMonthReading - lastMonthReading
                        view.findViewById<TextView>(R.id.thisMonthUsage_TextView).text = thisMonthUsage.toString()

                        if (view.findViewById<TextView>(R.id.thisMonthUsage_TextView).text.toString().toInt() < 0) {
                            view.findViewById<TextView>(R.id.thisMonthUsage_TextView).setTextColor(Color.RED)
                        } else {
                            view.findViewById<TextView>(R.id.thisMonthUsage_TextView).setTextColor(Color.GREEN)
                            //toModify
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
        })

        viewModel.fetchUsageDetails(args.propertyID)
        viewModel.usageDetails.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                view.findViewById<TextView>(R.id.LastMonthUsage_TextView).text = it[0].UsageData.toString()
            } else {
                view.findViewById<TextView>(R.id.LastMonthUsage_TextView).text = "-"
            }
        })

        viewModel.fetchUsageAverage(args.propertyID)
        viewModel.usageAverage.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                view.findViewById<TextView>(R.id.averageUsage_TextView).text = it[0].AverageUsage.toString()
            } else {
                view.findViewById<TextView>(R.id.averageUsage_TextView).text = "-"
            }
        })

        viewModel.network.observe(viewLifecycleOwner, {
            if(!it){
                Snackbar.make(
                        view.findViewById(R.id.secondFragmentLayout),
                        "Offline Mode.",

                        Snackbar.LENGTH_LONG
                ).show()
            }


        })

        viewModel.fetchNetworkStatus()

        view.findViewById<Button>(R.id.recordSubmitButton).setOnClickListener{

            if(view.findViewById<EditText>(R.id.inputRecord).text.toString() != "" && view.findViewById<TextView>(R.id.thisMonthUsage_TextView).text.toString().toInt()>-1) {
                view.findViewById<Button>(R.id.recordSubmitButton).isEnabled = false

                viewModel.fetchNetworkStatus()
                val context: Context? = getContext()

                if (viewModel.network.value!!) {

                    var apiP = ApiPost.create(context!!)

                    val sub = SubmitFormat(sessionManager?.fetchUserID()!!,args.propertyID, view.findViewById<EditText>(R.id.inputRecord).text.toString().toInt())

                    Log.d("amada", view.findViewById<EditText>(R.id.inputRecord).text.toString())

                    apiP.insertRecord(args.propertyID, sub).enqueue(object : Callback<SubmitFormat> {

                        override fun onResponse(call: Call<SubmitFormat>, response: Response<SubmitFormat>) {
                            Log.d("yapp", "success")

                            viewModel.fetchRecordDetails(args.propertyID)

                            viewModel.fetchUsageDetails(args.propertyID)

                            viewModel.fetchUsageAverage(args.propertyID)

                            view.findViewById<EditText>(R.id.inputRecord).setText("")

                            view.findViewById<Button>(R.id.recordSubmitButton).isEnabled = true
                        }

                        override fun onFailure(call: Call<SubmitFormat>, t: Throwable) {
                            Log.d("yapp", t.message)
                            view.findViewById<Button>(R.id.recordSubmitButton).isEnabled = true

                        }
                    })
                } else {



                    if (view.findViewById<EditText>(R.id.inputRecord).text.toString() != "") {
                        viewModel.insertRecordDB(args.propertyID,view.findViewById<TextView>(R.id.record_name_textView).text.toString() ,view.findViewById<EditText>(R.id.inputRecord).text.toString().toInt())
                        viewModel.fetchRecordDB(args.propertyID)
                        view.findViewById<Button>(R.id.recordSubmitButton).isEnabled = true
                    }



/*
                viewModel.recordDb.observe(viewLifecycleOwner,{
                    view.findViewById<RecyclerView>(R.id.recordDbRecycler).apply {
                        layoutManager = LinearLayoutManager(activity)
                        adapter = RecordDBAdapter(it)
                    }
                })*/
                }



            }


        }

    }



}
