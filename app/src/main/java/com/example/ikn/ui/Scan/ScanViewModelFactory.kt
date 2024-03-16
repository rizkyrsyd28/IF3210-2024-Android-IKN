package com.example.ikn.ui.Scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScanViewModelFactory :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScanViewModel() as T

    }
}