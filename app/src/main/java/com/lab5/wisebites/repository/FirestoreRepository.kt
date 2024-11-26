package com.lab5.wisebites.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class FirestoreRepository {
//    var db = Firebase.firestore //TODO: hapus
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun addBookmark(idMeal: Int): Result<String?> {
        val user = auth.currentUser
        return if (user != null) {
            try {
                val userRef = db.collection("users").document(user.uid)

                val userSnapshot = userRef.get().await()
                if (!userSnapshot.exists() || userSnapshot.get("bookmarks") == null) {
                    userRef.set(mapOf("bookmarks" to emptyList<Int>()), SetOptions.merge()).await()
                }

                userRef.update("bookmarks", FieldValue.arrayUnion(idMeal)).await()
                Result.success(null)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("No user is logged in"))
        }
    }

    suspend fun getBookmarkIds(): Result<List<Int>> {
        val user = auth.currentUser
        return if(user != null) {
            try {
                val snapshot = db.collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                val bookmarks = snapshot.get("bookmarks") as? List<Int> ?: emptyList()
                Result.success(bookmarks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("No user is logged in"))
        }
    }

    suspend fun deleteBookmarkById(idMeal: Int): Result<Void?> {
        val user = auth.currentUser
        return if (user != null) {
            try {
                db.collection("users")
                    .document(user.uid)
                    .update("bookmarks", FieldValue.arrayRemove(idMeal))
                    .await()
                Result.success(null)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("No user is logged in"))
        }
    }
}