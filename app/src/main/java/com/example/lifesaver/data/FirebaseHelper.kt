package com.example.lifesaver.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseHelper @Inject constructor(
    private val auth: FirebaseAuth,
    private val dbRef: DatabaseReference,
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
){

    suspend fun sendEmergencyRequestSuspend(emergencyType: String){
        val user = auth.currentUser
        if(user == null){
            Log.w("FirebaseHelper", "No user logged in; cannot send emergency")
            return
        }
       try {
           val location: Location? = try {
               if (ActivityCompat.checkSelfPermission(
                       context,
                       Manifest.permission.ACCESS_FINE_LOCATION
                   ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                       context,
                       Manifest.permission.ACCESS_COARSE_LOCATION
                   ) != PackageManager.PERMISSION_GRANTED
               ) {
                   return
               }
               fusedLocationClient.lastLocation.await()
           } catch (e: Exception){
               null
           }

           val request = EmergencyRequest(
               userId = user.uid,
               type = emergencyType,
               timestamp = System.currentTimeMillis(),
               latitude = location?.latitude ?: 0.0,
               longitude = location?.longitude ?: 0.0
           )
           dbRef.child("emergencies").push().setValue(request).await()
           Log.d("FirebaseHelper", "Emergency sent: $request")
       } catch (e: Exception){
           Log.e("FirebaseHelper", "Failed to send emergency", e)
       }
    }
}