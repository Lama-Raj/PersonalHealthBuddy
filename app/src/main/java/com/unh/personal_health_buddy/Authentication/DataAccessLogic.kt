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

object FirestoreHelper {
    @SuppressLint("StaticFieldLeak")
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    // ==================== FIREBASE STORAGE OPERATIONS ====================

    /**
     * Uploads profile image to Firebase Storage.
     * FIX: Path is now "profile_images/{userId}/profile.jpg"
     * 1. Creates a folder for the user ({userId}).
     * 2. Uses a static name "profile.jpg" to ensure it overwrites previous uploads.
     */
    private suspend fun uploadProfileImage(bitmap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()

                // *** THE FIX IS HERE ***
                // This puts the image INSIDE a folder named after the userId.
                // The file is always named "profile.jpg", so it overwrites automatically.
                val imageRef = storageRef.child("profile_images/$userId/profile.jpg")

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                val imageData = baos.toByteArray()

                val metadata = storageMetadata {
                    contentType = "image/jpeg"
                }

                // Upload
                val uploadTask = imageRef.putBytes(imageData, metadata)
                uploadTask.await()

                // Get Download URL
                val downloadUrl = imageRef.downloadUrl.await()

                Log.d("FirestoreHelper", "Profile image uploaded to folder: $downloadUrl")
                downloadUrl.toString()

            } catch (e: StorageException) {
                Log.e("FirestoreHelper", "Storage error: ${e.errorCode} - ${e.message}", e)
                null
            } catch (e: Exception) {
                Log.e("FirestoreHelper", "Error uploading profile image: ${e.message}", e)
                null
            }
        }
    }

    /**
     * Updates profile image.
     * Because we are overwriting "profile.jpg", we don't strictly need to delete the old one
     * if the path is the same. However, if the user previously had a random-ID file,
     * we attempt to delete it to keep the folder clean.
     */
    private suspend fun updateProfileImage(oldImageUrl: String?, newBitmap: Bitmap): String? {
        if (!oldImageUrl.isNullOrEmpty()) {
            try {
                deleteProfileImage(oldImageUrl)
            } catch (e: Exception) {
                // If delete fails (e.g. file name changed), just proceed to upload the new one
            }
        }
        return uploadProfileImage(newBitmap)
    }

    private suspend fun deleteProfileImage(imageUrl: String?): Boolean {
        if (imageUrl.isNullOrEmpty()) return false

        return withContext(Dispatchers.IO) {
            try {
                val imageRef = storage.getReferenceFromUrl(imageUrl)
                imageRef.delete().await()
                Log.d("FirestoreHelper", "Old profile image deleted")
                true
            } catch (e: StorageException) {
                if (e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    true
                } else {
                    Log.e("FirestoreHelper", "Error deleting image: ${e.message}", e)
                    false
                }
            } catch (e: Exception) {
                Log.e("FirestoreHelper", "Error deleting image: ${e.message}", e)
                false
            }
        }
    }

    // ==================== USER OPERATIONS ====================

    suspend fun getUser(userId: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = db.collection("users").document(userId).get().await()
                snapshot.toObject(User::class.java)
            } catch (e: Exception) {
                Log.e("FirestoreHelper", "Error getting user: ${e.message}")
                null
            }
        }
    }

    suspend fun writeUser(user: User, bitmap: Bitmap?) {
        return withContext(Dispatchers.IO) {
            val userId = getCurrentUserId()

            // Get existing user to preserve old image URL if no new one provided
            val existingUser = getUser(userId)
            val oldImageUrl = existingUser?.profileImageUrl

            // Upload new profile image if provided
            val profileImageUrl = if (bitmap != null) {
                updateProfileImage(oldImageUrl, bitmap)
            } else {
                oldImageUrl
            }

            // Update user object with image URL
            val userWithImage = user.copy(profileImageUrl = profileImageUrl)

            // Save to Firestore
            db.collection("users")
                .document(userId)
                .set(userWithImage)
                .await()

            Log.d("FirestoreHelper", "User saved. Image URL: $profileImageUrl")
        }
    }

    // ==================== HELPERS ====================

    fun getVerifiedUser(): Pair<String, String> {
        val user = FirebaseAuth.getInstance().currentUser
        requireNotNull(user?.uid) { "No authenticated user UID found." }
        requireNotNull(user.email) { "Authenticated user has no email." }
        return user.uid to user.email!!
    }

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("No authenticated user")
    }

    // ==================== EMERGENCY CONTACTS ====================

    suspend fun writeEmergencyContact(contact: EmergencyContact) {
        val userId = getCurrentUserId()
        return writeEmergencyContact(contact, userId)
    }

    private suspend fun writeEmergencyContact(contact: EmergencyContact, userId: String) {
        return withContext(Dispatchers.IO) {
            val contactRef = db.collection("users")
                .document(userId)
                .collection("emergencyContacts")
                .document()

            val contactWithId = contact.copy(contactId = contactRef.id)
            contactRef.set(contactWithId).await()
        }
    }

    suspend fun readAllEmergencyContacts(): List<EmergencyContact> {
        return withContext(Dispatchers.IO) {
            val userId = getCurrentUserId()
            val snapshot = db.collection("users")
                .document(userId)
                .collection("emergencyContacts")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(EmergencyContact::class.java)
            }
        }
    }

    suspend fun deleteEmergencyContact(contactId: String) {
        return withContext(Dispatchers.IO) {
            val userId = getCurrentUserId()
            db.collection("users")
                .document(userId)
                .collection("emergencyContacts")
                .document(contactId)
                .delete()
                .await()
        }
    }

    suspend fun updateEmergencyContact(contact: EmergencyContact) {
        val userId = getCurrentUserId()
        return updateEmergencyContact(contact, userId)
    }

    private suspend fun updateEmergencyContact(contact: EmergencyContact, userId: String) {
        return withContext(Dispatchers.IO) {
            db.collection("users")
                .document(userId)
                .collection("emergencyContacts")
                .document(contact.contactId)
                .set(contact)
                .await()
        }
    }

    suspend fun getEmergencyContact(): EmergencyContact? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()
                val snapshot = db.collection("users")
                    .document(userId)
                    .collection("emergencyContacts")
                    .limit(1)
                    .get()
                    .await()
                snapshot.documents.firstOrNull()?.toObject(EmergencyContact::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    // ==================== HEALTH INFORMATION ====================

    suspend fun getHealthInformation(): HealthInformation? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()
                val snapshot = db.collection("users")
                    .document(userId)
                    .collection("healthInformation")
                    .document("info")
                    .get()
                    .await()
                snapshot.toObject(HealthInformation::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun writeHealthInformation(healthInfo: HealthInformation) {
        val userId = getCurrentUserId()
        return writeHealthInformation(healthInfo, userId)
    }

    private suspend fun writeHealthInformation(healthInfo: HealthInformation, userId: String) {
        return withContext(Dispatchers.IO) {
            db.collection("users")
                .document(userId)
                .collection("healthInformation")
                .document("info")
                .set(healthInfo)
                .await()
        }
    }

    // ==================== PRESCRIPTION OPERATIONS ====================

    suspend fun writePrescription(prescription: Prescription) {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()
                val prescriptionRef = db.collection("users")
                    .document(userId)
                    .collection("prescriptions")
                    .document()

                val prescriptionWithId = prescription.copy(id = prescriptionRef.id)
                prescriptionRef.set(prescriptionWithId).await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun readAllPrescriptions(): List<Prescription> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()
                val snapshot = db.collection("users")
                    .document(userId)
                    .collection("prescriptions")
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Prescription::class.java)
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun deletePrescription(prescriptionId: String) {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()
                db.collection("users")
                    .document(userId)
                    .collection("prescriptions")
                    .document(prescriptionId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun deleteAllPrescriptions() {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()
                val prescriptionsSnapshot = db.collection("users")
                    .document(userId)
                    .collection("prescriptions")
                    .get()
                    .await()

                prescriptionsSnapshot.documents.forEach { doc ->
                    doc.reference.delete().await()
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // ==================== BULK UPDATE ====================

    suspend fun updateUserData(
        userId: String,
        updatedUser: User,
        updatedContacts: List<EmergencyContact>,
        updatedHealth: HealthInformation?
    ) {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = getCurrentUserId()
                if (userId != currentUserId) {
                    throw Exception("User ID mismatch")
                }

                // 1. Update User
                db.collection("users")
                    .document(userId)
                    .set(updatedUser)
                    .await()

                // 2. Update Contacts
                updatedContacts.forEach { contact ->
                    if (contact.contactId.isBlank()) {
                        writeEmergencyContact(contact, userId)
                    } else {
                        updateEmergencyContact(contact, userId)
                    }
                }

                // 3. Update Health Info
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
                Log.e("FirestoreHelper", "Error performing bulk update: ${e.message}", e)
                throw e
            }
        }
    }

    // ==================== DELETE ACCOUNT ====================

    suspend fun deleteUserProfileImage() {
        return withContext(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()
                val user = getUser(userId)
                val imageUrl = user?.profileImageUrl

                if (imageUrl != null) {
                    deleteProfileImage(imageUrl)

                    val updatedUser = user.copy(profileImageUrl = null)
                    db.collection("users")
                        .document(userId)
                        .set(updatedUser)
                        .await()

                    Log.d("FirestoreHelper", "User profile image deleted")
                }
            } catch (e: Exception) {
                Log.e("FirestoreHelper", "Error deleting user profile image: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun deleteAllUserData(userId: String) {
        return withContext(Dispatchers.IO) {
            try {
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
                    user?.profileImageUrl?.let { deleteProfileImage(it) }
                } catch (e: Exception) {}

                // 5. Delete user doc
                db.collection("users").document(userId).delete().await()

            } catch (e: Exception) {
                Log.e("FirestoreHelper", "Error deleting user data: ${e.message}", e)
            }
        }
    }

    suspend fun deleteUserAccountWithReauth(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser ?: throw Exception("No authenticated user")
                val credential = EmailAuthProvider.getCredential(email, password)
                user.reauthenticate(credential).await()

                val userId = user.uid
                deleteAllUserData(userId)
                user.delete().await()
                true
            } catch (e: Exception) {
                Log.e("FirestoreHelper", "Error deleting account: ${e.message}", e)
                false
            }
        }
    }
}