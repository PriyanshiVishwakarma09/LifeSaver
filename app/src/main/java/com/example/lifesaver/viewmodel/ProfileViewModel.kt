package com.example.lifesaver.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifesaver.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("users")

    // Use StateFlow for better asynchronous state management
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile(){
        val uid = firebaseAuth.currentUser?.uid ?: return

        // Use a ValueEventListener for real-time updates
        dbRef.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(UserProfile::class.java)
                Log.d("FirebaseData", "Fetched Profile: $profile")
                // Update the state flow
                _userProfile.value = profile ?: UserProfile()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch profile", error.toException())
                // Handle the error appropriately, e.g., set a loading state or show a message
            }
        })
    }

    fun logout(onLogout: () -> Unit){
        firebaseAuth.signOut()
        onLogout()
    }

    // Keep only one update function that uses a UserProfile object
    fun updateUserProfile(user: UserProfile){
        val uid = firebaseAuth.currentUser?.uid ?: return
        dbRef.child(uid).setValue(user)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.d("Firebase", "Profile updated successfully.")
                    // The ValueEventListener will automatically fetch and update the UI
                } else {
                    Log.e("Firebase", "Failed to update profile", it.exception)
                }
            }
    }
}

