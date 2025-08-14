package com.example.lifesaver.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifesaver.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileViewModel : ViewModel() {
    private val _userProfile = mutableStateOf(UserProfile())
    val userProfile: State<UserProfile> = _userProfile

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("users")

    init {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
        fetchUserProfile()
    }}

    private fun fetchUserProfile(){
        val uid = firebaseAuth.currentUser?.uid?:return
        dbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               val profile = snapshot.getValue(UserProfile::class.java)
                Log.d("FirebaseData", "Fetched Profile: $profile")
                _userProfile.value = profile ?: UserProfile()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch profile", error.toException())
            }
        })
    }

    fun logout(onLogout: () -> Unit){
        firebaseAuth.signOut()
        onLogout()
    }

    fun updateUserProfile(
        name: String,
        email : String,
        phone: String,
        bloodGroup: String,
        allergies: String
        ) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "bloodGroup" to bloodGroup,
            "allergies" to allergies
        )
        dbRef.child(uid).updateChildren(updates).addOnCompleteListener {
            if(it.isSuccessful){
                fetchUserProfile()
            }
        }
    }

    fun updateUserProfile(user: UserProfile){
        val uid = firebaseAuth.currentUser?.uid ?: return
        dbRef.child(uid).setValue(user).addOnCompleteListener {
            fetchUserProfile()
        }
    }

}