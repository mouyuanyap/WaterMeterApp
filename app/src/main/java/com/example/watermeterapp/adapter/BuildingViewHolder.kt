package com.example.watermeterapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.R
import com.example.watermeterapp.data.Buildings
import com.example.watermeterapp.ui.FirstFragmentDirections

class BuildingViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.building_item,parent,false)){

    private var buildingName: TextView? = null
    private var button:Button?=null

    init{
        buildingName = itemView.findViewById(R.id.building_name_textView)
        button = itemView.findViewById(R.id.buildingButton)
    }

    fun bind(building: Buildings){
        buildingName?.text = building.UnitBlock + '-' +building.UnitName+ '-' + building.UnitFloor
        button?.setOnClickListener(){
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(building.UnitID)
            itemView.findNavController().navigate(action)
        }
    }

}