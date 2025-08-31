package com.example.lifesaver.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifesaver.data.Contact
import com.example.lifesaver.repository.ContactRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

  //  private val dbRef = FirebaseDatabase.getInstance().getReference("emergencies")
   // private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = contactRepository.contacts

    fun addContact(name: String, role: String, phone: String, email: String) {
        viewModelScope.launch {
            contactRepository.addContact(name, role, phone, email)
        }
    }

    fun deleteContact(id: String) {
        viewModelScope.launch {
            contactRepository.deleteContact(id)
        }
    }
}


