package com.example.opsc7311_poe

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()

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

    // Add a category for a user
    fun addCategory(userId: String, category: Category, callback: () -> Unit) {
        val categoriesCollection = db.collection("users").document(userId).collection("categories")
        categoriesCollection.document(category.id.toString()).set(category)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreRepository", "Error adding category", e)
                Toast.makeText(context, "Failed to add category: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Get categories for a user
    fun getCategories(userId: String, callback: (List<Category>) -> Unit) {
        val categoriesCollection = db.collection("users").document(userId).collection("categories")
        categoriesCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val categories = querySnapshot.documents.mapNotNull { it.toObject(Category::class.java) }
                callback(categories)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreRepository", "Error getting categories", e)
                Toast.makeText(context, "Failed to get categories: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(emptyList())
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

    fun getTimeSheetEntry(callback: (List<Pair<String, String>>) -> Unit) {
        val timeEntriesCollection = db.collection("timeEntries")

        timeEntriesCollection.get().addOnSuccessListener { querySnapshot ->
            val entriesList = mutableListOf<Pair<String, String>>()
            for (document in querySnapshot.documents) {
                val date = document.getString("date") ?: "Unknown Date"
                val description = document.getString("description") ?: "No Description"
                entriesList.add(Pair(date, description))
            }
            callback(entriesList)
        }.addOnFailureListener { exception ->
            // Handle failure
            Log.e("FirestoreRepository", "Error getting time sheet entries", exception)
            callback(emptyList())
        }
    }

    // Method to get Categories and their respective hours
    fun getCategorySummary(callback: (List<Pair<String, Double>>) -> Unit) {
        val timeEntriesCollection = db.collection("timeEntries")

        timeEntriesCollection.get().addOnSuccessListener { querySnapshot ->
            val categoryMap = mutableMapOf<String, Double>()
            for (document in querySnapshot.documents) {
                val category = document.getString("category") ?: "Uncategorized"
                val duration = document.getDouble("duration") ?: 0.0
                categoryMap[category] = categoryMap.getOrDefault(category, 0.0) + duration
            }
            val categoryList = categoryMap.toList()
            callback(categoryList)
        }.addOnFailureListener { exception ->
            // Handle failure
            Log.e("FirestoreRepository", "Error getting category summary", exception)
            callback(emptyList())
        }
    }
}
