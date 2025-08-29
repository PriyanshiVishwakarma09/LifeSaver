package com.example.lifesaver.repository

class WearableRepository @Inject constructor() {
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
                currentSteps += Random.nextInt(1, 5) // Increment steps by a small random amount

                _wearableData.value = _wearableData.value.copy(
                    heartRate = newHeartRate,
                    steps = currentSteps
                )

                delay(1000) // Update data every 1 second
            }
        }
    }

    fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
        _wearableData.value = _wearableData.value.copy(isTracking = false)
    }
}