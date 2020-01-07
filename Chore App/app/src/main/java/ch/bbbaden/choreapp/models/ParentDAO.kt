package ch.bbbaden.choreapp.models

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ParentDAO {

    private val db = FirebaseFirestore.getInstance()

    fun getDocumentReference(uid: String) : DocumentReference {
        return db.collection("users").document(uid)
    }

    fun getParent(parentId: String, callback: ((Parent?) -> Unit)? = null) {
        getDocumentReference(parentId)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject(Parent::class.java)
                    callback?.invoke(user)
                } else {
                    callback?.invoke(null)
                }
            }
            .addOnFailureListener {
                callback?.invoke(null)
                Log.e(this::class.simpleName, it.message ?: it.toString())
            }
    }

    fun addParent(parent: Parent, callback: ((success: Boolean) -> Unit)? = null) {
        parent.documentReference
            .set(parent)
            .addOnSuccessListener {
                callback?.invoke(true)
            }
            .addOnFailureListener { e ->
                callback?.invoke(false)
            }
    }

}