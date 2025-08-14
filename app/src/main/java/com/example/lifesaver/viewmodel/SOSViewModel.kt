package com.example.lifesaver.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifesaver.data.FirebaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SOSViewModel @Inject constructor(
    private val firebaseHelper: FirebaseHelper
) : ViewModel(){

    fun sendEmergency(emergencyType: String){
        viewModelScope.launch {
            firebaseHelper.sendEmergencyRequestSuspend(emergencyType)
        }
    }
}