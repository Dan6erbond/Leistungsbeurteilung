package ch.bbbaden.choreapp.models

import android.graphics.Bitmap
import android.text.format.DateUtils
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.firebase.firestore.DocumentId
import com.google.zxing.WriterException
import java.text.SimpleDateFormat
import java.util.*

class Chore(
    @DocumentId val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val assignments: ArrayList<Assignment> = arrayListOf()
) {

    var childId: String? = null

    fun getQRCode(smallerDimension: Int): Bitmap {
        val content = "choreid:$id"
        val qrgEncoder = QRGEncoder(content, null, QRGContents.Type.TEXT, smallerDimension)

        try {
            return qrgEncoder.encodeAsBitmap()
        } catch (e: WriterException) {
            throw e
        }
    }

    fun getNextDate(): Date? {
        for (assignment in assignments) {
            if (assignment.assignedTo == childId) {
                return assignment.getNextDate()
            }
        }
        return null
    }

    fun getDisplayTime(): String? {
        val dateTimeFormat = SimpleDateFormat.getDateTimeInstance()
        val timeFormat = SimpleDateFormat.getTimeInstance()
        val nextDate = getNextDate()
        return if (nextDate != null) {
            if (DateUtils.isToday(nextDate.time))
                timeFormat.format(nextDate) else dateTimeFormat.format(nextDate)
        } else {
            null
        }
    }

    override fun equals(other: Any?): Boolean =
        if (other is Chore) other.id == id
        else false

    override fun hashCode(): Int = Objects.hash(id, name, description)

}