package com.example.lifesaver.repository

import com.example.lifesaver.data.Contact
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor() {

    private val dbRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("emergencies")

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    init {
        fetchContacts()
    }

    private fun fetchContacts() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contactList = mutableListOf<Contact>()
                for (contactSnapshot in snapshot.children) {
                    val id = contactSnapshot.key ?: continue
                    val name = contactSnapshot.child("name").getValue(String::class.java) ?: ""
                    val role = contactSnapshot.child("role").getValue(String::class.java) ?: ""
                    val phone = contactSnapshot.child("phone").getValue(String::class.java) ?: ""
                    val email = contactSnapshot.child("email").getValue(String::class.java) ?: ""

                    if (name.isNotEmpty() && role.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()) {
                        contactList.add(Contact(id, name, role, phone, email))
                    }
                }
                _contacts.value = contactList
            }

            override fun onCancelled(error: DatabaseError) {
                // Log the error
            }
        })
    }

    fun addContact(name: String, role: String, phone: String, email: String) {
        val key = dbRef.push().key ?: return
        val newContact = mapOf(
            "name" to name,
            "role" to role,
            "phone" to phone,
            "email" to email
        )
        dbRef.child(key).setValue(newContact)
    }

    fun deleteContact(id: String) {
        dbRef.child(id).removeValue()
    }
}