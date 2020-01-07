package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.DocumentId
import com.google.zxing.WriterException

data class Parent (
    @DocumentId val userId: String? = null,
    val email: String? = null,
    val first: String? = null,
    val last: String? = null,
    val children: List<String> = arrayListOf(),
    val childrenL: ArrayList<Child> = arrayListOf(),
    val chores: ArrayList<Chore> = arrayListOf()
) {

    fun getQRCode(smallerDimension: Int): Bitmap {
        val content = "parentuid:$userId"
        val qrgEncoder = QRGEncoder(content, null, QRGContents.Type.TEXT, smallerDimension)

        try {
            return qrgEncoder.encodeAsBitmap()
        } catch (e: WriterException) {
            throw e
        }
    }

    fun fetchChildren(callback: ((ArrayList<Child>) -> Unit)? = null) {
        if (childrenL.isEmpty()) {
            ChildDAO().getChildren(this.userId!!) {
                childrenL.addAll(it!!)
                callback?.invoke(childrenL)
            }
        } else {
            callback?.invoke(childrenL)
        }
    }

    fun fetchChores(callback: ((ArrayList<Chore>) -> Unit)? = null) {
        if (chores.isEmpty()) {
            ChoreDAO().getChores(this.userId!!) {
                chores.clear()
                chores.addAll(it!!)
                callback?.invoke(chores)
            }
        } else {
            callback?.invoke(chores)
        }
    }

    fun saveChore(chore: Chore, callback: ((success: Boolean) -> Unit)? = null) {
        ChoreDAO().saveChore(this, chore) {
            if (it) {
                chores.remove(chore)
                chores.add(chore)
            }
            callback?.invoke(it)
        }
    }

    fun addChore(chore: Chore, callback: ((chore: Chore?) -> Unit)? = null) {
        ChoreDAO().addChore(this, chore) {
            if (it != null) chores.add(it)
            callback?.invoke(it)
        }
    }

    fun deleteChore(chore: Chore, callback: ((success: Boolean) -> Unit)? = null) {
        ChoreDAO().deleteChore(this, chore) {
            if (it) chores.remove(chore)
            callback?.invoke(it)
        }
    }
}