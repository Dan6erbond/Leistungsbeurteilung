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
    var parent: DocumentReference? = null,
    var completedChores: ArrayList<CompletedChore> = arrayListOf()
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
        ChoreDAO().getChores(this) {
            chores.clear()
            chores.addAll(it!!)
            callback?.invoke(chores)
        }
    }

    fun fetchParent(callback: ((Parent?) -> Unit)? = null) {
        ParentDAO().getParent(parent!!.id, callback)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Child

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

}