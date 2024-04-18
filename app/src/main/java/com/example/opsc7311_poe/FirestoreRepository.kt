package com.example.opsc7311_poe

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FirestoreRepository {
    private val db = Firebase.firestore

    fun createTables() {
        // Create a collection reference for each table
        val usersCollection = db.collection("users")
        val timeEntriesCollection = db.collection("timeEntries")
        val categoriesCollection = db.collection("categories")

        // Add a document to each collection to create the table
        usersCollection.document("placeholder").set(mapOf("placeholder" to "data"))
        timeEntriesCollection.document("placeholder").set(mapOf("placeholder" to "data"))
        categoriesCollection.document("placeholder").set(mapOf("placeholder" to "data"))
    }

    fun addUser(user: User) {
        val userCollection = db.collection("users")
        userCollection.document(user.username).set(user)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    fun addTimeEntry(timeEntry: TimeEntry) {
        val timeEntriesCollection = db.collection("timeEntries")
        timeEntriesCollection.document().set(timeEntry)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    fun addCategory(category: Category) {
        val categoriesCollection = db.collection("categories")
        categoriesCollection.document().set(category)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    // Add more methods as needed for read, update, delete operations
}