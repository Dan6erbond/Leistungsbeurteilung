package ch.bbbaden.choreapp.models

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ChildDAO {

    private val db = FirebaseFirestore.getInstance()

    fun getDocumentReference(userId: String): DocumentReference {
        return db.collection("children").document(userId)
    }

    fun getChild(userId: String, callback: ((Child?) -> Unit)? = null) {
        getDocumentReference(userId)
            .get()
            .addOnSuccessListener { ds ->
                val child = ds.toObject(Child::class.java)
                callback?.invoke(child)
            }
            .addOnFailureListener { e ->
                callback?.invoke(null)
                Log.e(this::class.simpleName, e.message ?: e.toString())
            }
    }

    fun getChildren(parent: Parent, callback: ((ArrayList<Child>?) -> Unit)? = null) {
        db.collection("children").whereEqualTo("parent", parent.documentReference)
            .get()
            .addOnSuccessListener {
                val children = ArrayList<Child>()
                for (document in it.documents) {
                    val child = document.toObject(Child::class.java)
                    children.add(child!!)
                }
                callback?.invoke(children)
            }
            .addOnFailureListener {
                callback?.invoke(null)
                Log.e(this::class.simpleName, it.message ?: it.toString())
            }
    }

    fun saveChild(child: Child, callback: ((success: Boolean) -> Unit)? = null) {
        child.documentReference
            .set(child)
            .addOnSuccessListener {
                callback?.invoke(true)
            }
            .addOnFailureListener {
                callback?.invoke(false)
                Log.e(this::class.simpleName, it.message ?: it.toString())
            }
    }
}