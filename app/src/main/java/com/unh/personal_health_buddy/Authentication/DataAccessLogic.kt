package com.unh.personal_health_buddy.authentication

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storageMetadata
import com.unh.personal_health_buddy.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID
/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */
object FirestoreHelper {
    @SuppressLint("StaticFieldLeak")
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    // ==================== MASTER SAVE FUNCTION (THE FIX) ====================

    /**
     * Handles ALL updates: User profile, Image upload, Contacts, and Health Info.
     * Updated to accept 'profileBitmap' (5th argument) to resolve your error.
     */
    suspend fun updateUserData(
        userId: String,
        updatedUser: User,
        updatedContacts: List<EmergencyContact>,
        updatedHealth: HealthInformation?,
        profileBitmap: Bitmap? // <--- Added this 5th parameter
    ) {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = getCurrentUserId()
                if (userId != currentUserId) throw Exception("User ID mismatch")

                // 1. Handle Image Upload FIRST
                var finalUser = updatedUser

                if (profileBitmap != null) {
                    // Upload to: profile_images/{userId}/profile.jpg
                    val newUrl = uploadProfileImage(profileBitmap)

                    // Update user object with the new URL
                    if (newUrl != null) {
                        finalUser = updatedUser.copy(profileImageUrl = newUrl)
                    }
                }

                // 2. Save User Profile
                db.collection("users")
                    .document(userId)
                    .set(finalUser)
                    .await()

                Log.d("FirestoreHelper", "User saved. URL: ${finalUser.profileImageUrl}")

                // 3. Update Emergency Contacts
                updatedContacts.forEach { contact ->
                    if (contact.contactId.isBlank()) {
                        writeEmergencyContact(contact, userId)
                    } else {
                        updateEmergencyContact(contact, userId)
                    }
                }

                // 4. Update Health Information
                if (updatedHealth != null) {
                    writeHealthInformation(updatedHealth, userId)
                } else {
                    db.collection("users")
                        .document(userId)
                        .collection("healthInformation")
                        .document("info")
                        .delete()
                        .await()
                }

            } catch (e: Exception) {
                Log.e("FirestoreHelper", "Error in bulk update: ${e.message}", e)
                throw e
            }
        }
    }

    // ==================== STORAGE OPERATIONS ====================

    private suspend fun uploadProfileImage(bitmap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()

                // *** FOLDER STRUCTURE FIX ***
                // Creates a folder named {userId} and puts 'profile.jpg' inside it.
                val imageRef = storageRef.child("profile_images/$userId/profile.jpg")

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                val imageData = baos.toByteArray()

                val metadata = storageMetadata { contentType = "image/jpeg" }

                // Overwrite the file
                imageRef.putBytes(imageData, metadata).await()

                val downloadUrl = imageRef.downloadUrl.await()
                Log.d("FirestoreHelper", "Uploaded: $downloadUrl")
                downloadUrl.toString()

            } catch (e: Exception) {
                Log.e("FirestoreHelper", "Upload failed: ${e.message}", e)
                null
            }
        }
    }

    // ==================== UTILITIES ====================

    fun getVerifiedUser(): Pair<String, String> {
        val user = FirebaseAuth.getInstance().currentUser
        requireNotNull(user?.uid)
        return user.uid to user.email!!
    }

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("No authenticated user")
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) { null }
    }

    // --- Emergency Contacts ---
    suspend fun writeEmergencyContact(contact: EmergencyContact) {
        writeEmergencyContact(contact, getCurrentUserId())
    }

    private suspend fun writeEmergencyContact(contact: EmergencyContact, userId: String) {
        val ref = db.collection("users").document(userId).collection("emergencyContacts").document()
        ref.set(contact.copy(contactId = ref.id)).await()
    }

    suspend fun updateEmergencyContact(contact: EmergencyContact) {
        updateEmergencyContact(contact, getCurrentUserId())
    }

    private suspend fun updateEmergencyContact(contact: EmergencyContact, userId: String) {
        db.collection("users").document(userId).collection("emergencyContacts").document(contact.contactId).set(contact).await()
    }

    suspend fun readAllEmergencyContacts(): List<EmergencyContact> {
        return try {
            val uid = getCurrentUserId()
            val snap = db.collection("users").document(uid).collection("emergencyContacts").get().await()
            snap.toObjects(EmergencyContact::class.java)
        } catch (e: Exception) { emptyList() }
    }

    suspend fun deleteEmergencyContact(contactId: String) {
        db.collection("users").document(getCurrentUserId()).collection("emergencyContacts").document(contactId).delete().await()
    }

    // --- Health Info ---
    suspend fun getHealthInformation(): HealthInformation? {
        return try {
            val uid = getCurrentUserId()
            val snap = db.collection("users").document(uid).collection("healthInformation").document("info").get().await()
            snap.toObject(HealthInformation::class.java)
        } catch (e: Exception) { null }
    }

    suspend fun writeHealthInformation(healthInfo: HealthInformation) {
        writeHealthInformation(healthInfo, getCurrentUserId())
    }

    private suspend fun writeHealthInformation(healthInfo: HealthInformation, userId: String) {
        db.collection("users").document(userId).collection("healthInformation").document("info").set(healthInfo).await()
    }

    // --- Prescriptions ---
    suspend fun writePrescription(prescription: Prescription) {
        val userId = getCurrentUserId()
        val ref = db.collection("users").document(userId).collection("prescriptions").document()
        ref.set(prescription.copy(id = ref.id)).await()
    }

    suspend fun readAllPrescriptions(): List<Prescription> {
        val userId = getCurrentUserId()
        val snap = db.collection("users").document(userId).collection("prescriptions").get().await()
        return snap.toObjects(Prescription::class.java)
    }

    suspend fun deletePrescription(id: String) {
        db.collection("users").document(getCurrentUserId()).collection("prescriptions").document(id).delete().await()
    }

    suspend fun deleteAllPrescriptions() {
        val userId = getCurrentUserId()
        val snap = db.collection("users").document(userId).collection("prescriptions").get().await()
        snap.forEach { it.reference.delete() }
    }

    // --- Delete Logic ---
    suspend fun deleteUserProfileImage() {
        val userId = getCurrentUserId()
        val user = getUser(userId)

        user?.profileImageUrl?.let { url ->
            try { storage.getReferenceFromUrl(url).delete().await() } catch(e: Exception){}
        }

        val updatedUser = user?.copy(profileImageUrl = null)
        if (updatedUser != null) {
            db.collection("users").document(userId).set(updatedUser).await()
        }
    }

    // Keep writeUser for legacy calls if needed, but simple one
    suspend fun writeUser(user: User, bitmap: Bitmap?) {
        // Just redirect to master update with empty lists
        updateUserData(getCurrentUserId(), user, emptyList(), null, bitmap)
    }

    suspend fun deleteAllUserData(userId: String) {
        val currentUserId = getCurrentUserId()
        if (userId != currentUserId) throw Exception("User ID mismatch")

        // 1. Delete contacts
        try {
            val contacts = db.collection("users").document(userId).collection("emergencyContacts").get().await()
            contacts.documents.forEach { it.reference.delete().await() }
        } catch (e: Exception) {}

        // 2. Delete health info
        try {
            db.collection("users").document(userId).collection("healthInformation").document("info").delete().await()
        } catch (e: Exception) {}

        // 3. Delete prescriptions
        try {
            val scripts = db.collection("users").document(userId).collection("prescriptions").get().await()
            scripts.documents.forEach { it.reference.delete().await() }
        } catch (e: Exception) {}

        // 4. Delete profile image
        try {
            val user = getUser(userId)
            user?.profileImageUrl?.let {
                storage.getReferenceFromUrl(it).delete().await()
            }
        } catch (e: Exception) {}

        // 5. Delete folder contents
        try {
            storageRef.child("profile_images/$userId/profile.jpg").delete().await()
        } catch (e: Exception) {}

        // 6. Delete user doc
        db.collection("users").document(userId).delete().await()
    }

    suspend fun deleteUserAccountWithReauth(email: String, p: String): Boolean {
        val user = auth.currentUser ?: return false
        val cred = EmailAuthProvider.getCredential(email, p)
        user.reauthenticate(cred).await()

        deleteAllUserData(user.uid)
        user.delete().await()
        return true
    }
}