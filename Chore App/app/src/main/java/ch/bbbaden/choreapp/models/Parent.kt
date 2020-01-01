package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.DocumentId
import com.google.zxing.WriterException
import java.io.Serializable

data class Parent (
    @DocumentId val userId: String? = null,
    val email: String? = null,
    val first: String? = null,
    val last: String? = null,
    val children: List<String> = arrayListOf(),
    val childrenL: ArrayList<Child> = arrayListOf(),
    val chores: ArrayList<Chore> = arrayListOf()
) : Serializable {

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
            ChildDAO().getChildren(userId!!) {
                childrenL.addAll(it!!)
                callback?.invoke(childrenL)
            }
        } else {
            callback?.invoke(childrenL)
        }
    }

    fun fetchChores(callback: ((ArrayList<Chore>) -> Unit)? = null) {
        if (chores.isEmpty()) {
            ChoreDAO().getChores(userId!!) {
                chores.addAll(it!!)
                callback?.invoke(chores)
            }
        } else {
            callback?.invoke(chores)
        }
    }

    fun addChore(chore: Chore, callback: ((Chore) -> Unit)? = null) {

    }
}