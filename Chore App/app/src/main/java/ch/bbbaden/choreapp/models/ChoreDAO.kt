package ch.bbbaden.choreapp.models

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ChoreDAO {

    private val db = FirebaseFirestore.getInstance()

    fun getDocumentReference(choreId: String): DocumentReference {
        return db.collection("chores").document(choreId)
    }

    fun getChores(child: Child, callback: ((ArrayList<Chore>?) -> Unit)? = null) {
        getChores(child.parent?.id!!) {
            if (it != null) {
                val chores = ArrayList<Chore>()
                for (chore in it) {
                    for (assignment in chore.assignments) {
                        val assignedTo = assignment.assignedTo?.id == child.id
                        val nextAssignmentExists = assignment.getNextDate() != null
                        if (assignedTo && nextAssignmentExists) {
                            chores.add(chore)
                            break
                        }
                    }
                }
                callback?.invoke(chores)
            } else {
                callback?.invoke(null)
            }
        }
    }

    fun getChore(choreId: String, callback: ((chore: Chore?) -> Unit)? = null) {
        getDocumentReference(choreId)
            .get()
            .addOnSuccessListener {
                val chore = it.toObject(Chore::class.java)
                callback?.invoke(chore!!)
            }
            .addOnFailureListener {
                callback?.invoke(null)
                Log.e(this::class.simpleName, it.message ?: it.toString())
            }
    }

    fun getChores(userId: String, callback: ((ArrayList<Chore>?) -> Unit)? = null) {
        db.collection("chores").whereEqualTo("parent", ParentDAO().getDocumentReference(userId))
            .get()
            .addOnSuccessListener {
                val chores = ArrayList<Chore>()
                for (document in it) {
                    val chore = document.toObject(Chore::class.java)
                    chores.add(chore)
                }
                callback?.invoke(chores)
            }
            .addOnFailureListener {
                callback?.invoke(null)
                Log.e(this::class.simpleName, it.message ?: it.toString())
            }
    }

    fun saveChore(chore: Chore, callback: ((success: Boolean) -> Unit)? = null) {
        chore.documentReference
            .set(chore)
            .addOnSuccessListener {
                callback?.invoke(true)
            }
            .addOnFailureListener {
                callback?.invoke(false)
                Log.e(this::class.simpleName, it.message ?: it.toString())
            }
    }

    fun addChore(chore: Chore, callback: ((chore: Chore?) -> Unit)? = null) {
        db.collection("chores")
            .add(chore)
            .addOnSuccessListener {
                chore.id = it.id
                callback?.invoke(chore)
            }
            .addOnFailureListener {
                callback?.invoke(null)
                Log.e(this::class.simpleName, it.message ?: it.toString())
            }
    }

    fun deleteChore(chore: Chore, callback: ((success: Boolean) -> Unit)? = null) {
        getDocumentReference(chore.id!!)
            .delete()
            .addOnSuccessListener {
                callback?.invoke(true)
            }
            .addOnFailureListener {
                callback?.invoke(false)
                Log.e(this::class.simpleName, it.message ?: it.toString())
            }
    }

}