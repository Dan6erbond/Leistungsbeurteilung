package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.DocumentId
import com.google.zxing.WriterException

data class Child(
    @DocumentId val userId: String? = null,
    val first: String? = null,
    val last: String? = null,
    var parentId: String? = null,
    val chores: ArrayList<Chore> = arrayListOf()
) {

    var parent: Parent? = null
        set(value) {
            field = value
            parentId = value?.userId
        }


    fun getQRCode(smallerDimension: Int): Bitmap {
        val content = "childuid:$userId"
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
            callback?.invoke(it)
        }
    }

    fun fetchParent(callback: ((Parent?) -> Unit)? = null) {
        ParentDAO().getParent(parentId!!, callback)
    }

}