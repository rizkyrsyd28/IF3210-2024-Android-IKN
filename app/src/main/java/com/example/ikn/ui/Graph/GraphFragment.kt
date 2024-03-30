package com.example.ikn.ui.Graph

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ikn.R
import com.example.ikn.databinding.FragmentGraphBinding
import ir.mahozad.android.PieChart
import ir.mahozad.android.PieChart.Slice
import ir.mahozad.android.component.Alignment

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pieChart = binding.pieChart
        pieChart.apply {
            slices = listOf(
                Slice(0.6f, Color.rgb(3, 34, 205)),
                Slice(0.4f, Color.rgb(215, 240, 43))
            )
            isLegendsPercentageEnabled = false
            labelType = PieChart.LabelType.NONE
            holeRatio = 0.75f
        }
    }
}