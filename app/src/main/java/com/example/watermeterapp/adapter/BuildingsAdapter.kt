package com.example.watermeterapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.data.Buildings

class BuildingsAdapter(private val buildings:List<Buildings>): RecyclerView.Adapter<BuildingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BuildingViewHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: BuildingViewHolder, position: Int) {
        val buildings:Buildings = buildings[position]
        holder.bind(buildings)

    }

    override fun getItemCount() = buildings.size


}