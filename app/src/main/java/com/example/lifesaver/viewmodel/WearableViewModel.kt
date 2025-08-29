package com.example.lifesaver.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lifesaver.repository.WearableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.lifesaver.data.WearableData

@HiltViewModel
class WearableViewModel @Inject constructor(
    private val repository: WearableRepository
) : ViewModel() {

    // Expose the wearable data from the repository as a state flow for the UI
    val wearableData: StateFlow<WearableData> = repository.wearableData

    // Function to start the tracking simulation
    fun startTracking() {
        repository.startSimulation()
    }

    // Function to stop the tracking simulation
    fun stopTracking() {
        repository.stopSimulation()
    }
}