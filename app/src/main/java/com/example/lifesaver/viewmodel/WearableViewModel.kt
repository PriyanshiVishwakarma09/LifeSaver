package com.example.lifesaver.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifesaver.repository.WearableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.lifesaver.data.WearableData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WearableViewModel @Inject constructor(
    private val wearableRepository: WearableRepository
) : ViewModel() {
    // Expose the wearable data from the repository as a state flow for the UI
    val wearableData: StateFlow<WearableData> = wearableRepository.wearableData

    // Function to start the tracking simulation
    fun startTracking() {
        wearableRepository.startSimulation()
    }

    // Function to stop the tracking simulation
    fun stopTracking() {
        wearableRepository.stopSimulation()
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}