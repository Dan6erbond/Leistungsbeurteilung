package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.zxing.WriterException
import java.util.*

class Chore(
    @DocumentId var id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val assignments: ArrayList<Assignment> = arrayListOf(),
    var parent: DocumentReference? = null
) {

    // TODO: Get rid of this for cleanliness
    @get:Exclude
    var child: Child? = null

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
    private fun getAssignment(): Assignment? {
        for (assignment in assignments) {
            if (assignment.assignedTo?.id == child?.id) {
                return assignment
            }
        }
        return null
    }

    @Exclude
    fun getDisplayTime(): String? {
        getAssignment()?.let { assignment ->
            assignment.getNextDate()?.let {
                return assignment.getDisplayTime(Timestamp(it))
            }
        }
        return null
    }

    override fun equals(other: Any?): Boolean =
        if (other is Chore) other.id == id
        else false

    override fun hashCode(): Int = Objects.hash(id, name, description)

    fun addAssignment(assignment: Assignment, callback: ((success: Boolean) -> Unit)? = null) {
        assignments.add(assignment)
        ChoreDAO().saveChore(this) {
            callback?.invoke(it)
        }
    }

}