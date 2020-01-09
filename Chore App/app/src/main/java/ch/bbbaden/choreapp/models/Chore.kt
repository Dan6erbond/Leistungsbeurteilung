package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.zxing.WriterException
import java.util.*

class Chore(
    @DocumentId var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    val assignments: ArrayList<Assignment> = arrayListOf(),
    var parent: DocumentReference? = null,
    var deleted: Boolean = false
) {

    @get:Exclude
    val documentReference: DocumentReference
        get() {
            return ChoreDAO().getDocumentReference(id!!)
        }

    @Exclude
    fun getQRCode(smallerDimension: Int): Bitmap {
        val content = "choreid:$id"
        val qrgEncoder = QRGEncoder(content, null, QRGContents.Type.TEXT, smallerDimension)

        try {
            return qrgEncoder.encodeAsBitmap()
        } catch (e: WriterException) {
            throw e
        }
    }

    @Exclude
    fun getAssignment(child: Child): Assignment? {
        for (assignment in assignments) {
            if (assignment.assignedTo?.id == child?.id) {
                return assignment
            }
        }
        return null
    }

    fun addAssignment(assignment: Assignment, callback: ((success: Boolean) -> Unit)? = null) {
        assignments.add(assignment)
        ChoreDAO().saveChore(this) {
            callback?.invoke(it)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chore

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

}