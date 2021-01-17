package com.example.watermeterapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.R
import com.example.watermeterapp.adapter.BuildingsAdapter
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {


    private lateinit var viewModel: FirstViewModel



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

        viewModel.buildingDetails.observe(viewLifecycleOwner, Observer {
            view.findViewById<RecyclerView>(R.id.buildingRecycler).apply{
                layoutManager = LinearLayoutManager(activity)
                adapter = BuildingsAdapter(it)
            }
        })

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



/*
        Log.d("checkedID", view.findViewById<RadioGroup>(R.id.blockRadioGroup).checkedRadioButtonId.toString())
        if (view.findViewById<RadioGroup>(R.id.blockRadioGroup).checkedRadioButtonId == -1){
            view.findViewById<RadioGroup>(R.id.blockRadioGroup).check(R.id.radioButtonAll)
        }
*/




        
    }
}