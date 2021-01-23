package com.example.watermeterapp.ui

import android.icu.text.AlphabeticIndex
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.R
import com.example.watermeterapp.SessionManager
import com.example.watermeterapp.adapter.BuildingsAdapter
import com.example.watermeterapp.adapter.RecordDBAdapter
import com.example.watermeterapp.adapter.RecordDBAdapterFirst
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val args:FirstFragmentArgs by navArgs()

    private lateinit var viewModel: FirstViewModel
    private lateinit var viewModel2: SecondViewModel




    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("view","created")
        viewModel = ViewModelProvider(this).get(FirstViewModel::class.java)
        viewModel2 = ViewModelProvider(this).get(SecondViewModel::class.java)

        val sessionManager = context?.let { SessionManager(it.applicationContext) }
        Log.d("userID",sessionManager?.fetchUserID().toString())

        viewModel.buildingDetails.observe(viewLifecycleOwner, Observer {
            view.findViewById<RecyclerView>(R.id.buildingRecycler).apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = viewModel.recordDetails.value?.let { it1 -> BuildingsAdapter(it, it1) }
            }
        })

        view.findViewById<TextView>(R.id.userLastNameTextView).text = sessionManager?.fetchUserLastName()

        viewModel.isOnline.observe(viewLifecycleOwner, Observer {
            if (!it){
                Snackbar.make(
                        view.findViewById(R.id.firstFragmentLayout),
                        "Offline Mode.",
                        Snackbar.LENGTH_LONG
                ).show()
            }
        })

        viewModel.checkNetwork()

        fun doQuery(blockQuery:String,floorQuery:String){
            Log.d("doBlockquery",blockQuery)
            Log.d("doFloorquery",floorQuery)
            if ((blockQuery == "All" || blockQuery == "")&& (floorQuery == "All" || floorQuery == "")){
                viewModel.fetchBuildingDetails()
            }else if (floorQuery == "All" || floorQuery == ""){
                viewModel.fetchSpecificBlockBuildingDetails(blockQuery)
            }else if (blockQuery == "All" || blockQuery == ""){
                viewModel.fetchSpecificFloorBuildingDetails(floorQuery)
            }else{
                viewModel.fetchSpecificAllBuildingDetails(blockQuery,floorQuery)
            }
        }

        viewModel.blockQuery.observe(viewLifecycleOwner, Observer {

            doQuery(it,viewModel.floorQuery.value.toString())
        })

        viewModel.floorQuery.observe(viewLifecycleOwner, Observer {
            doQuery(viewModel.blockQuery.value.toString(),it)
        })

        viewModel.blockQuery.value = ""
        viewModel.floorQuery.value = ""

        view.findViewById<RadioGroup>(R.id.blockRadioGroup).setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{
            radioGroup, i ->

            Log.d("radio",view.findViewById<RadioButton>(i).text.toString())
            viewModel.getBlockQuery(view.findViewById<RadioButton>(i).text.toString())

        })

        view.findViewById<RadioGroup>(R.id.floorRadioGroup).setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{
            radioGroup, i ->

            Log.d("radio",view.findViewById<RadioButton>(i).text.toString())
            viewModel.getFloorQuery(view.findViewById<RadioButton>(i).text.toString())

        })



        if (args.isCheck){
            Log.d("isCheck",viewModel.buildingName.value.toString())
            view.findViewById<CheckBox>(R.id.noUploadcheckBox).isChecked = true
            view.findViewById<RecyclerView>(R.id.buildingRecycler).visibility = View.INVISIBLE
            view.findViewById<RecyclerView>(R.id.recordDbRecyclerFirst).visibility = View.VISIBLE
            viewModel2.fetchALLRecordDB()

            viewModel2.recordDb.observe(viewLifecycleOwner, {
                Log.d("dog1a",it.toString())

                view.findViewById<RecyclerView>(R.id.recordDbRecyclerFirst).apply {

                    layoutManager = LinearLayoutManager(activity)
                    adapter = RecordDBAdapterFirst(it)
                }


            })
        }else{
            view.findViewById<CheckBox>(R.id.noUploadcheckBox).isChecked = false
            view.findViewById<RecyclerView>(R.id.buildingRecycler).visibility = View.VISIBLE
            view.findViewById<RecyclerView>(R.id.recordDbRecyclerFirst).visibility = View.INVISIBLE
        }


        view.findViewById<CheckBox>(R.id.noUploadcheckBox).setOnCheckedChangeListener { buttonView, isChecked ->

            Log.d("checking","true")

            viewModel2.fetchALLRecordDB()



            viewModel2.recordDb.observe(viewLifecycleOwner, {

                view.findViewById<RecyclerView>(R.id.recordDbRecyclerFirst).apply {

                    layoutManager = LinearLayoutManager(activity)
                    adapter = RecordDBAdapterFirst(it)
                }
            })

            if (isChecked){
                view.findViewById<RecyclerView>(R.id.buildingRecycler).visibility = View.INVISIBLE
                view.findViewById<RecyclerView>(R.id.recordDbRecyclerFirst).visibility = View.VISIBLE

            }else{
                view.findViewById<RecyclerView>(R.id.buildingRecycler).visibility = View.VISIBLE
                view.findViewById<RecyclerView>(R.id.recordDbRecyclerFirst).visibility = View.INVISIBLE
            }

        }


/*
        Log.d("checkedID", view.findViewById<RadioGroup>(R.id.blockRadioGroup).checkedRadioButtonId.toString())
        if (view.findViewById<RadioGroup>(R.id.blockRadioGroup).checkedRadioButtonId == -1){
            view.findViewById<RadioGroup>(R.id.blockRadioGroup).check(R.id.radioButtonAll)
        }
*/
    }


}