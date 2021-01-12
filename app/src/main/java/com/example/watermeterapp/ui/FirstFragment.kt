package com.example.watermeterapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watermeterapp.R
import com.example.watermeterapp.adapter.BuildingsAdapter

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

        viewModel = ViewModelProvider(this).get(FirstViewModel::class.java)
        viewModel.fetchBuildingDetails()
        viewModel.buildingDetails.observe(viewLifecycleOwner, Observer {
            view.findViewById<RecyclerView>(R.id.buildingRecycler).apply{
                layoutManager = LinearLayoutManager(activity)
                adapter = BuildingsAdapter(it)
            }
        })

        
    }
}