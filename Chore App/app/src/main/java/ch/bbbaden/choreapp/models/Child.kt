package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.zxing.WriterException

data class Child(
    @DocumentId val id: String? = null,
    val first: String? = null,
    val last: String? = null,
    var parent: DocumentReference? = null
) {

    @get:Exclude
    val chores: ArrayList<Chore> = arrayListOf()

    @get:Exclude
    val documentReference: DocumentReference
        get() {
            return ChildDAO().getDocumentReference(id!!)
        }

    @Exclude
    fun getQRCode(smallerDimension: Int): Bitmap {
        val content = "childuid:$id"
        val qrgEncoder = QRGEncoder(content, null, QRGContents.Type.TEXT, smallerDimension)

        try {
            return qrgEncoder.encodeAsBitmap()
        } catch (e: WriterException) {
            throw e
        }
    }

    fun fetchChores(callback: ((ArrayList<Chore>) -> Unit)? = null) {
        if (chores.isEmpty()) {
            ChoreDAO().getChores(this) {
                chores.clear()
                chores.addAll(it!!)
                callback?.invoke(it)
            }
        } else {
            callback?.invoke(chores)
        }
    }

    fun fetchParent(callback: ((Parent?) -> Unit)? = null) {
        ParentDAO().getParent(parent!!.id, callback)
    }

}