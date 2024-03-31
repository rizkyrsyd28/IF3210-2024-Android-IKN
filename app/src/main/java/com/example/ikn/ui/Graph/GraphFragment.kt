package com.example.ikn.ui.Graph

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ikn.databinding.FragmentGraphBinding
import ir.mahozad.android.PieChart
import ir.mahozad.android.PieChart.Slice
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round
import java.lang.String

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!
    private val graphViewModel: GraphViewModel by viewModels { GraphViewModel.Factory }

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
        var pieChart = binding.pieChart;
        pieChart.apply {
            slices = listOf(
                Slice(0.6f, Color.rgb(3, 34, 205)),
                Slice(0.4f, Color.rgb(215, 240, 43))
            )
            isLegendsPercentageEnabled = false
            labelType = PieChart.LabelType.NONE
            holeRatio = 0.75f
        }

        graphViewModel.transactions.observe(viewLifecycleOwner) { transactionList ->
            val graphData = graphViewModel.getGraphData(transactionList)
            binding.textViewExpensePrcntg.text = (round(graphData.expensePrcnt*100)).toInt().toString().plus("%");
            binding.textViewIncomePrcntg.text = (round(graphData.incomePrcnt*100)).toInt().toString().plus("%");

            val format = NumberFormat.getNumberInstance(Locale("en"))

            binding.textViewExpenseAmount.text = String("Rp ").concat(format.format(graphData.expense));
            binding.textViewIncomeAmount.text = String("Rp ").concat(format.format(graphData.income));

            pieChart.apply {
                slices = listOf(
                    Slice(graphData.expensePrcnt, Color.rgb(3, 34, 205)),
                    Slice(graphData.incomePrcnt, Color.rgb(215, 240, 43))
                )
            }
        }
    }
}