package com.example.lifesaver.repository

import com.example.lifesaver.data.WearableData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class WearableRepository @Inject constructor() {

    // The mutable state flow that will hold our wearable data
    private val _wearableData = MutableStateFlow(WearableData())
    val wearableData: StateFlow<WearableData> = _wearableData

    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Job())

    fun startSimulation() {
        // If a simulation is already running, do nothing
        if (simulationJob != null && simulationJob?.isActive == true) {
            return
        }

        // Reset data and start a new job
        _wearableData.value = WearableData(isTracking = true, heartRate = 70)

        var currentSteps = _wearableData.value.steps

        simulationJob = scope.launch {
            while (true) {
                // Generate realistic random data
                val newHeartRate = Random.nextInt(60, 120) // Heart rate between 60 and 120
               // currentSteps += Random.nextInt(1, 5) // Increment steps by a small random amount

                _wearableData.value = _wearableData.value.copy(
                    heartRate = newHeartRate,
                   // steps = currentSteps
                )
                if(newHeartRate > 110 || newHeartRate < 50){
                    println("ALERT! Abnormal heart rate detected: $newHeartRate bpm")
                }

                delay(1000) // Update data every 1 second
            }
        }
    }
    fun addSteps(stepsToAdd: Int){
        val currentSteps = _wearableData.value.steps
        _wearableData.value = _wearableData.value.copy(
            steps = currentSteps + stepsToAdd
        )
    }

    fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
        _wearableData.value = _wearableData.value.copy(isTracking = false)
    }
}
