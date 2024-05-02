package com.example.opsc7311_poe

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.widget.Toast
import android.content.Context


class FirestoreRepository(private val context: Context) {
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

    //Method to check if user already exists in User table
    fun userExists(username: String, callback: (Boolean) -> Unit) {
        val userCollection = db.collection("users")
        userCollection.document(username).get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener { e ->
                // Handle failure
                callback(false)
            }
    }

    //Method to get user from User table
    fun getUser(username: String, callback: (User?) -> Unit) {
        val userCollection = db.collection("users")
        userCollection.document(username).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                callback(null)
            }
    }

    //Method to add user to User table
    fun addUser(user: User, callback: (Boolean) -> Unit) {
        val userCollection = db.collection("users")
        userCollection.document(user.username).set(user)
            .addOnSuccessListener {
                // Handle success
                callback(true)
            }
            .addOnFailureListener { e ->
                // Handle failure
                callback(false)
            }
    }


    // Add a time entry to Firestore
    fun addTimeEntry(timeEntry: TimeEntry, callback: (Boolean) -> Unit) {
        val timeEntriesCollection = db.collection("timeEntries")
        timeEntriesCollection.document().set(timeEntry)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Update a time entry in Firestore
    fun updateTimeEntry(
        timeEntryId: String,
        updatedTimeEntry: TimeEntry,
        callback: (Boolean) -> Unit
    ) {
        val timeEntryRef = db.collection("timeEntries").document(timeEntryId)
        timeEntryRef.set(updatedTimeEntry)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Delete a time entry from Firestore
    fun deleteTimeEntry(timeEntryId: String, callback: (Boolean) -> Unit) {
        val timeEntryRef = db.collection("timeEntries").document(timeEntryId)
        timeEntryRef.delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
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

    // Add method to save goals within FirestoreRepository
    fun saveUserGoals(
        userId: String,
        minGoal: Double,
        maxGoal: Double,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val goalsData = hashMapOf(
            "minGoal" to minGoal,
            "maxGoal" to maxGoal,
            "timestamp" to System.currentTimeMillis()
        )


        db.collection("users").document(userId).collection("goals").document("current")
            .set(goalsData)
            .addOnSuccessListener {
                Toast.makeText(context, "Goals saved successfully", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Failed to save goals: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                onFailure(exception)
            }
    }
}
    // Add more methods as needed for read, update, delete operations
