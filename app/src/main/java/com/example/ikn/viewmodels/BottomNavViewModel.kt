package com.example.ikn.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class BottomNavViewModel : ViewModel() {
    private val _addRandomTransaction = MutableLiveData(false)
    val addRandomTransaction: LiveData<Boolean>
        get() = _addRandomTransaction

    fun setShowNewTransaction(show: Boolean) {
        _addRandomTransaction.value = show
    }

}