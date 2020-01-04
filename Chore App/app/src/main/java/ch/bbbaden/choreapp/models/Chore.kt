package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.DocumentId
import com.google.zxing.WriterException
import java.io.Serializable
import java.util.*

class Chore(
    @DocumentId var id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val assignments: ArrayList<Assignment> = arrayListOf()
) : Serializable {

    var child: Child? = null

    fun getQRCode(smallerDimension: Int): Bitmap {
        val content = "choreid:$id"
        val qrgEncoder = QRGEncoder(content, null, QRGContents.Type.TEXT, smallerDimension)

        try {
            return qrgEncoder.encodeAsBitmap()
        } catch (e: WriterException) {
            throw e
        }
    }

    private fun getAssignment(): Assignment? {
        for (assignment in assignments) {
            if (assignment.assignedTo == child?.userId) {
                return assignment
            }
        }
        return null
    }

    fun getDisplayTime(): String? {
        getAssignment()?.let { assignment ->
            assignment.getNextDate()?.let {
                return assignment.getDisplayTime(it)
            }
        }
        return null
    }

    override fun equals(other: Any?): Boolean =
        if (other is Chore) other.id == id
        else false

    override fun hashCode(): Int = Objects.hash(id, name, description)

    fun getData(): HashMap<String, *> {
        return hashMapOf(
            "name" to name,
            "description" to description,
            "assignments" to assignments.map { it.getData() }
        )
    }

    fun addAssignment(assignment: Assignment, parent: Parent, callback: ((success: Boolean) -> Unit)? = null) {
        assignments.add(assignment)
        ChoreDAO().saveChore(parent, this) {
            callback?.invoke(it)
        }
    }

}