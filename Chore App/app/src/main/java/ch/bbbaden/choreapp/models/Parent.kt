package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.zxing.WriterException

data class Parent(
    @DocumentId val id: String? = null,
    val email: String? = null,
    val first: String? = null,
    val last: String? = null
) {

    private val children: ArrayList<Child> = arrayListOf()

    private val chores: ArrayList<Chore> = arrayListOf()

    @Exclude
    val undeletedChores: ArrayList<Chore> = arrayListOf()

    fun updateUndeletedChores() {
        undeletedChores.clear()
        undeletedChores.addAll(chores.filter { chore -> !chore.deleted })
    }

    @get:Exclude
    val documentReference: DocumentReference
        get() {
            return ParentDAO().getDocumentReference(id!!)
        }

    @Exclude
    fun getQRCode(smallerDimension: Int): Bitmap {
        val content = "parentuid:$id"
        val qrgEncoder = QRGEncoder(content, null, QRGContents.Type.TEXT, smallerDimension)

        try {
            return qrgEncoder.encodeAsBitmap()
        } catch (e: WriterException) {
            throw e
        }
    }

    fun fetchChildren(callback: ((ArrayList<Child>) -> Unit)? = null) {
        if (children.isEmpty()) {
            ChildDAO().getChildren(this) {
                children.addAll(it!!)
                callback?.invoke(children)
            }
        } else {
            callback?.invoke(children)
        }
    }

    fun fetchChores(callback: ((ArrayList<Chore>) -> Unit)? = null) {
        if (chores.isEmpty()) {
            ChoreDAO().getChores(this.id!!) {
                chores.clear()
                chores.addAll(it!!)
                callback?.invoke(chores)
            }
        } else {
            callback?.invoke(chores)
        }
    }

    fun saveChild(child: Child, callback: ((success: Boolean) -> Unit)? = null) {
        ChildDAO().saveChild(child) {
            if (it) {
                children.remove(child)
                children.add(child)
            }
            callback?.invoke(it)
        }
    }

    fun addChild(child: Child, callback: ((child: Child?) -> Unit)? = null) {
        child.parent = documentReference
        ChildDAO().addChild(child) {
            if (it != null) children.add(it)
            callback?.invoke(it)
        }
    }

    fun deleteChild(child: Child, callback: ((success: Boolean) -> Unit)? = null) {
        ChildDAO().deleteChild(child) {
            if (it) children.remove(child)
            callback?.invoke(it)
        }
    }

    fun saveChore(chore: Chore, callback: ((success: Boolean) -> Unit)? = null) {
        ChoreDAO().saveChore(chore) {
            if (it) {
                chores.remove(chore)
                chores.add(chore)
            }
            callback?.invoke(it)
        }
    }

    fun addChore(chore: Chore, callback: ((chore: Chore?) -> Unit)? = null) {
        chore.parent = documentReference
        ChoreDAO().addChore(chore) {
            if (it != null) chores.add(it)
            callback?.invoke(it)
        }
    }

    fun deleteChore(chore: Chore, callback: ((success: Boolean) -> Unit)? = null) {
        chore.deleted = true
        ChoreDAO().saveChore(chore) {
            if (it) {
                chores.remove(chore)
                chores.add(chore)
            }
            callback?.invoke(it)
        }
    }

    @Exclude
    fun getChore(choreId: String, callback: ((chore: Chore?) -> Unit)? = null) {
        fetchChores {
            var found = false
            for (chore in it) {
                if (chore.id == choreId) {
                    found = true
                    callback?.invoke(chore)
                }
            }
            if (!found) callback?.invoke(null)
        }
    }

    @Exclude
    fun getChild(childId: String, callback: ((child: Child?) -> Unit)? = null) {
        fetchChildren {
            var found = false
            for (child in it) {
                if (child.id == childId) {
                    found = true
                    callback?.invoke(child)
                }
            }
            if (!found) callback?.invoke(null)
        }
    }
}