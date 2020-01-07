package ch.bbbaden.choreapp.models

import android.text.format.DateUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import java.text.SimpleDateFormat
import java.util.*

data class Assignment(
    var assignedTo: DocumentReference? = null,
    val repeat: Repeat? = null,
    val startDate: Timestamp? = null
) {

    @Exclude
    fun getNextDate(): Date? {
        val currentDate = Calendar.getInstance().time
        val nextDate = startDate!!.toDate()
        val repeat = repeat

        when {
            nextDate.after(currentDate) -> {
                return nextDate
            }
            repeat != null -> {
                val value = repeat.value!!
                when (repeat.unit!!) {
                    "day" -> return addToDate(nextDate, currentDate, Calendar.DATE, value)
                    "week" -> return addToDate(nextDate, currentDate, Calendar.DATE, value * 7)
                    "month" -> return addToDate(nextDate, currentDate, Calendar.MONTH, value)
                    "year" -> return addToDate(nextDate, currentDate, Calendar.YEAR, value)
                }
                return null
            }
            else -> {
                return null
            }
        }
    }

    @Exclude
    fun getDisplayTime(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val dateTimeFormat = SimpleDateFormat.getDateTimeInstance()
        val timeFormat = SimpleDateFormat.getTimeInstance()
        return if (DateUtils.isToday(date.time))
            "Today @ ${timeFormat.format(date)}" else dateTimeFormat.format(date)
    }

    private fun addToDate(date: Date, afterDate: Date, unit: Int, value: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        while (calendar.time.before(afterDate)) {
            calendar.add(unit, value)
        }
        return calendar.time
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Assignment

        if (assignedTo != other.assignedTo) return false
        if (repeat != other.repeat) return false
        if (startDate != other.startDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = assignedTo?.hashCode() ?: 0
        result = 31 * result + (repeat?.hashCode() ?: 0)
        result = 31 * result + startDate.hashCode()
        return result
    }

}