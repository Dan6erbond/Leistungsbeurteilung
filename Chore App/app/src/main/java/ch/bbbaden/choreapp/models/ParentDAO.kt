package ch.bbbaden.choreapp.models

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ParentDAO {

    private val db = FirebaseFirestore.getInstance()

    fun getParent(parentId: String, callback: ((Parent?) -> Unit)? = null) {
        db.collection("users").document(parentId)
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
        db.collection("users").document(parent.userId!!)
            .set(parent) // use Collection.add() if document should have a generated id
            .addOnSuccessListener {
                callback?.invoke(true)
            }
            .addOnFailureListener { e ->
                callback?.invoke(false)
            }
    }

}