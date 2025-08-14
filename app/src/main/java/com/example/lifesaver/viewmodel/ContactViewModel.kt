package com.example.lifesaver.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lifesaver.data.Contact
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor() : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("emergencies")
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts


    init {
        fetchContacts()
    }

    private fun fetchContacts(){

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contactList = mutableListOf<Contact>()
                for(contactSnapshot in snapshot.children) {
                    val name = contactSnapshot.child("name").getValue(String::class.java) ?: ""
                    val role = contactSnapshot.child("role").getValue(String::class.java) ?: ""
                    val phone = contactSnapshot.child("phone").getValue(String::class.java) ?: ""
                    val email = contactSnapshot.child("email").getValue(String::class.java) ?: ""

                    if (name.isNotEmpty() && role.isNotEmpty()) {
                        contactList.add(Contact(name, role, phone, email))
                    }
                }
                    _contacts.value = contactList

            }
            override fun onCancelled(error: DatabaseError){
                Log.e("Firebase", "Failed to load contacts: ${error.message}")
            }
        })
    }
    fun deleteContact(key: String){
        dbRef.child(key).removeValue()
    }
}